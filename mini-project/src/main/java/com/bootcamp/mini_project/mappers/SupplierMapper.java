package com.bootcamp.mini_project.mappers;

import com.bootcamp.mini_project.dto.suppliers.*;
import com.bootcamp.mini_project.entity.Supplier;
import org.springframework.stereotype.Component;

/**
 * Handles data mapping between {@link Supplier} entity and its corresponding DTOs.
 */
@Component
public class SupplierMapper {
    /**
     * Converts a {@link SupplierRequest} DTO into a {@link Supplier} entity.
     *
     * @param request the request DTO source
     * @return mapped {@link Supplier} entity instance, or {@code null} if source is null
     */
    public Supplier toEntity(SupplierRequest request) {
        if (request == null) {
            return null;
        }

        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        supplier.setPhoneNumber(request.getPhoneNumber());

        return supplier;
    }

    /**
     * Converts a {@link Supplier} entity into a {@link SupplierResponse} DTO.
     *
     * @param supplier the entity source
     * @return mapped {@link SupplierResponse} instance, or {@code null} if source is null
     */
    public SupplierResponse toResponse(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .email(supplier.getEmail())
                .phoneNumber(supplier.getPhoneNumber())
                .address(supplier.getAddress())
                .build();
    }
}