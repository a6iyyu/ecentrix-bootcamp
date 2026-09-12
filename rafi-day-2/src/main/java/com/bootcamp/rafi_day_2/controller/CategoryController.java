package com.bootcamp.rafi_day_2.controller;

import com.bootcamp.rafi_day_2.dto.common.ApiResponse;
import com.bootcamp.rafi_day_2.dto.categories.*;
import com.bootcamp.rafi_day_2.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Categories Management")
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "Create a new category.")
    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse createdCategory = categoryService.createCategory(categoryRequest);

        return ApiResponse.<CategoryResponse>builder()
                .message("Category successfully created.")
                .data(createdCategory)
                .build();
    }

    @Operation(summary = "Delete category by ID.")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);

        return ApiResponse.<String>builder()
                .message("Category successfully deleted.")
                .data(null)
                .build();
    }

    @Operation(summary = "Get all categories.")
    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> category = categoryService.getAllCategories();

        return ApiResponse.<List<CategoryResponse>>builder()
                .message("Successfully fetching all categories.")
                .data(category)
                .build();
    }

    @Operation(summary = "Get category by ID.")
    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);

        return ApiResponse.<CategoryResponse>builder()
                .message("Successfully fetching category with ID: " + id)
                .data(category)
                .build();
    }

    @Operation(summary = "Update category by ID.")
    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse updatedCategory = categoryService.updateCategory(id, categoryRequest);

        return ApiResponse.<CategoryResponse>builder()
                .message("Category successfully updated.")
                .data(updatedCategory)
                .build();
    }
}