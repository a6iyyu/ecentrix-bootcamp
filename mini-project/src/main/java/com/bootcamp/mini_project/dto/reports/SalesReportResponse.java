package com.bootcamp.mini_project.dto.reports;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

/**
 * Data transfer object for sales report data items.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesReportResponse {
    private Long transactionId;

    private String transactionCode;

    private BigDecimal totalAmount;

    private Integer totalItems;

    private LocalDateTime transactionDate;
}