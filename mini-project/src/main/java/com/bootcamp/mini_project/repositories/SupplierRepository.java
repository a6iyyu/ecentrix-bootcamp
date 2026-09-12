package com.bootcamp.mini_project.repositories;

import com.bootcamp.mini_project.entity.Supplier;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides database operation methods for {@link Supplier} entities.
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    /**
     * Checks whether a supplier exists with the given email address.
     *
     * @param email the email address to check
     * @return {@code true} if a supplier exists
     */
    boolean existsByEmail(String email);

    /**
     * Finds a supplier by email address.
     *
     * @param email the supplier email address
     * @return an {@link Optional} containing the found supplier
     */
    Optional<Supplier> findByEmail(String email);
}