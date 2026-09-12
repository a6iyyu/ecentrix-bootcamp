package com.bootcamp.mini_project.repositories;

import com.bootcamp.mini_project.entity.Transaction;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides database operation methods for {@link Transaction} entities.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    /**
     * Finds a transaction record by its unique code.
     *
     * @param transactionCode the transaction UUID
     * @return an {@link Optional} containing the transaction if found
     */
    Optional<Transaction> findByTransactionCode(String transactionCode);

    /**
     * Retrieves paginated transactions within a specific date range for sales reporting.
     *
     * @param startDate the start date boundary
     * @param endDate   the end date boundary
     * @param pageable  the pagination configuration
     * @return a {@link Page} of transactions within the date range
     */
    Page<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}