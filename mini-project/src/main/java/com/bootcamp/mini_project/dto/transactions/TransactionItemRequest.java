package com.bootcamp.mini_project.dto.transactions;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Data transfer object representing a product item entry in a transaction request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionItemRequest {
    @NotNull(message = "Product ID is required.")
    private Long productId;

    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity;
}