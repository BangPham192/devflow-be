package com.backend.devflow.dtos;

import com.backend.devflow.models.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceMemberDto {
    private UUID userId;
    private String username;
    private String email;
    private String avatarUrl;
    private WorkspaceRole roleName;
    private LocalDateTime joinedAt;
}
