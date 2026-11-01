package com.backend.devflow.services;

import com.backend.devflow.dtos.ProjectDto;
import com.backend.devflow.http.PageRequestCustom;
import com.backend.devflow.mapper.ProjectMapper;
import com.backend.devflow.models.Project;
import com.backend.devflow.models.ProjectStatus;
import com.backend.devflow.models.User;
import com.backend.devflow.models.Workspace;
import com.backend.devflow.models.WorkspaceMember;
import com.backend.devflow.models.WorkspaceRole;
import com.backend.devflow.repository.ProjectRepository;
import com.backend.devflow.repository.UserRepository;
import com.backend.devflow.repository.WorkspaceMemberRepository;
import com.backend.devflow.repository.WorkspaceRepository;
import com.backend.devflow.request.ProjectCreateRequest;
import com.backend.devflow.request.ProjectUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ProjectService {

    private static final Set<WorkspaceRole> MANAGE_ROLES = EnumSet.of(WorkspaceRole.OWNER, WorkspaceRole.ADMIN);

    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Autowired
    public ProjectService(ProjectRepository projectRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberRepository workspaceMemberRepository,
            UserRepository userRepository,
            ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userRepository = userRepository;
        this.projectMapper = projectMapper;
    }

    @Transactional
    public ProjectDto createProject(UUID workspacePublicId, ProjectCreateRequest request, UserDetails currentUser) {
        WorkspaceAccess access = requireMembership(workspacePublicId, currentUser);
        requireManageRole(access.member());

        Project project = projectMapper.toEntity(request, access.workspace().getId());
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectDto> listProjects(UUID workspacePublicId, ProjectStatus status,
            PageRequestCustom pageRequest, UserDetails currentUser) {
        WorkspaceAccess access = requireMembership(workspacePublicId, currentUser);

        PageRequest page = PageRequest.of(pageRequest.getPage(), pageRequest.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Project> projects = projectRepository.findAllByWorkspaceIdAndStatus(
                access.workspace().getId(), status, page);
        List<ProjectDto> dtos = projectMapper.toDto(projects.getContent());
        return new PageImpl<>(dtos, page, projects.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ProjectDto getProject(UUID workspacePublicId, UUID projectPublicId, UserDetails currentUser) {
        WorkspaceAccess access = requireMembership(workspacePublicId, currentUser);
        Project project = requireProjectInWorkspace(projectPublicId, access.workspace());
        return projectMapper.toDto(project);
    }

    @Transactional
    public ProjectDto updateProject(UUID workspacePublicId, UUID projectPublicId,
            ProjectUpdateRequest request, UserDetails currentUser) {
        WorkspaceAccess access = requireMembership(workspacePublicId, currentUser);
        requireManageRole(access.member());

        Project project = requireProjectInWorkspace(projectPublicId, access.workspace());
        project = projectMapper.update(project, request);
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Transactional
    public void archiveProject(UUID workspacePublicId, UUID projectPublicId, UserDetails currentUser) {
        WorkspaceAccess access = requireMembership(workspacePublicId, currentUser);
        requireManageRole(access.member());

        Project project = requireProjectInWorkspace(projectPublicId, access.workspace());
        project.setStatus(ProjectStatus.ARCHIVED);
        projectRepository.save(project);
    }

    /**
     * Resolves the caller's membership in one workspace. Returns 404 whenever the caller
     * should not be able to tell the workspace apart from one that does not exist at all —
     * covers "workspace missing" and "not a member" with the same response.
     */
    private WorkspaceAccess requireMembership(UUID workspacePublicId, UserDetails currentUser) {
        User actor = userRepository.findByEmail(currentUser.getUsername());
        if (actor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found");
        }

        Workspace workspace = workspaceRepository.findByPublicId(workspacePublicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found"));

        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspace.getId(), actor.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found"));

        return new WorkspaceAccess(workspace, member);
    }

    private void requireManageRole(WorkspaceMember member) {
        if (!MANAGE_ROLES.contains(member.getRoleName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only workspace owners and admins can manage projects");
        }
    }

    private Project requireProjectInWorkspace(UUID projectPublicId, Workspace workspace) {
        Project project = projectRepository.findByPublicId(projectPublicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        if (!project.getWorkspaceId().equals(workspace.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
        return project;
    }

    private record WorkspaceAccess(Workspace workspace, WorkspaceMember member) {
    }
}
