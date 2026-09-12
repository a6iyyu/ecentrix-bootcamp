package com.bootcamp.mini_project.repositories;

import com.bootcamp.mini_project.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides database operation methods for {@link Category} entities.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Checks if a category exists by its name.
     *
     * @param name the category name to check
     * @return {@code true} if a category exists with the given name
     */
    boolean existsByName(String name);
}