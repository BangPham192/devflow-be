package com.backend.devflow.controllers;

import com.backend.devflow.dtos.WorkspaceMemberDto;
import com.backend.devflow.interfaces.IWorkspaceMemberController;
import com.backend.devflow.models.User;
import com.backend.devflow.repository.UserRepository;
import com.backend.devflow.request.UpdateWorkspaceMemberRequest;
import com.backend.devflow.services.WorkspaceMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class WorkspaceMemberController extends BaseController implements IWorkspaceMemberController {
    private final WorkspaceMemberService workspaceMemberService;
    private final UserRepository userRepository;

    @Autowired
    public WorkspaceMemberController(WorkspaceMemberService workspaceMemberService, UserRepository userRepository) {
        this.workspaceMemberService = workspaceMemberService;
        this.userRepository = userRepository;
    }

    @Override
    public List<WorkspaceMemberDto> getMembers(UUID workspaceId) {
        return workspaceMemberService.getMembers(workspaceId);
    }

    @Override
    public WorkspaceMemberDto updateMemberRole(UUID workspaceId, UUID userId, UpdateWorkspaceMemberRequest request) {
        User currentUser = userRepository.findByEmail(getCurrentUser().getUsername());
        return workspaceMemberService.updateMemberRole(workspaceId, userId, currentUser, request);
    }

    @Override
    public void removeMember(UUID workspaceId, UUID userId) {
        User currentUser = userRepository.findByEmail(getCurrentUser().getUsername());
        workspaceMemberService.removeMember(workspaceId, userId, currentUser);
    }
}
