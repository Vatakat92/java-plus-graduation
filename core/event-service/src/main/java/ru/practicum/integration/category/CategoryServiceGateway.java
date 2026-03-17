package ru.practicum.integration.category;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ServiceUnavailableException;
import ru.practicum.integration.category.dto.CategoryIdsBatchRequest;
import ru.practicum.integration.category.dto.CategoryShortDto;
import ru.practicum.integration.category.exceptions.CategoryNotFoundException;
import ru.practicum.integration.category.exceptions.CategoryServiceUnavailableException;

@Component
@RequiredArgsConstructor
public class CategoryServiceGateway {

    private final CategoryServiceClient categoryClient;

    public Map<Long, CategoryShortDto> getCategoriesByIds(List<Long> categoryIds) {

        if (categoryIds == null || categoryIds.isEmpty()) {
            return Map.of();
        }

        try {
            return categoryClient.getCategoriesByIds(new CategoryIdsBatchRequest(categoryIds));
        } catch (CategoryServiceUnavailableException e) {
            throw new ServiceUnavailableException("Category service is temporarily unavailable");
        }
    }

    public CategoryShortDto getCategoryById(Long categoryId) {
        try {
            return categoryClient.getCategoryById(categoryId);
        } catch (CategoryNotFoundException e) {
            throw new NotFoundException("Category with ids=" + categoryId + " was not found");
        } catch (CategoryServiceUnavailableException e) {
            throw new ServiceUnavailableException("Category service is temporarily unavailable");
        }
    }
}