package com.bootcamp.mini_project.dto.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

/**
 * Data transfer object for product response details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private Long id;

    private String name;

    private Long categoryId;

    private String categoryName;

    private Long supplierId;

    private String supplierName;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}