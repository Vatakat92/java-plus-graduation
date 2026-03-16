package ru.practicum.controller.external;

import java.util.List;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.external.CategoryDto;
import ru.practicum.service.CategoryService;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated

public class PublicCategoriesController {

    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> list(@RequestParam(defaultValue="0") @PositiveOrZero int from,
                                  @RequestParam(defaultValue="10") @Positive int size) {
        return service.list(PageRequest.ofSize(size).withPage(from / size));
    }

    @GetMapping("/{categoryId}")
    public CategoryDto get(@PathVariable Long categoryId) {
        return service.get(categoryId);
    }
}
