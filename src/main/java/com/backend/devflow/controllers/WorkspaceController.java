package com.backend.devflow.controllers;

import com.backend.devflow.dtos.WorkspaceDto;
import com.backend.devflow.http.PageRequestCustom;
import com.backend.devflow.interfaces.IWorkspaceController;
import com.backend.devflow.request.WorkspaceCreateRequest;
import com.backend.devflow.request.WorkspaceUpdateRequest;
import com.backend.devflow.services.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class WorkspaceController extends BaseController implements IWorkspaceController {

    private final WorkspaceService workspaceService;

    @Autowired
    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @Override
    public Page<WorkspaceDto> getAllWorkspaces(PageRequestCustom pageRequest) {
        return workspaceService.getAllWorkspaces(pageRequest);
    }

    @Override
    public WorkspaceDto createWorkspace(WorkspaceCreateRequest request) {
        UserDetails currentUser = getCurrentUser();
        return workspaceService.createWorkspace(request, currentUser);
    }

    @Override
    public WorkspaceDto getWorkspace(UUID id) {
        return workspaceService.getWorkspaceByPublicId(id);
    }

    @Override
    public WorkspaceDto updateWorkspace(UUID id, WorkspaceUpdateRequest request) {
        UserDetails currentUser = getCurrentUser();
        return workspaceService.updateWorkspace(id, currentUser, request);
    }
}
