package com.backend.devflow.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workspace_members")
@Getter
@Setter
public class WorkspaceMember extends BaseEntity {

    @Column(name = "workspace_id", insertable = false, updatable = false)
    private Long workspaceId;
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private WorkspaceRole roleName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
