package ru.practicum.integration.request;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.integration.request.config.RequestServiceClientConfig;
import ru.practicum.integration.request.dto.BatchCountEventIdAndStatus;
import ru.practicum.integration.request.fallback.RequestServiceClientFallbackFactory;

@FeignClient(
    name = "request-service",
    path = "/internal/requests",
    configuration = RequestServiceClientConfig.class,
    fallbackFactory = RequestServiceClientFallbackFactory.class
)

public interface RequestServiceClient {
    @PostMapping("/count-by-ids")
    Map<Long, Long> getCountByEventIdsAndStatus(@RequestBody BatchCountEventIdAndStatus requesterIds);
}
