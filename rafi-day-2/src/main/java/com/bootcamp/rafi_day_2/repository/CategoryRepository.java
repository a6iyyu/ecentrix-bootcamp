package com.bootcamp.rafi_day_2.repository;

import com.bootcamp.rafi_day_2.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Categories, Long> {}