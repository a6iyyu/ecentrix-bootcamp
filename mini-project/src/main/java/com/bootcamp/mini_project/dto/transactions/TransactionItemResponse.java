package com.bootcamp.mini_project.dto.transactions;

import lombok.*;

import java.math.BigDecimal;

/**
 * Data transfer object for detailed item responses within a transaction.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionItemResponse {
    private Long id;

    private Long productId;

    private String productName;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subtotal;
}