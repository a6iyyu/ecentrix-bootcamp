package com.bootcamp.mini_project.controllers;

import com.bootcamp.mini_project.dto.common.PageResponse;
import com.bootcamp.mini_project.dto.reports.*;
import com.bootcamp.mini_project.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Controller providing REST API endpoints for viewing sales analytics and stock audit reports.
 */
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reports & Analytics", description = "Endpoints for sales summary and stock audit log reports")
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/sales")
    @Operation(summary = "Retrieve paginated sales analytics report")
    public PageResponse<SalesReportResponse> getSalesReport(Pageable pageable) {
        log.info("[API REST] Fetching sales report page: {}", pageable.getPageNumber());
        return reportService.getSalesReport(pageable);
    }

    @GetMapping("/stock-logs")
    @Operation(summary = "Retrieve paginated stock movement audit log report")
    public PageResponse<StockLogResponse> getStockLogReport(Pageable pageable) {
        log.info("[API REST] Fetching stock log report page: {}", pageable.getPageNumber());
        return reportService.getStockLogReport(pageable);
    }
}