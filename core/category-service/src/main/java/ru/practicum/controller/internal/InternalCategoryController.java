package ru.practicum.controller.internal;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.internal.CategoryIdsBatchRequest;
import ru.practicum.dto.internal.CategoryShortDto;
import ru.practicum.service.CategoryService;

@RestController
@RequestMapping("/internal/category")
@RequiredArgsConstructor
public class InternalCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/by-id/{categoryId}")
    public CategoryShortDto getCategoryById(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    @PostMapping("/by-ids")
    public Map<Long, CategoryShortDto> getCategoriesByIds(@RequestBody CategoryIdsBatchRequest categoryIds){
        return categoryService.getCategoriesByIds(categoryIds.ids());
    }
}
