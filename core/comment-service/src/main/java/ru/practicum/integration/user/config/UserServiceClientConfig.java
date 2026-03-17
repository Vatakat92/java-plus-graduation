package ru.practicum.integration.user.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.integration.user.fallback.UserServiceErrorDecoder;

@Configuration
public class UserServiceClientConfig {

    @Bean
    public ErrorDecoder userClientErrorDecoder() {
        return new UserServiceErrorDecoder();
    }

    @Bean
    public Retryer userClientRetryer() {
        return Retryer.NEVER_RETRY;
    }
}
