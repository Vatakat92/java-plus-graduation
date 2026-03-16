package ru.practicum.dto.external;

import lombok.Builder;
import lombok.Data;
import ru.practicum.integration.event.dto.EnrichedEventSummaryDto;
import java.util.List;

@Data
@Builder
public class CompilationFullDto {
    private Long id;
    private List<EnrichedEventSummaryDto> events;
    private Boolean pinned;
    private String title;
}