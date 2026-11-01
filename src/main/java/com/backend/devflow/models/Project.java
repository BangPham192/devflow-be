package com.backend.devflow.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
public class Project extends BaseEntity {
    @Column(name = "public_id", unique = true, nullable = false, columnDefinition = "BINARY(16)")
    private UUID publicId;

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;
}
