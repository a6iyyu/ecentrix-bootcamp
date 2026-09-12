package com.bootcamp.mini_project.repositories;

import com.bootcamp.mini_project.entity.Product;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides database operation methods for {@link Product} entities.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Checks whether a product exists with the specified name.
     *
     * @param name product name to check
     * @return {@code true} if product exists
     */
    boolean existsByName(String name);

    /**
     * Retrieves paginated products filtered by category ID.
     *
     * @param categoryId the target category ID
     * @param pageable   the pagination configuration
     * @return a {@link Page} of matching products
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * Finds a product by exact name match.
     *
     * @param name the product name
     * @return an {@link Optional} containing the found product
     */
    Optional<Product> findByName(String name);

    /**
     * Searches products containing the specified keyword in their name with pagination.
     *
     * @param name     the search keyword
     * @param pageable the pagination configuration
     * @return a {@link Page} of matching products
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}