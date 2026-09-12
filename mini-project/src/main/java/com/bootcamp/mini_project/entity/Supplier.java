package com.bootcamp.mini_project.entity;

import jakarta.persistence.*;
import java.sql.Types;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.*;

/**
 * Represents a product supplier entity.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "address")
    @JdbcTypeCode(Types.LONGVARCHAR)
    private String address;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    @SoftDelete
    private LocalDateTime deletedAt;
}