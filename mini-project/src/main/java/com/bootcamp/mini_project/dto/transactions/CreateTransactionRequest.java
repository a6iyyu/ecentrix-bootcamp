package com.bootcamp.mini_project.dto.transactions;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.*;

/**
 * Data transfer object for initiating a new sales transaction.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransactionRequest {
    @NotEmpty(message = "Transaction items must not be empty.")
    @Valid
    private List<TransactionItemRequest> items;
}