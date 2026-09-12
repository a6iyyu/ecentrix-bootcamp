package com.bootcamp.mini_project.mappers;

import com.bootcamp.mini_project.dto.products.*;
import com.bootcamp.mini_project.entity.Product;
import org.springframework.stereotype.Component;

/**
 * Handles data mapping between {@link Product} entity and its corresponding DTOs.
 */
@Component
public class ProductMapper {
    /**
     * Converts a {@link ProductRequest} DTO into a {@link Product} entity.
     * Note: Category and Supplier entities must be fetched and assigned in the service layer.
     *
     * @param request the request DTO source
     * @return mapped {@link Product} entity instance, or {@code null} if source is null
     */
    public Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        return product;
    }

    /**
     * Converts a {@link Product} entity into a {@link ProductResponse} DTO.
     * Includes null checks for optional relationships like Supplier.
     *
     * @param product the entity source
     * @return mapped {@link ProductResponse} instance, or {@code null} if source is null
     */
    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .supplierId(product.getSupplier() != null ? product.getSupplier().getId() : null)
                .supplierName(product.getSupplier() != null ? product.getSupplier().getName() : null)
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}