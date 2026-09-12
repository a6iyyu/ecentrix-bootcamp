package com.bootcamp.mini_project.dto.reports;

import com.bootcamp.mini_project.entity.StockLog;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Data transfer object for stock change audit logs.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockLogResponse {
    private Long logId;

    private Long productId;

    private String productName;

    private StockLog.StockChangeType changeType;

    private Integer quantity;

    private Integer previousStock;

    private Integer currentStock;

    private String remark;

    private LocalDateTime createdAt;
}