package ru.practicum.dto.external;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentFullDto {
    private Long id;
    private String text;
    private Long commentatorId;
    private Long eventId;
    private LocalDateTime publishedOn;
    private boolean deleted;
}
