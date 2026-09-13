package com.bootcamp.mini_project.services;

import com.bootcamp.mini_project.dto.categories.*;
import com.bootcamp.mini_project.dto.common.PageResponse;
import com.bootcamp.mini_project.entity.Category;
import com.bootcamp.mini_project.exceptions.custom.ResourceNotFoundException;
import com.bootcamp.mini_project.mappers.CategoryMapper;
import com.bootcamp.mini_project.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles business logic and caching operations for product categories.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    /**
     * Retrieves a paginated list of all active categories mapped to response DTOs.
     *
     * @param pageable pagination parameters including page index, size, and sorting
     * @return a {@link PageResponse} wrapping category response items
     */
    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        log.debug("[CATEGORY] Fetching paginated categories page: {}", pageable.getPageNumber());
        return PageResponse.from(categoryRepository.findAll(pageable).map(categoryMapper::toResponse));
    }

    /**
     * Retrieves a specific category by its unique identifier.
     * Caches the result in Redis under the 'categories' cache space using ID as the key.
     *
     * @param id the unique category identifier
     * @return the corresponding {@link CategoryResponse} DTO
     * @throws ResourceNotFoundException if no category is found with the given ID
     */
    @Cacheable(value = "categories", key = "#id")
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        log.info("[CACHE MISS] Fetching category from database for ID: {}", id);

        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("[CATEGORY NOT FOUND] Read operation failed. Category ID {} does not exist.", id);
            return new ResourceNotFoundException("Category not found with ID: " + id);
        });

        return categoryMapper.toResponse(category);
    }

    /**
     * Creates a new product category record in the system.
     *
     * @param request payload containing new category details
     * @return the created {@link CategoryResponse} DTO
     * @throws IllegalArgumentException if a category with the same name already exists
     */
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            log.warn("[CATEGORY DUPLICATE] Creation failed. Name '{}' already exists.", request.getName());
            throw new IllegalArgumentException("Category name already exists: " + request.getName());
        }

        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);
        log.info("[CATEGORY] Successfully created category with ID: {}", savedCategory.getId());

        return categoryMapper.toResponse(savedCategory);
    }

    /**
     * Updates an existing category record and invalidates its corresponding entry in Redis cache.
     *
     * @param id      the unique identifier of the category to update
     * @param request payload containing updated category details
     * @return the updated {@link CategoryResponse} DTO
     * @throws ResourceNotFoundException if target category is not found
     */
    @CacheEvict(value = "categories", key = "#id")
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("[CATEGORY NOT FOUND] Update operation failed. Category ID {} does not exist.", id);
            return new ResourceNotFoundException("Category not found with ID: " + id);
        });

        String oldName = category.getName();

        if (!oldName.equalsIgnoreCase(request.getName()) && categoryRepository.existsByName(request.getName())) {
            log.warn("[CATEGORY DUPLICATE] Update failed. Name '{}' already exists.", request.getName());
            throw new IllegalArgumentException("Category name already exists: " + request.getName());
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        log.info("[CATEGORY] Updated category ID: {} and evicted cache entry.", id);
        return categoryMapper.toResponse(updatedCategory);
    }

    /**
     * Soft-deletes a category by its ID and removes its entry from the Redis cache.
     *
     * @param id the unique category identifier
     * @throws ResourceNotFoundException if target category is not found
     */
    @CacheEvict(value = "categories", key = "#id")
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("[CATEGORY NOT FOUND] Delete operation failed. Category ID {} does not exist.", id);
            return new ResourceNotFoundException("Category not found with ID: " + id);
        });

        categoryRepository.delete(category);
        log.info("[CATEGORY] Soft-deleted category ID: {} and evicted cache entry.", id);
    }
}