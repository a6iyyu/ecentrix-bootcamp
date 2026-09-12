package com.bootcamp.mini_project.services;

import com.bootcamp.mini_project.dto.common.PageResponse;
import com.bootcamp.mini_project.dto.transactions.*;
import com.bootcamp.mini_project.entity.*;
import com.bootcamp.mini_project.exceptions.custom.ResourceNotFoundException;
import com.bootcamp.mini_project.kafka.producer.TransactionProducer;
import com.bootcamp.mini_project.mappers.TransactionMapper;
import com.bootcamp.mini_project.repositories.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles business logic for sales transactions, stock deduction, audit logging,
 * Redis cache eviction, and publishing events to Kafka.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final CacheManager cacheManager;
    private final ProductRepository productRepository;
    private final StockLogRepository stockLogRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionProducer transactionProducer;
    private final TransactionRepository transactionRepository;

    /**
     * Retrieves a paginated history of transactions.
     *
     * @param pageable pagination configuration
     * @return paginated {@link TransactionResponse} items
     */
    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> getAllTransactions(Pageable pageable) {
        log.debug("[TRANSACTION] Fetching paginated transactions page: {}", pageable.getPageNumber());
        return PageResponse.from(transactionRepository.findAll(pageable).map(transactionMapper::toResponse));
    }

    /**
     * Retrieves a transaction by its unique database ID.
     *
     * @param id target transaction ID
     * @return mapped {@link TransactionResponse}
     */
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> {
            log.warn("[TRANSACTION NOT FOUND] Read operation failed. Transaction ID {} does not exist.", id);
            return new ResourceNotFoundException("Transaction not found with ID: " + id);
        });

        return transactionMapper.toResponse(transaction);
    }

    /**
     * Processes a new sales transaction checkout atomically:
     * 1. Validates stock availability.
     * 2. Deducts product stock & records StockLog audit.
     * 3. Evicts product Redis caches.
     * 4. Saves Transaction & TransactionDetails.
     * 5. Publishes TransactionEvent to Kafka topic.
     *
     * @param request payload containing items and quantities to purchase
     * @return the created {@link TransactionResponse}
     */
    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        // Generate unique transaction code.
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        String transactionCode = "TRX-" + timestamp + "-" + randomSuffix;

        log.info("[TRANSACTION START] Initiating checkout for Code: {}", transactionCode);

        Transaction transaction = new Transaction();
        transaction.setTransactionCode(transactionCode);
        transaction.setTransactionDate(LocalDateTime.now());

        BigDecimal grandTotal = BigDecimal.ZERO;
        List<TransactionDetail> details = new ArrayList<>();

        // Process each transaction item (validation, stock deduction, logs).
        for (TransactionItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> {
                log.warn("[PRODUCT NOT FOUND] Transaction failed. Product ID {} does not exist.", item.getProductId());
                return new ResourceNotFoundException("Product not found with ID: " + item.getProductId());
            });

            // Check stock availability.
            if (product.getStock() < item.getQuantity()) {
                log.warn("[STOCK INSUFFICIENT] Product '{}' (ID: {}) requested quantity {} exceeds available stock {}.", product.getName(), product.getId(), item.getQuantity(), product.getStock());
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName() + ". Available: " + product.getStock() + ", Requested: " + item.getQuantity());
            }

            // Deduct product stock.
            int previousStock = product.getStock();
            int updatedStock = previousStock - item.getQuantity();
            product.setStock(updatedStock);
            productRepository.save(product);

            // Record stock change audit logs
            StockLog stockLog = new StockLog();
            stockLog.setProduct(product);
            stockLog.setChangeType(StockLog.StockChangeType.OUT);
            stockLog.setQuantity(item.getQuantity());
            stockLog.setPreviousStock(previousStock);
            stockLog.setCurrentStock(updatedStock);
            stockLog.setRemark("Sales Transaction: " + transactionCode);
            stockLog.setCreatedAt(LocalDateTime.now());
            stockLogRepository.save(stockLog);

            // Evict product cache Redis
            evictProductCache(product.getId(), product.getName());

            // Calculate subtotals and accumulation
            BigDecimal itemPrice = product.getPrice();
            BigDecimal subtotal = itemPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            grandTotal = grandTotal.add(subtotal);

            TransactionDetail detail = new TransactionDetail();
            detail.setTransaction(transaction);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(itemPrice);
            detail.setSubtotal(subtotal);

            details.add(detail);
        }

        // Save the transaction along with its details.
        transaction.setTotalAmount(grandTotal);
        transaction.setDetails(details);

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("[TRANSACTION SUCCESS] Saved transaction ID: {} with Code: {}, Total: {}", savedTransaction.getId(), transactionCode, grandTotal);

        // Publish event to Kafka
        TransactionEvent event = transactionMapper.toEvent(savedTransaction);
        transactionProducer.sendTransactionEvent(event);
        log.info("[KAFKA PUBLISH] Published TransactionEvent for Code: {}", transactionCode);

        return transactionMapper.toResponse(savedTransaction);
    }

    /**
     * Helper to evict cache for a specific product whose stock was modified.
     *
     * @param id    the unique identifier of the product whose cache will be evicted
     * @param names dynamic array of product names to evict from cache keys
     */
    private void evictProductCache(Long id, String... names) {
        Cache cache = cacheManager.getCache("products");

        if (cache == null) {
            return;
        }

        if (id != null) {
            cache.evict(id);
        }

        Stream.of(names)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(name -> cache.evict("name:" + name));
    }
}