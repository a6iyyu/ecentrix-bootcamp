package com.bootcamp.mini_project.controllers;

import com.bootcamp.mini_project.dto.common.*;
import com.bootcamp.mini_project.dto.transactions.*;
import com.bootcamp.mini_project.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Controller providing REST API operations for sales transactions and checkout processing.
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transactions Management", description = "Endpoints for processing checkout and retrieving transaction history")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Process a new sales transaction checkout")
    public ApiResponse<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        log.info("[API REST] Checkout request received with {} items", request.getItems() != null ? request.getItems().size() : 0);
        TransactionResponse response = transactionService.createTransaction(request);

        return ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Transaction processed successfully")
                .data(response)
                .build();
    }

    @GetMapping
    @Operation(summary = "Get paginated transaction history")
    public PageResponse<TransactionResponse> getAllTransactions(Pageable pageable) {
        log.info("[API REST] Fetching transactions history page: {}", pageable.getPageNumber());
        return transactionService.getAllTransactions(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction details by unique ID")
    public ApiResponse<TransactionResponse> getTransactionById(@PathVariable Long id) {
        log.info("[API REST] Fetching transaction ID: {}", id);
        TransactionResponse response = transactionService.getTransactionById(id);

        return ApiResponse.<TransactionResponse>builder()
                .success(true)
                .message("Transaction retrieved successfully")
                .data(response)
                .build();
    }
}