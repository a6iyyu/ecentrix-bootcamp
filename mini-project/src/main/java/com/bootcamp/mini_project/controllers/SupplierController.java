package com.bootcamp.mini_project.controllers;

import com.bootcamp.mini_project.dto.common.*;
import com.bootcamp.mini_project.dto.suppliers.*;
import com.bootcamp.mini_project.services.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Controller providing REST API operations for managing product suppliers.
 */
@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Suppliers Management", description = "CRUD operations for product suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @Operation(summary = "Create a new supplier")
    public ApiResponse<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest request) {
        log.info("[API REST] Creating supplier with name: {}", request.getName());
        SupplierResponse response = supplierService.createSupplier(request);

        return ApiResponse.<SupplierResponse>builder()
                .success(true)
                .message("Supplier created successfully")
                .data(response)
                .build();
    }

    @GetMapping
    @Operation(summary = "Get all suppliers with pagination")
    public PageResponse<SupplierResponse> getAllSuppliers(Pageable pageable) {
        log.info("[API REST] Fetching suppliers page: {}", pageable.getPageNumber());
        return supplierService.getAllSuppliers(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier details by unique ID")
    public ApiResponse<SupplierResponse> getSupplierById(@PathVariable Long id) {
        log.info("[API REST] Fetching supplier ID: {}", id);
        SupplierResponse response = supplierService.getSupplierById(id);

        return ApiResponse.<SupplierResponse>builder()
                .success(true)
                .message("Supplier retrieved successfully")
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update supplier details by ID")
    public ApiResponse<SupplierResponse> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        log.info("[API REST] Updating supplier ID: {}", id);
        SupplierResponse response = supplierService.updateSupplier(id, request);

        return ApiResponse.<SupplierResponse>builder()
                .success(true)
                .message("Supplier updated successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete supplier by ID")
    public ApiResponse<String> deleteSupplier(@PathVariable Long id) {
        log.info("[API REST] Deleting supplier ID: {}", id);
        supplierService.deleteSupplier(id);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Supplier deleted successfully")
                .data(null)
                .build();
    }
}