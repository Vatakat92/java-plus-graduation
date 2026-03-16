package ru.practicum.mapper;

import ru.practicum.dto.external.CategoryDto;
import ru.practicum.dto.internal.CategoryShortDto;
import ru.practicum.model.Category;

public final class CategoryMapper {
    private CategoryMapper() {}

    public static CategoryDto toDto(Category c) {
        return CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
    }

    public static CategoryShortDto toShortDto(Category c) {
        return CategoryShortDto.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
    }
}
