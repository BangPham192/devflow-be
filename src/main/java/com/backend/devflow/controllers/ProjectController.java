package com.backend.devflow.controllers;

import com.backend.devflow.dtos.ProjectDto;
import com.backend.devflow.http.PageRequestCustom;
import com.backend.devflow.interfaces.IProjectController;
import com.backend.devflow.models.ProjectStatus;
import com.backend.devflow.request.ProjectCreateRequest;
import com.backend.devflow.request.ProjectUpdateRequest;
import com.backend.devflow.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ProjectController extends BaseController implements IProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public ProjectDto createProject(UUID workspaceId, ProjectCreateRequest request) {
        UserDetails currentUser = getCurrentUser();
        return projectService.createProject(workspaceId, request, currentUser);
    }

    @Override
    public Page<ProjectDto> getProjects(UUID workspaceId, ProjectStatus status, PageRequestCustom pageRequest) {
        UserDetails currentUser = getCurrentUser();
        return projectService.listProjects(workspaceId, status, pageRequest, currentUser);
    }

    @Override
    public ProjectDto getProject(UUID workspaceId, UUID projectId) {
        UserDetails currentUser = getCurrentUser();
        return projectService.getProject(workspaceId, projectId, currentUser);
    }

    @Override
    public ProjectDto updateProject(UUID workspaceId, UUID projectId, ProjectUpdateRequest request) {
        UserDetails currentUser = getCurrentUser();
        return projectService.updateProject(workspaceId, projectId, request, currentUser);
    }

    @Override
    public void archiveProject(UUID workspaceId, UUID projectId) {
        UserDetails currentUser = getCurrentUser();
        projectService.archiveProject(workspaceId, projectId, currentUser);
    }
}
