package ru.practicum.mapper;

import java.util.List;
import ru.practicum.dto.external.CompilationFullDto;
import ru.practicum.integration.event.dto.EnrichedEventSummaryDto;
import ru.practicum.model.Compilation;

public final class CompilationMapper {
    private CompilationMapper() {}

    public static CompilationFullDto toFullDto(Compilation compilation, List<EnrichedEventSummaryDto> events) {
        return CompilationFullDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(events)
                .build();
    }
}
