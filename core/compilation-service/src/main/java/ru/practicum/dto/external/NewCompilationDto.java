package ru.practicum.dto.external;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCompilationDto {

    @Builder.Default
    private List<Long> events = new ArrayList<>();

    @Builder.Default
    private Boolean pinned = false;

    @NotBlank(message = "Title must not be blank")
    @Size(min = 1, max = 50, message = "The title length must be between 1 and 50 characters.")
    private String title;
}