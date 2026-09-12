package com.bootcamp.mini_project.services;

import com.bootcamp.mini_project.dto.common.PageResponse;
import com.bootcamp.mini_project.dto.suppliers.*;
import com.bootcamp.mini_project.entity.Supplier;
import com.bootcamp.mini_project.exceptions.custom.ResourceNotFoundException;
import com.bootcamp.mini_project.mappers.SupplierMapper;
import com.bootcamp.mini_project.repositories.SupplierRepository;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.*;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles business logic and caching operations for product suppliers.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SupplierService {
    private final CacheManager cacheManager;
    private final SupplierMapper supplierMapper;
    private final SupplierRepository supplierRepository;

    /**
     * Retrieves a paginated list of all suppliers mapped to response DTOs.
     *
     * @param pageable pagination configuration
     * @return a {@link PageResponse} wrapping supplier response items
     */
    @Transactional(readOnly = true)
    public PageResponse<SupplierResponse> getAllSuppliers(Pageable pageable) {
        log.debug("[SUPPLIER] Fetching paginated suppliers page: {}", pageable.getPageNumber());
        return PageResponse.from(supplierRepository.findAll(pageable).map(supplierMapper::toResponse));
    }

    /**
     * Retrieves a specific supplier by ID and caches the result in Redis.
     *
     * @param id the unique supplier identifier
     * @return the corresponding {@link SupplierResponse} DTO
     * @throws ResourceNotFoundException if target supplier is not found
     */
    @Cacheable(value = "suppliers", key = "#id")
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {
        log.info("[CACHE MISS] Fetching supplier from database for ID: {}", id);

        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> {
            log.warn("[SUPPLIER NOT FOUND] Read operation failed. Supplier ID {} does not exist.", id);
            return new ResourceNotFoundException("Supplier not found with ID: " + id);
        });

        return supplierMapper.toResponse(supplier);
    }

    /**
     * Retrieves a specific supplier by email and caches the result in Redis.
     *
     * @param email the unique supplier email address
     * @return the corresponding {@link SupplierResponse} DTO
     * @throws ResourceNotFoundException if target supplier is not found
     */
    @Cacheable(value = "suppliers", key = "'email:' + #email")
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierByEmail(String email) {
        log.info("[CACHE MISS] Fetching supplier from database for email: {}", email);

        Supplier supplier = supplierRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("[SUPPLIER NOT FOUND] Read operation failed. Supplier email {} does not exist.", email);
            return new ResourceNotFoundException("Supplier not found with email: " + email);
        });

        return supplierMapper.toResponse(supplier);
    }

    /**
     * Creates a new supplier record.
     *
     * @param request payload containing supplier creation details
     * @return the created {@link SupplierResponse} DTO
     * @throws IllegalArgumentException if supplier email is already registered
     */
    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {
        if (supplierRepository.existsByEmail(request.getEmail())) {
            log.warn("[SUPPLIER DUPLICATE] Creation failed. Email '{}' already registered.", request.getEmail());
            throw new IllegalArgumentException("Supplier email already exists: " + request.getEmail());
        }

        Supplier supplier = supplierMapper.toEntity(request);
        Supplier savedSupplier = supplierRepository.save(supplier);
        log.info("[SUPPLIER] Successfully created supplier with ID: {}", savedSupplier.getId());

        return supplierMapper.toResponse(savedSupplier);
    }

    /**
     * Updates an existing supplier record and invalidates both ID and Email cache entries.
     *
     * @param id      the unique supplier identifier
     * @param request payload containing updated supplier details
     * @return the updated {@link SupplierResponse} DTO
     * @throws ResourceNotFoundException if target supplier is not found
     */
    @Transactional
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> {
            log.warn("[SUPPLIER NOT FOUND] Update operation failed. Supplier ID {} does not exist.", id);
            return new ResourceNotFoundException("Supplier not found with ID: " + id);
        });

        String oldEmail = supplier.getEmail();

        if (!oldEmail.equalsIgnoreCase(request.getEmail()) && supplierRepository.existsByEmail(request.getEmail())) {
            log.warn("[SUPPLIER DUPLICATE] Update failed. Email '{}' already registered to another supplier.", request.getEmail());
            throw new IllegalArgumentException("Supplier email already exists: " + request.getEmail());
        }

        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setPhoneNumber(request.getPhoneNumber());
        supplier.setAddress(request.getAddress());

        Supplier updatedSupplier = supplierRepository.save(supplier);

        evictSupplierCache(id, oldEmail, updatedSupplier.getEmail());
        log.info("[SUPPLIER] Updated supplier ID: {} and evicted cache entries.", id);

        return supplierMapper.toResponse(updatedSupplier);
    }

    /**
     * Soft-deletes a supplier by ID and invalidates its Redis cache entries.
     *
     * @param id the unique supplier identifier
     * @throws ResourceNotFoundException if target supplier is not found
     */
    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> {
            log.warn("[SUPPLIER NOT FOUND] Delete operation failed. Supplier ID {} does not exist.", id);
            return new ResourceNotFoundException("Supplier not found with ID: " + id);
        });

        evictSupplierCache(supplier.getId(), supplier.getEmail());
        supplierRepository.delete(supplier);
        log.info("[SUPPLIER] Soft-deleted supplier ID: {} and evicted cache entries.", id);
    }

    /**
     * Helper method to programmatically evict cache entries without nested IF blocks.
     *
     * @param id     supplier ID key to evict
     * @param emails dynamic array of email keys to evict
     */
    private void evictSupplierCache(Long id, String... emails) {
        Cache cache = cacheManager.getCache("suppliers");

        if (cache == null) {
            return;
        }

        if (id != null) {
            cache.evict(id);
        }

        Stream.of(emails)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(email -> cache.evict("email:" + email));
    }
}