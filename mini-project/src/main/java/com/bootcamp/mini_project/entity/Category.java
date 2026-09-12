package com.bootcamp.mini_project.entity;

import jakarta.persistence.*;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.*;
import lombok.*;
import org.hibernate.annotations.*;

/**
 * Represents a product category classification.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(name = "description")
    @JdbcTypeCode(Types.LONGVARCHAR)
    private String description;

    @ToString.Exclude
    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    @SoftDelete
    private LocalDateTime deletedAt;
}