package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.response.MenuResponse;
import com.medical.pneumonia.entity.Menu;
import com.medical.pneumonia.entity.Permission;
import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.repository.MenuRepository;
import com.medical.pneumonia.repository.PermissionRepository;
import com.medical.pneumonia.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MenuService {

  MenuRepository menuRepository;
  UserRepository userRepository;
  PermissionRepository permissionRepository;

  @Cacheable(value = "menus", key = "#username")
  public List<MenuResponse> getMyMenus(String username) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));

    List<Permission> allPermissions = permissionRepository.findAll();
    Set<String> expandedPermissions;

    if (isAdmin) {
      expandedPermissions =
          allPermissions.stream().map(Permission::getName).collect(Collectors.toSet());
    } else {
      Set<String> userDirectPermissions =
          user.getRoles().stream()
              .flatMap(role -> role.getPermissions().stream())
              .map(Permission::getName)
              .collect(Collectors.toSet());

      Map<String, List<String>> childMap =
          allPermissions.stream()
              .filter(p -> p.getParentName() != null)
              .collect(
                  Collectors.groupingBy(
                      Permission::getParentName,
                      Collectors.mapping(Permission::getName, Collectors.toList())));

      Map<String, String> parentMap =
          allPermissions.stream()
              .filter(p -> p.getParentName() != null)
              .collect(
                  Collectors.toMap(Permission::getName, Permission::getParentName, (a, b) -> a));

      expandedPermissions = new HashSet<>();
      java.util.Queue<String> queue = new java.util.LinkedList<>(userDirectPermissions);

      while (!queue.isEmpty()) {
        String p = queue.poll();
        if (expandedPermissions.add(p)) {
          // Expand downwards
          queue.addAll(childMap.getOrDefault(p, java.util.Collections.emptyList()));
          // Expand upwards
          String parent = parentMap.get(p);
          if (parent != null) {
            queue.add(parent);
          }
        }
      }
    }

    List<Menu> allMenus = menuRepository.findAllByOrderBySortOrderAsc();

    List<Menu> allowedMenus =
        allMenus.stream()
            .filter(
                menu -> {
                  if (menu.getPermissionCode() == null
                      || menu.getPermissionCode().trim().isEmpty()) {
                    return true;
                  }
                  return java.util.Arrays.stream(menu.getPermissionCode().split(","))
                      .map(String::trim)
                      .anyMatch(expandedPermissions::contains);
                })
            .toList();

    return buildMenuTree(allowedMenus);
  }

  private List<MenuResponse> buildMenuTree(List<Menu> menus) {
    Map<Long, List<Menu>> childrenMap =
        menus.stream()
            .filter(m -> m.getParentId() != null)
            .collect(Collectors.groupingBy(Menu::getParentId));

    List<Menu> rootMenus = menus.stream().filter(m -> m.getParentId() == null).toList();

    return rootMenus.stream()
        .map(menu -> mapToMenuResponse(menu, childrenMap))
        .filter(
            response ->
                response.getUrl() != null
                    || (response.getItems() != null && !response.getItems().isEmpty()))
        .collect(Collectors.toList());
  }

  private MenuResponse mapToMenuResponse(Menu menu, Map<Long, List<Menu>> childrenMap) {
    List<Menu> children = childrenMap.getOrDefault(menu.getId(), new ArrayList<>());

    List<MenuResponse> items =
        children.stream()
            .map(child -> mapToMenuResponse(child, childrenMap))
            .filter(
                response ->
                    response.getUrl() != null
                        || (response.getItems() != null && !response.getItems().isEmpty()))
            .collect(Collectors.toList());

    return MenuResponse.builder()
        .id(menu.getId())
        .title(menu.getTitle())
        .icon(menu.getIcon())
        .url(menu.getUrl())
        .permissionCode(menu.getPermissionCode())
        .items(items.isEmpty() ? null : items)
        .build();
  }

  public String getCurrentUsername() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null ? authentication.getName() : "anonymous";
  }
}
