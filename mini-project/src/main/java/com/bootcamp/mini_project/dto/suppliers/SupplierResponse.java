package com.bootcamp.mini_project.dto.suppliers;

import java.time.LocalDateTime;
import lombok.*;

/**
 * Data transfer object for supplier response details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierResponse {
    private Long id;

    private String name;

    private String email;

    private String phoneNumber;

    private String address;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}