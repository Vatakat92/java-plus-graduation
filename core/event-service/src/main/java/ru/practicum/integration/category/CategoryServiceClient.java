package ru.practicum.integration.category;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.integration.category.config.CategoryServiceClientConfig;
import ru.practicum.integration.category.dto.CategoryIdsBatchRequest;
import ru.practicum.integration.category.dto.CategoryShortDto;
import ru.practicum.integration.category.fallback.CategoryServiceClientFallbackFactory;

@FeignClient(
    name = "category-service",
    path = "/internal/category",
    configuration = CategoryServiceClientConfig.class,
    fallbackFactory = CategoryServiceClientFallbackFactory.class
)

public interface CategoryServiceClient {
    @PostMapping("/by-ids")
    Map<Long, CategoryShortDto> getCategoriesByIds(@RequestBody CategoryIdsBatchRequest categoryIds);

    @GetMapping("/by-id/{categoryId}")
    CategoryShortDto getCategoryById(@PathVariable Long categoryId);
}
