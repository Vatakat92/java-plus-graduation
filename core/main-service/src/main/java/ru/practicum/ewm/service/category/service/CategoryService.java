package ru.practicum.ewm.service.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.category.dto.NewCategoryDto;
import ru.practicum.ewm.service.category.dto.UpdateCategoryRequest;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto dto);

    CategoryDto update(long id, UpdateCategoryRequest dto);

    void delete(long id);

    List<CategoryDto> list(Pageable pageable);

    CategoryDto get(long id);
}
