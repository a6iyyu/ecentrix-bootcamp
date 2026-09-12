package com.bootcamp.mini_project.dto.categories;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Data transfer object for creating or updating a category.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {
    @NotBlank(message = "Name of the category is required.")
    @Size(max = 50, message = "Name must not exceed 50 characters.")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters.")
    private String description;
}