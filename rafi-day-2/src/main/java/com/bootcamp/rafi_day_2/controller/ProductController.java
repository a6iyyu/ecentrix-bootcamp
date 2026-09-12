package com.bootcamp.rafi_day_2.controller;

import com.bootcamp.rafi_day_2.dto.common.ApiResponse;
import com.bootcamp.rafi_day_2.dto.products.*;
import com.bootcamp.rafi_day_2.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Products Management")
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Operation(summary = "Create a new product.")
    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        ProductResponse createdProduct = productService.createProduct(productRequest);

        return ApiResponse.<ProductResponse>builder()
                .message("Product successfully created.")
                .data(createdProduct)
                .build();
    }

    @Operation(summary = "Delete product by ID.")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);

        return ApiResponse.<String>builder()
                .message("Product successfully deleted.")
                .data(null)
                .build();
    }

    @Operation(summary = "Get all products with pagination and optional category filter.")
    @GetMapping
    public ApiResponse<Page<ProductResponse>> getAllProducts(
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ProductResponse> products = productService.getAllProducts(categoryId, pageable);

        return ApiResponse.<Page<ProductResponse>>builder()
                .message("Successfully fetching products.")
                .data(products)
                .build();
    }

    @Operation(summary = "Get product by ID.")
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);

        return ApiResponse.<ProductResponse>builder()
                .message("Successfully fetching product with ID: " + id)
                .data(product)
                .build();
    }

    @Operation(summary = "Update product by ID.")
    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
        ProductResponse updatedProduct = productService.updateProduct(id, productRequest);

        return ApiResponse.<ProductResponse>builder()
                .message("Product successfully updated.")
                .data(updatedProduct)
                .build();
    }
}