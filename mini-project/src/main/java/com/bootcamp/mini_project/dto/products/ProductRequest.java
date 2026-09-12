package com.bootcamp.mini_project.dto.products;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;

/**
 * Data transfer object for creating or updating a product.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = "Name of the product is required.")
    @Size(max = 255, message = "Name must not exceed 255 characters.")
    private String name;

    @NotNull(message = "Category ID is required.")
    private Long categoryId;

    private Long supplierId;

    @Size(max = 2000, message = "Description must not exceed 2000 characters.")
    private String description;

    @NotNull(message = "Price is required.")
    @DecimalMin(inclusive = false, message = "Price must be greater than zero.", value = "0.0")
    private BigDecimal price;

    @NotNull(message = "Stock is required.")
    private Integer stock;
}