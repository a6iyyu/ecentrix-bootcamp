package com.bootcamp.mini_project.dto.suppliers;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Data transfer object for creating or updating a supplier.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierRequest {
    @NotBlank(message = "Name is required.")
    @Size(max = 255, message = "Name must not exceed 255 characters.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @Pattern(regexp = "^[0-9+]*$", message = "Phone number must contain only numbers or leading +.")
    @Size(max = 15, message = "Phone number must not exceed 15 characters.")
    private String phoneNumber;

    @Size(max = 500, message = "Address must not exceed 500 characters.")
    private String address;
}