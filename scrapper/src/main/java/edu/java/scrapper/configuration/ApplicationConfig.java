package edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    @Bean
    Scheduler scheduler,
    @NotNull
    @Bean
    LinkCheckProperties linkCheckProperties,
    @NotNull
    @Bean
    AccessType databaseAccessType
) {
    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record LinkCheckProperties(@NotNull Duration linkCheckInterval) {
    }

    public enum AccessType {
        JDBC, JPA,
    }
}
