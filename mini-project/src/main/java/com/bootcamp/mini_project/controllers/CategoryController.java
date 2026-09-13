package com.bootcamp.mini_project.controllers;

import com.bootcamp.mini_project.dto.categories.*;
import com.bootcamp.mini_project.dto.common.*;
import com.bootcamp.mini_project.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Controller providing REST API operations for managing product categories.
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categories Management", description = "CRUD operations for product categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new product category")
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("[API REST] Creating category with name: {}", request.getName());
        CategoryResponse createdCategory = categoryService.createCategory(request);

        return ApiResponse.<CategoryResponse>builder()
                .success(true)
                .message("Category created successfully")
                .data(createdCategory)
                .build();
    }

    @GetMapping
    @Operation(summary = "Retrieve paginated list of all categories")
    public PageResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        log.info("[API REST] Fetching categories page: {}", pageable.getPageNumber());
        return categoryService.getAllCategories(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category details by unique ID")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
        log.info("[API REST] Fetching category ID: {}", id);
        CategoryResponse response = categoryService.getCategoryById(id);

        return ApiResponse.<CategoryResponse>builder()
                .success(true)
                .message("Category retrieved successfully")
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category by ID")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        log.info("[API REST] Updating category ID: {}", id);
        CategoryResponse response = categoryService.updateCategory(id, request);

        return ApiResponse.<CategoryResponse>builder()
                .success(true)
                .message("Category updated successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category by ID")
    public ApiResponse<String> deleteCategory(@PathVariable Long id) {
        log.info("[API REST] Deleting category ID: {}", id);
        categoryService.deleteCategory(id);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Category deleted successfully")
                .data(null)
                .build();
    }
}