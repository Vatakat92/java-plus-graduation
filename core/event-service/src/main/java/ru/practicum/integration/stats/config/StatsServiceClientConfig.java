package ru.practicum.integration.stats.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.integration.stats.fallback.StatsServiceErrorDecoder;

@Configuration
public class StatsServiceClientConfig {

    @Bean
    public ErrorDecoder statsClientErrorDecoder() {
        return new StatsServiceErrorDecoder();
    }

    @Bean
    public Retryer statsClientRetryer() {
        return Retryer.NEVER_RETRY;
    }
}
