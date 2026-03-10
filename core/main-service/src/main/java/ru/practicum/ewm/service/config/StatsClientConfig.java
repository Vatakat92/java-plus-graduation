package ru.practicum.ewm.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.client.props.ClientProperties;

@Configuration
@EnableConfigurationProperties(ClientProperties.class)
public class StatsClientConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(3000L);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    @Bean
    public StatsClient statsClient(RestTemplateBuilder builder,
                                   ClientProperties props,
                                   @Value("${spring.application.name:ewm-service}") String appName,
                                   DiscoveryClient discoveryClient,
                                   RetryTemplate retryTemplate,
                                   @Value("${stats.service-id:stats-service}") String statsServiceId) {
        return new StatsClient(builder, props, appName, discoveryClient, retryTemplate, statsServiceId);
    }
}