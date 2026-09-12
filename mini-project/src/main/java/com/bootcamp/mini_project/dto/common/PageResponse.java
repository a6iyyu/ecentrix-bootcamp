package com.bootcamp.mini_project.dto.common;

import java.util.List;
import lombok.*;
import org.springframework.data.domain.Page;

/**
 * Standardized wrapper for paginated API responses containing pagination metadata.
 *
 * @param <T> the type of items in the page content
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    /**
     * The list of records contained within the current page.
     */
    private List<T> content;

    /**
     * The current page index (zero-based).
     */
    private int pageNumber;

    /**
     * The maximum number of items requested per page.
     */
    private int pageSize;

    /**
     * The grand total of elements available across all pages.
     */
    private long totalElements;

    /**
     * The total number of pages derived from total elements and page size.
     */
    private int totalPages;

    /**
     * Indicates whether the current page is the first one.
     */
    private boolean isFirst;

    /**
     * Indicates whether the current page is the last one.
     */
    private boolean isLast;

    /**
     * Constructs a {@link PageResponse} instance directly from a Spring Data {@link Page}.
     *
     * @param page the Spring Data page source
     * @param <T>  the item type
     * @return a mapped {@link PageResponse} instance
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }
}