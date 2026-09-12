package com.bootcamp.mini_project.dto.categories;

import lombok.*;

/**
 * Data transfer object for category response details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;

    private String name;

    private String description;
}