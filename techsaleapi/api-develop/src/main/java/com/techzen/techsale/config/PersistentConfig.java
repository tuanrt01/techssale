package com.techzen.techsale.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@RequiredArgsConstructor
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class PersistentConfig {

    private final AuditorAwareImpl auditorAware;

    @Bean
    public AuditorAware<String> auditorProvider() {
        return auditorAware;
    }
}