package com.bootcamp.rafi_day_2.service;

import com.bootcamp.rafi_day_2.dto.categories.*;
import com.bootcamp.rafi_day_2.entity.Categories;
import com.bootcamp.rafi_day_2.repository.CategoryRepository;
import com.bootcamp.rafi_day_2.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private CategoryResponse mapToResponse(Categories categories) {
        return CategoryResponse
                .builder()
                .id(categories.getId())
                .name(categories.getName())
                .description(categories.getDescription())
                .build();
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        log.info("Starting to create category with name: {}", categoryRequest.getName());

        Categories category = new Categories();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        Categories savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getId());

        return mapToResponse(savedCategory);
    }

    public List<CategoryResponse> getAllCategories() {
        log.info("Fetching all categories from database.");

        List<CategoryResponse> categories = categoryRepository.findAll().stream().map(this::mapToResponse).toList();
        log.info("Successfully fetched {} categories.", categories.size());
        return categories;
    }

    public CategoryResponse getCategoryById(Long id) {
        log.info("Fetching category with ID: {}", id);

        Categories categories = categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Fetch failed: Category with ID {} not found.", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");
        });

        log.info("Category found: {}", categories.getName());
        return mapToResponse(categories);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        log.info("Starting update process for category ID: {}", id);

        Categories categories = categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Update failed: Category with ID {} not found.", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");
        });

        categories.setName(categoryRequest.getName());
        categories.setDescription(categoryRequest.getDescription());

        Categories updatedCategory = categoryRepository.save(categories);
        log.info("Category with ID: {} updated successfully.", updatedCategory.getId());

        return mapToResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Attempting to delete category with ID: {}", id);

        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");
        }

        if (productRepository.existsByCategoryId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot delete category because it is still referenced by products."
            );
        }

        categoryRepository.deleteById(id);
        log.info("Category with ID: {} deleted successfully.", id);
    }
}