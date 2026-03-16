package ru.practicum.dto.external;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEventDto {
    @Size(min=20,max=2000)
    @NotBlank
    private String annotation;

    @NotNull
    private Long category;

    @Size(min=20, max=7000)
    @NotBlank
    private String description;

    @NotNull
    private LocationDto location;
    private boolean paid = false;

    @Min(0)
    private int participantLimit = 0;

    private boolean requestModeration = true;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private LocalDateTime eventDate;

    @Size(min = 3, max = 120, message = "Title length must be 3..120")
    @NotBlank
    private String title;
}
