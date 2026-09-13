package com.bootcamp.mini_project.services;

import com.bootcamp.mini_project.dto.common.PageResponse;
import com.bootcamp.mini_project.dto.reports.*;
import com.bootcamp.mini_project.entity.TransactionDetail;
import com.bootcamp.mini_project.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles business logic for generating sales analytics and stock audit reports.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
    private final StockLogRepository stockLogRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Retrieves a paginated sales report mapping transaction details and total item counts.
     *
     * @param pageable pagination configuration
     * @return paginated {@link SalesReportResponse} items
     */
    @Transactional(readOnly = true)
    public PageResponse<SalesReportResponse> getSalesReport(Pageable pageable) {
        log.debug("[REPORT] Fetching paginated sales report page: {}", pageable.getPageNumber());

        return PageResponse.from(transactionRepository.findAll(pageable).map(transaction -> {
            int totalItems = transaction.getDetails() == null
                    ? 0
                    : transaction.getDetails()
                        .stream()
                        .mapToInt(TransactionDetail::getQuantity)
                        .sum();

            return SalesReportResponse.builder()
                    .transactionId(transaction.getId())
                    .transactionCode(transaction.getTransactionCode())
                    .totalAmount(transaction.getTotalAmount())
                    .totalItems(totalItems)
                    .transactionDate(transaction.getTransactionDate())
                    .build();
        }));
    }

    /**
     * Retrieves a paginated audit log report of all stock movements.
     *
     * @param pageable pagination configuration
     * @return paginated {@link StockLogResponse} items
     */
    @Transactional(readOnly = true)
    public PageResponse<StockLogResponse> getStockLogReport(Pageable pageable) {
        log.debug("[REPORT] Fetching paginated stock log report page: {}", pageable.getPageNumber());

        return PageResponse.from(stockLogRepository
                .findAll(pageable)
                .map(stockLog -> StockLogResponse.builder()
                        .logId(stockLog.getId())
                        .productId(stockLog.getProduct() != null ? stockLog.getProduct().getId() : null)
                        .productName(stockLog.getProduct() != null ? stockLog.getProduct().getName() : null)
                        .changeType(stockLog.getChangeType())
                        .quantity(stockLog.getQuantity())
                        .previousStock(stockLog.getPreviousStock())
                        .currentStock(stockLog.getCurrentStock())
                        .remark(stockLog.getRemark())
                        .createdAt(stockLog.getCreatedAt())
                        .build()
                ));
    }
}