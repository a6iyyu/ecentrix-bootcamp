package com.bootcamp.mini_project.mappers;

import com.bootcamp.mini_project.dto.transactions.*;
import com.bootcamp.mini_project.entity.*;
import java.util.*;
import org.springframework.stereotype.Component;

/**
 * Handles mapping between Transaction entities and transaction-related DTOs.
 */
@Component
public class TransactionMapper {
    /**
     * Converts a saved {@link Transaction} entity into a {@link TransactionEvent} payload for Kafka.
     */
    public TransactionEvent toEvent(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        List<TransactionItemRequest> items = transaction.getDetails() == null
                ? Collections.emptyList()
                : transaction.getDetails().stream().map(detail -> TransactionItemRequest.builder()
                    .productId(detail.getProduct() != null ? detail.getProduct().getId() : null)
                    .quantity(detail.getQuantity())
                    .build()).toList();

        return TransactionEvent.builder()
                .transactionCode(transaction.getTransactionCode())
                .totalAmount(transaction.getTotalAmount())
                .transactionDate(transaction.getTransactionDate())
                .items(items)
                .build();
    }

    /**
     * Converts a single {@link TransactionDetail} entity into a {@link TransactionItemResponse} DTO.
     */
    public TransactionItemResponse toItemResponse(TransactionDetail detail) {
        if (detail == null) {
            return null;
        }

        return TransactionItemResponse.builder()
                .id(detail.getId())
                .productId(detail.getProduct() != null ? detail.getProduct().getId() : null)
                .productName(detail.getProduct() != null ? detail.getProduct().getName() : null)
                .quantity(detail.getQuantity())
                .price(detail.getPrice())
                .subtotal(detail.getSubtotal())
                .build();
    }

    /**
     * Converts a {@link Transaction} entity into a complete {@link TransactionResponse} DTO.
     */
    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        List<TransactionItemResponse> details = transaction.getDetails() == null
                ? Collections.emptyList()
                : transaction.getDetails().stream().map(this::toItemResponse).toList();

        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionCode(transaction.getTransactionCode())
                .totalAmount(transaction.getTotalAmount())
                .transactionDate(transaction.getTransactionDate())
                .details(details)
                .build();
    }
}