package com.bootcamp.mini_project.controllers;

import com.bootcamp.mini_project.dto.common.*;
import com.bootcamp.mini_project.dto.products.*;
import com.bootcamp.mini_project.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Controller providing REST API operations for managing inventory products.
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products Management", description = "CRUD operations for inventory products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create a new product")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        log.info("[API REST] Creating product with name: {}", request.getName());
        ProductResponse response = productService.createProduct(request);

        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Product created successfully")
                .data(response)
                .build();
    }

    @GetMapping
    @Operation(summary = "Get all products with pagination")
    public PageResponse<ProductResponse> getAllProducts(Pageable pageable) {
        log.info("[API REST] Fetching products page: {}", pageable.getPageNumber());
        return productService.getAllProducts(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product details by unique ID")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
        log.info("[API REST] Fetching product ID: {}", id);
        ProductResponse response = productService.getProductById(id);

        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Product retrieved successfully")
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product details by ID")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        log.info("[API REST] Updating product ID: {}", id);
        ProductResponse updatedProduct = productService.updateProduct(id, request);

        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Product updated successfully")
                .data(updatedProduct)
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product by ID")
    public ApiResponse<ProductResponse> deleteProduct(@PathVariable Long id) {
        log.info("[API REST] Deleting product ID: {}", id);
        productService.deleteProduct(id);

        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Product deleted successfully")
                .data(null)
                .build();
    }
}