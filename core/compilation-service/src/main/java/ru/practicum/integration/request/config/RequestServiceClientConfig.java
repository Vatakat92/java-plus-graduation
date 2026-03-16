package ru.practicum.integration.request.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.integration.request.fallback.RequestServiceErrorDecoder;

@Configuration
public class RequestServiceClientConfig {

    @Bean
    public ErrorDecoder requestClientErrorDecoder() {
        return new RequestServiceErrorDecoder();
    }

    @Bean
    public Retryer requestClientRetryer() {
        return Retryer.NEVER_RETRY;
    }
}
