package com.backend.devflow.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

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
    private List<UserRole> userRoles =  new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Workspace>  workspaces = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkspaceMember>  workspaceMembers = new ArrayList<>();

    @OneToMany(mappedBy = "inviter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkspaceInvitation> workspaceInvitations = new ArrayList<>();
}
