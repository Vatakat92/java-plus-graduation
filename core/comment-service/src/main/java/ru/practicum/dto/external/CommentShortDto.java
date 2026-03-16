package ru.practicum.dto.external;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentShortDto {
    private String text;
    private Long commentatorId;
    private LocalDateTime publishedOn;
}
