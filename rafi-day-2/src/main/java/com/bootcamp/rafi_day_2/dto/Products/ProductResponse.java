package com.bootcamp.rafi_day_2.dto.Products;

import java.math.BigDecimal;
import lombok.*;

/**
 * Data Transfer Object (DTO) representing the product details sent back to the client.
 * It contains the mapped product data retrieved from the database.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Long categoryId;
}