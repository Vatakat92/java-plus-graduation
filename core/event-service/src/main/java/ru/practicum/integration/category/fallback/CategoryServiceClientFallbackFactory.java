package ru.practicum.integration.category.fallback;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.integration.category.CategoryServiceClient;
import ru.practicum.integration.category.dto.CategoryIdsBatchRequest;
import ru.practicum.integration.category.dto.CategoryShortDto;
import ru.practicum.integration.category.exceptions.CategoryServiceUnavailableException;

@Component
@Slf4j
public class CategoryServiceClientFallbackFactory implements FallbackFactory<CategoryServiceClient> {

    @Override
    public CategoryServiceClient create(Throwable cause) {
        return new CategoryServiceClient() {

            @Override
            public Map<Long, CategoryShortDto> getCategoriesByIds(CategoryIdsBatchRequest categoryIds) {
                log.warn("Category service is unavailable. categoryIds={}, cause={}",
                        categoryIds.ids(), cause.toString());
                throw new CategoryServiceUnavailableException("Category service is unavailable.", cause);
            }

            @Override
            public CategoryShortDto getCategoryById(Long categoryId) {
                log.warn("Category service is unavailable. categoryId={}, cause={}",
                        categoryId, cause.toString());
                throw new CategoryServiceUnavailableException("Category service is unavailable.", cause);
            }
        };
    }
}