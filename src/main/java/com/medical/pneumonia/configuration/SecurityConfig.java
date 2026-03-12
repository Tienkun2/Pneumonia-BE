package com.medical.pneumonia.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

  private static final String[] PUBLIC_ENDPOINTS = {"/auth/**"};

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
                    // Register user
                    .requestMatchers(HttpMethod.POST, "/users")
                    .permitAll()

                    // Auth APIs
                    .requestMatchers(PUBLIC_ENDPOINTS)
                    .permitAll()

                    // Admin APIs
                    .requestMatchers(ADMIN_ENDPOINTS)
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        // Custom verify JWT (Get token from Authorization header, check signature, check
        // expiration)
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
    // Auto verify JWT (Get token from Authorization header, check signature, check expiration)
    // .oauth2ResourceServer(oauth2 -> oauth2.jwt());

    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
        new JwtGrantedAuthoritiesConverter();
    jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

    return converter;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }
}
