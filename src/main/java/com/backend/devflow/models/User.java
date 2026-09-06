package com.backend.devflow.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {
    @Column(name = "public_id", unique = true, nullable = false, columnDefinition = "BINARY(16)")
    private UUID publicId;

    @Column(name = "username")
    private String username;
    private String email;
    private String passwordHash;
    private String avatarUrl;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles =  new HashSet<>();
}
