package com.backend.devflow.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workspace_invitations")
@Getter
@Setter
public class WorkspaceInvitation extends BaseEntity{

    @Column(name = "public_id", unique = true, nullable = false, columnDefinition = "BINARY(16)")
    private UUID publicId;
    @Column(name = "workspace_id", insertable = false, updatable = false)
    private Long workspaceId;
    @Column(name = "inviter_id", insertable = false, updatable = false)
    private Long inviterId;
    private String email;
    @Enumerated(EnumType.STRING)
    private WorkspaceRole roleName;
    private String tokenHash;

    @Enumerated(EnumType.STRING)
    private WorkspaceInvitationStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime acceptedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id")
    private User inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;
}
