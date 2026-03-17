package ru.practicum.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.external.CategoryDto;
import ru.practicum.dto.internal.CategoryShortDto;
import ru.practicum.dto.external.NewCategoryDto;
import ru.practicum.dto.external.UpdateCategoryRequest;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.integration.event.EventServiceGateway;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventServiceGateway eventServiceGateway;

    @Transactional
    public CategoryDto add(NewCategoryDto dto) {
        return CategoryMapper.toDto(
                categoryRepository.save(
                        Category.builder()
                                .name(dto.getName())
                                .build()
                )
        );
    }

    @Transactional
    public CategoryDto update(Long id, UpdateCategoryRequest dto) {
        Category c = categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        "Category with id=" + id + " was not found."
                )
        );
        c.setName(dto.getName());
        return CategoryMapper.toDto(c);
    }

    @Transactional
    public void delete(Long id){
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(
                    "Category with id=" + id + " was not found."
            );
        }
        Boolean hasEvents = eventServiceGateway.existsByCategoryId(id);
        if (hasEvents) {
            throw new ConflictException(
                    "Category with id=" + id + " is linked to events."
            );
        }
        categoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> list(Pageable p) {
        return categoryRepository.findAll(p)
                .map(CategoryMapper::toDto)
                .getContent();
    }

    @Transactional(readOnly = true)
    public CategoryDto get(Long id) {
        return CategoryMapper.toDto(
                categoryRepository.findById(id).orElseThrow(
                        () -> new NotFoundException(
                                "Category with id=" + id + " was not found."
                        )
                )
        );
    }

    @Transactional(readOnly = true)
    public CategoryShortDto getCategoryById(Long categoryId) {
        return CategoryMapper.toShortDto(
                categoryRepository.findById(categoryId).orElseThrow(
                        () -> new NotFoundException(
                                "Category with id=" + categoryId + " was not found."
                        )
                )
        );
    }

    @Transactional(readOnly = true)
    public Map<Long, CategoryShortDto> getCategoriesByIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return categoryRepository.findAllById(categoryIds).stream()
                .collect(Collectors.toMap(
                        Category::getId,
                        CategoryMapper::toShortDto
                ));
    }
}
