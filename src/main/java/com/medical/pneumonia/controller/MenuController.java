package com.medical.pneumonia.controller;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.response.MenuResponse;
import com.medical.pneumonia.service.MenuService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menus")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MenuController {

  MenuService menuService;

  @GetMapping("/me")
  public ApiResponse<List<MenuResponse>> getMyMenus() {
    return ApiResponse.<List<MenuResponse>>builder()
        .message("Get menu successfully")
        .result(menuService.getMyMenus())
        .build();
  }
}
