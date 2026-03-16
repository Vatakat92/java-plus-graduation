package ru.practicum.integration.request.fallback;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.integration.request.RequestServiceClient;
import ru.practicum.integration.request.dto.BatchCountEventIdAndStatus;
import ru.practicum.integration.request.dto.RequestStatus;
import ru.practicum.integration.request.exceptions.RequestServiceUnavailableException;

@Component
@Slf4j
public class RequestServiceClientFallbackFactory implements FallbackFactory<RequestServiceClient> {

    @Override
    public RequestServiceClient create(Throwable cause) {
        return new RequestServiceClient() {

            @Override
            public Map<Long, Long> getCountByEventIdsAndStatus(BatchCountEventIdAndStatus batchRequest) {
                log.warn("Request service is unavailable. batchRequest={}, cause={}",
                        batchRequest, cause.toString());
                throw new RequestServiceUnavailableException("Request service is unavailable.", cause);
            }

            @Override
            public Long getCountByEventIdAndStatus(Long eventId, RequestStatus status) {
                log.warn("Request service is unavailable. eventId={}, status={}, cause={}",
                        eventId, status, cause.toString());
                throw new RequestServiceUnavailableException("Request service is unavailable.", cause);
            }
        };
    }
}