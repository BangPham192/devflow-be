package com.backend.devflow.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
@Getter
@Setter
public class Workspace extends BaseEntity{
    @Column(name = "public_id", unique = true, nullable = false, columnDefinition = "BINARY(16)")
    private UUID publicId;
    @Column(name = "owner_id", insertable = false, updatable = false)
    private Long ownerId;
    private String name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    private WorkspaceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkspaceMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkspaceInvitation> invitations = new ArrayList<>();

}
