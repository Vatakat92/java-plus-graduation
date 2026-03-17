package ru.practicum.dto.external;

import java.util.List;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationDto {

    private List<Long> events;
    private Boolean pinned;

    @Size(max = 50, message = "The title length must be between 1 and 50 characters.")
    private String title;
}