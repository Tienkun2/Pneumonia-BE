package com.medical.pneumonia.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ApplicationInitConfig {

  @Bean
  public ApplicationRunner applicationRunner() {
    return args -> {
      log.info("Application initialized successfully.");
    };
  }
}
