package ru.practicum.controller.external;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.external.CategoryDto;
import ru.practicum.dto.external.NewCategoryDto;
import ru.practicum.dto.external.UpdateCategoryRequest;
import ru.practicum.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoriesController {

    private final CategoryService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public CategoryDto add(@Valid @RequestBody NewCategoryDto dto) {
        return service.add(dto);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto update(@PathVariable Long categoryId,
                              @Valid @RequestBody UpdateCategoryRequest dto) {
        return service.update(categoryId, dto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long categoryId) {
        service.delete(categoryId);
    }
}
