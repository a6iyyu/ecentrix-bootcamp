package com.bootcamp.rafi_day_2.dto.products;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;

/**
 * Data Transfer Object (DTO) for handling product creation and update requests.
 * It includes validation rules to ensure incoming product data is correct.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = "Name is required.")
    @Size(max = 255, message = "Name must not exceed 255 characters.")
    private String name;

    @Size(max = 5000, message = "Description must not exceed 5000 characters.")
    private String description;

    @NotNull(message = "Price is required.")
    @PositiveOrZero(message = "Price must be greater than or equal to zero.")
    private BigDecimal price;

    @NotNull(message = "Stock is required.")
    @PositiveOrZero(message = "Stock must be greater than or equal to zero.")
    private Integer stock;

    @NotNull(message = "Category ID is required.")
    @PositiveOrZero(message = "Category ID must be greater than or equal to zero.")
    private Long categoryId;
}