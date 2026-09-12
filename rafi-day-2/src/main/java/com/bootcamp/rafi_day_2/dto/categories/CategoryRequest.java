package com.bootcamp.rafi_day_2.dto.categories;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {
    @NotBlank(message = "Name is required.")
    @Size(max = 255, message = "Name must not exceed 255 characters.")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;
}