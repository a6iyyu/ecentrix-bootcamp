package com.bootcamp.mini_project.mappers;

import com.bootcamp.mini_project.dto.categories.*;
import com.bootcamp.mini_project.entity.Category;
import org.springframework.stereotype.Component;

/**
 * Handles data mapping between {@link Category} entity and its corresponding DTOs.
 */
@Component
public class CategoryMapper {
    /**
     * Converts a {@link CategoryRequest} DTO into a new {@link Category} entity.
     *
     * @param request the request DTO source
     * @return mapped {@link Category} entity instance, or {@code null} if source is null
     */
    public Category toEntity(CategoryRequest request) {
        if (request == null) {
            return null;
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return category;
    }

    /**
     * Converts a {@link Category} entity into a {@link CategoryResponse} DTO.
     *
     * @param category the entity source
     * @return mapped {@link CategoryResponse} instance, or {@code null} if source is null
     */
    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}