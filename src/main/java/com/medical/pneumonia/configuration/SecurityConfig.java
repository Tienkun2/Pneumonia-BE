package com.medical.pneumonia.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINT = {
        "/users",
        "/auth/login"
    };

    private final String[] ADMIN_ENDPOINT = {
        "/users/**",
        "/permissions/**",
        "/roles/**"
    };

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(request -> 
            request.requestMatchers(HttpMethod.POST,PUBLIC_ENDPOINT).permitAll()
            .requestMatchers(ADMIN_ENDPOINT).permitAll()
            .anyRequest().authenticated()
        )
        // Custom verify JWT (Get token from Authorization header, check signature, check expiration)
        .oauth2ResourceServer(oauth2 -> 
            oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
            .jwtAuthenticationConverter(jwtAuthenticationConverter()))
            .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );
        // Auto verify JWT (Get token from Authorization header, check signature, check expiration)
        // .oauth2ResourceServer(oauth2 -> oauth2.jwt());

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder(){
        SecretKeySpec secretKey = new SecretKeySpec(signerKey.getBytes(),"HS512");
        return NimbusJwtDecoder
        .withSecretKey(secretKey)
        .macAlgorithm(MacAlgorithm.HS512)
        .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}
