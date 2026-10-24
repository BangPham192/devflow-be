package com.backend.devflow.services;

import com.backend.devflow.dtos.WorkspaceMemberDto;
import com.backend.devflow.mapper.WorkspaceMemberMapper;
import com.backend.devflow.models.User;
import com.backend.devflow.models.Workspace;
import com.backend.devflow.models.WorkspaceMember;
import com.backend.devflow.models.WorkspaceRole;
import com.backend.devflow.repository.UserRepository;
import com.backend.devflow.repository.WorkspaceMemberRepository;
import com.backend.devflow.repository.WorkspaceRepository;
import com.backend.devflow.request.UpdateWorkspaceMemberRequest;
import jakarta.validation.constraints.NotNull;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceMemberService {
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final WorkspaceMemberMapper workspaceMemberMapper;

    public WorkspaceMemberService(WorkspaceMemberRepository workspaceMemberRepository,
            WorkspaceRepository workspaceRepository,
            UserRepository userRepository,
            WorkspaceMemberMapper workspaceMemberMapper) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
        this.workspaceMemberMapper = workspaceMemberMapper;
    }

    public List<WorkspaceMemberDto> getMembers(UUID workspaceId) {
        Workspace workspace = workspaceRepository.findByPublicId(workspaceId)
            .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        List<WorkspaceMember> members = workspaceMemberRepository.findByWorkspaceId(workspace.getId());

        return members.stream().map(this.workspaceMemberMapper::toDto).toList();
    }


    @Transactional
    public WorkspaceMemberDto updateMemberRole(UUID workspaceId, UUID targetUserPublicId, User actor, UpdateWorkspaceMemberRequest request) {
        User targetUser = userRepository.findByPublicId(targetUserPublicId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Workspace workspace = workspaceRepository.findByPublicId(workspaceId).orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        WorkspaceMember actorMember = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspace.getId(), actor.getId())
            .orElseThrow(() -> new ResourceNotFoundException("actor member not found"));
            
            
        WorkspaceMember targetMember = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspace.getId(), targetUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("target member not found"));
        
        validateRoleUpdate(actorMember, targetMember, request.getRoleName());
        applyRoleUpdate(targetMember, request.getRoleName());
        workspaceMemberRepository.save(targetMember);

        return workspaceMemberMapper.toDto(targetMember);
    }

    private void applyRoleUpdate(WorkspaceMember targetMember, @NotNull WorkspaceRole roleName) {
        targetMember.setRoleName(roleName);
    }

    private void validateRoleUpdate(WorkspaceMember actorMember, WorkspaceMember targetMember, @NotNull WorkspaceRole roleName) {
        // only OWNER can change roles
        if (actorMember.getRoleName() != WorkspaceRole.OWNER) {
            throw new AccessDeniedException("Only workspace owner can change role");
        }

        // Cannot change OWNER's role here
        if (targetMember.getRoleName() == WorkspaceRole.OWNER) {
            throw  new AccessDeniedException("Cannot change the owner role");
        }

        // OWNER role has separate operation
        if (roleName == WorkspaceRole.OWNER) {
            throw  new AccessDeniedException("User transfer ownership instead");
        }
    }

    @Transactional
    public void removeMember(UUID workspaceId, UUID targetUserPublicId, User currentUser) {
        User targetUser = userRepository.findByPublicId(targetUserPublicId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Workspace workspace = workspaceRepository.findByPublicId(workspaceId).orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));
        WorkspaceMember actorMember = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspace.getId(), currentUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("actor member not found"));
        WorkspaceMember targetMember = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspace.getId(), targetUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("target member not found"));
        validateRemoveMember(actorMember, targetMember);
        removeMemberData(targetMember);
        workspaceMemberRepository.save(targetMember);
    }

    private void validateRemoveMember(WorkspaceMember actorMember, WorkspaceMember targetMember) {
        WorkspaceRole actorRole = actorMember.getRoleName();
        WorkspaceRole targetRole = targetMember.getRoleName();
        // MEMBER cannot remove anyone
        if (actorRole == WorkspaceRole.MEMBER) {
            throw new AccessDeniedException("Only workspace owner or admin can remove members");
        }
        // Cannot remove workspace owner
        if (targetRole == WorkspaceRole.OWNER) {
            throw new  AccessDeniedException("Cannot remove the workspace owner");
        }

        // ADMIN cannot remove another ADMIN
        if (actorRole == WorkspaceRole.ADMIN &&
            targetRole == WorkspaceRole.ADMIN) {
            throw new AccessDeniedException("Only workspace owner can remove an admin");
        }

        // Cannot use this endpoint to remove yourself
        if (actorMember.getId().equals(targetMember.getId())) {
            throw new AccessDeniedException("Use leave workspace instead");
        }
    }

    private void removeMemberData(WorkspaceMember targetMember) {
        workspaceMemberRepository.delete(targetMember);
    }
}
