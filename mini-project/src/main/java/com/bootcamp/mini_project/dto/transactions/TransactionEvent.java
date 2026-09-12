package com.bootcamp.mini_project.dto.transactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

/**
 * Data transfer object published to Kafka topics representing a created transaction event.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionEvent {
    private String transactionCode;

    private BigDecimal totalAmount;

    private LocalDateTime transactionDate;

    private List<TransactionItemRequest> items;
}