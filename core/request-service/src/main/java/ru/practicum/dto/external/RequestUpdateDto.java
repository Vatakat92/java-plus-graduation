package ru.practicum.dto.external;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.RequestStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestUpdateDto {
    private List<Long> requestIds;
    private RequestStatus status;
}