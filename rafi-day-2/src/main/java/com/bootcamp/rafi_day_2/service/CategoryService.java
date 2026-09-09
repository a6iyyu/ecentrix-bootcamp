package com.bootcamp.rafi_day_2.service;

import com.bootcamp.rafi_day_2.dto.Categories.*;
import com.bootcamp.rafi_day_2.entity.Categories;
import com.bootcamp.rafi_day_2.repository.CategoryRepository;
import com.bootcamp.rafi_day_2.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

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
        logger.info("Starting to create category with name: {}", categoryRequest.getName());

        Categories category = new Categories();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        Categories savedCategory = categoryRepository.save(category);
        logger.info("Category created successfully with ID: {}", savedCategory.getId());

        return mapToResponse(savedCategory);
    }

    public List<CategoryResponse> getAllCategories() {
        logger.info("Fetching all categories from database.");

        List<CategoryResponse> categories = categoryRepository.findAll().stream().map(this::mapToResponse).toList();
        logger.info("Successfully fetched {} categories.", categories.size());
        return categories;
    }

    public CategoryResponse getCategoryById(Long id) {
        logger.info("Fetching category with ID: {}", id);

        Categories categories = categoryRepository.findById(id).orElseThrow(() -> {
            logger.warn("Fetch failed: Category with ID {} not found.", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");
        });

        logger.info("Category found: {}", categories.getName());
        return mapToResponse(categories);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        logger.info("Starting update process for category ID: {}", id);

        Categories categories = categoryRepository.findById(id).orElseThrow(() -> {
            logger.warn("Update failed: Category with ID {} not found.", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");
        });

        categories.setName(categoryRequest.getName());
        categories.setDescription(categoryRequest.getDescription());

        Categories updatedCategory = categoryRepository.save(categories);
        logger.info("Category with ID: {} updated successfully.", updatedCategory.getId());

        return mapToResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        logger.info("Attempting to delete category with ID: {}", id);

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
        logger.info("Category with ID: {} deleted successfully.", id);
    }
}