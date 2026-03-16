package ru.practicum.integration.event.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.integration.event.fallback.EventServiceErrorDecoder;

@Configuration
public class EventServiceClientConfig {

    @Bean
    public ErrorDecoder eventClientErrorDecoder() {
        return new EventServiceErrorDecoder();
    }

    @Bean
    public Retryer eventClientRetryer() {
        return Retryer.NEVER_RETRY;
    }
}
