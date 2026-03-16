package ru.practicum.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.RequestStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestShortDto {
    private Long requestId;
    private RequestStatus status;
}