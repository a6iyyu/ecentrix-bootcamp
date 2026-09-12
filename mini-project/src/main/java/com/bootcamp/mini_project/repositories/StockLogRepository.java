package com.bootcamp.mini_project.repositories;

import com.bootcamp.mini_project.entity.StockLog;
import java.time.LocalDateTime;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides database operation methods for {@link StockLog} entities.
 */
@Repository
public interface StockLogRepository extends JpaRepository<StockLog, Long> {
    /**
     * Retrieves paginated stock logs for a specific product.
     *
     * @param productId the target product ID
     * @param pageable  the pagination configuration
     * @return a {@link Page} of stock logs for the product
     */
    Page<StockLog> findByProductId(Long productId, Pageable pageable);

    /**
     * Retrieves paginated stock logs created within a specific date range for reporting.
     *
     * @param startDate the start date boundary
     * @param endDate   the end date boundary
     * @param pageable  the pagination configuration
     * @return a {@link Page} of stock logs within the date range
     */
    Page<StockLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}