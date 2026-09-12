package com.bootcamp.mini_project.dto.transactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

/**
 * Data transfer object for complete transaction details response.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;

    private String transactionCode;

    private BigDecimal totalAmount;

    private LocalDateTime transactionDate;

    private List<TransactionItemResponse> details;
}