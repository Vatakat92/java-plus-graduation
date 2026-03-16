package ru.practicum.integration.event.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSummaryDto {
    private Long id;
    private String annotation;
    private Long category;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Long initiator;
    private Boolean paid;
    private String title;
    private EventState state;
    private Integer participantLimit;
    private Boolean requestModeration;
}
