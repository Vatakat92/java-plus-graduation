package ru.practicum.integration.category.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.integration.category.fallback.CategoryServiceErrorDecoder;

@Configuration
public class CategoryServiceClientConfig {

    @Bean
    public ErrorDecoder eventClientErrorDecoder() {
        return new CategoryServiceErrorDecoder();
    }

    @Bean
    public Retryer eventClientRetryer() {
        return Retryer.NEVER_RETRY;
    }
}
