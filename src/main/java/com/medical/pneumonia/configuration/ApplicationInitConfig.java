package com.medical.pneumonia.configuration;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.medical.pneumonia.repository.RoleRepository;
import com.medical.pneumonia.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    @Bean
    public ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {

        //     if (userRepository.findByUsername("admin").isEmpty()) {

        //         Role adminRole = roleRepository.findById("ADMIN")
        //                 .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        //         Set<Role> roles = new HashSet<>();
        //         roles.add(adminRole);

        //         User user = User.builder()
        //                 .username("admin")
        //                 .password(passwordEncoder.encode("admin123@"))
        //                 .roles(roles)
        //                 .build();

        //         userRepository.save(user);

        //         log.info("Admin user created");
        //     }
        };
    }
}