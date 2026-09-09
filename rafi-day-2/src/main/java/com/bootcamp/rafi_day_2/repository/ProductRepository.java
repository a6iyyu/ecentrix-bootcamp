package com.bootcamp.rafi_day_2.repository;

import com.bootcamp.rafi_day_2.entity.Products;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@NullMarked
@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {
    boolean existsByCategoryId(Long categoryId);

    @Override
    @EntityGraph(attributePaths = {"category"})
    Page<Products> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"category"})
    Optional<Products> findById(Long id);

    @EntityGraph(attributePaths = {"category"})
    Page<Products> findByCategoryId(Long categoryId, Pageable pageable);
}