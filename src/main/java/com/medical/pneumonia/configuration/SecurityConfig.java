package com.medical.pneumonia.configuration;

import com.medical.pneumonia.entity.Permission;
import com.medical.pneumonia.repository.PermissionRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  @Autowired CustomJwtDecoder customJwtDecoder;
  @Autowired PermissionRepository permissionRepository;

  private static final String[] PUBLIC_ENDPOINTS = {"/auth/**", "/users/set-password", "/ws/**"};
  private static final String[] ADMIN_ENDPOINTS = {"/users/**", "/roles/**", "/permissions/**"};

  @Value("${jwt.signerKey}")
  private String signerKey;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> {})
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            request ->
                request
                    .requestMatchers(PUBLIC_ENDPOINTS)
                    .permitAll()
                    .requestMatchers(ADMIN_ENDPOINTS)
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2
                    .jwt(
                        jwtConfigurer ->
                            jwtConfigurer
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                    .accessDeniedHandler(new JwtAccessDeniedHandler()));

    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
        new JwtGrantedAuthoritiesConverter();
    jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(
        jwt -> {
          var authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
          if (authorities == null) return Collections.emptyList();

          Set<String> direct =
              authorities.stream().map(auth -> auth.getAuthority()).collect(Collectors.toSet());

          // BFS Expansion (Cha đẻ ra Con)
          List<Permission> all = permissionRepository.findAll();
          Map<String, List<String>> childMap =
              all.stream()
                  .filter(p -> p.getParentName() != null)
                  .collect(
                      Collectors.groupingBy(
                          Permission::getParentName,
                          Collectors.mapping(Permission::getName, Collectors.toList())));

          Set<String> expanded = new HashSet<>();
          Queue<String> queue = new LinkedList<>(direct);
          while (!queue.isEmpty()) {
            String p = queue.poll();
            if (expanded.add(p)) {
              List<String> children = childMap.getOrDefault(p, Collections.emptyList());
              queue.addAll(children);
            }
          }

          return expanded.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        });

    return converter;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }
}
