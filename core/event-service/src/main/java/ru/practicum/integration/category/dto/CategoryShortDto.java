package ru.practicum.integration.category.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryShortDto {
    private Long id;
    private String name;
}
