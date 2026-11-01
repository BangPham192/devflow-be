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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private WorkspaceRepository workspaceRepository;
    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private UserDetails currentUser;

    @InjectMocks
    private ProjectService projectService;

    private final UUID workspacePublicId = UUID.randomUUID();
    private User actor;
    private Workspace workspace;

    @BeforeEach
    void setUp() {
        actor = new User();
        actor.setId(10L);

        workspace = new Workspace();
        workspace.setId(1L);

        lenient().when(currentUser.getUsername()).thenReturn("actor@example.com");
        lenient().when(userRepository.findByEmail("actor@example.com")).thenReturn(actor);
        lenient().when(workspaceRepository.findByPublicId(workspacePublicId)).thenReturn(Optional.of(workspace));
    }

    private WorkspaceMember memberWithRole(WorkspaceRole role) {
        WorkspaceMember member = new WorkspaceMember();
        member.setRoleName(role);
        return member;
    }

    @Test
    void createProject_whenCallerIsAdmin_savesAndReturnsDto() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(1L, 10L))
                .thenReturn(Optional.of(memberWithRole(WorkspaceRole.ADMIN)));

        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName("Mobile App");

        Project entity = new Project();
        ProjectDto dto = new ProjectDto();
        when(projectMapper.toEntity(request, 1L)).thenReturn(entity);
        when(projectMapper.toDto(entity)).thenReturn(dto);

        ProjectDto result = projectService.createProject(workspacePublicId, request, currentUser);

        assertThat(result).isSameAs(dto);
        verify(projectRepository).save(entity);
    }

    @Test
    void createProject_whenCallerIsMember_isForbidden() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(1L, 10L))
                .thenReturn(Optional.of(memberWithRole(WorkspaceRole.MEMBER)));

        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName("Mobile App");

        assertThatThrownBy(() -> projectService.createProject(workspacePublicId, request, currentUser))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_whenCallerIsViewer_isForbidden() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(1L, 10L))
                .thenReturn(Optional.of(memberWithRole(WorkspaceRole.VIEWER)));

        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName("Mobile App");

        assertThatThrownBy(() -> projectService.createProject(workspacePublicId, request, currentUser))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void createProject_whenCallerIsNotAMember_isNotFound() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(1L, 10L))
                .thenReturn(Optional.empty());

        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName("Mobile App");

        assertThatThrownBy(() -> projectService.createProject(workspacePublicId, request, currentUser))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_whenWorkspaceDoesNotExist_isNotFound() {
        when(workspaceRepository.findByPublicId(workspacePublicId)).thenReturn(Optional.empty());

        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName("Mobile App");

        assertThatThrownBy(() -> projectService.createProject(workspacePublicId, request, currentUser))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getProject_whenProjectBelongsToAnotherWorkspace_isNotFound() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(1L, 10L))
                .thenReturn(Optional.of(memberWithRole(WorkspaceRole.VIEWER)));

        UUID projectPublicId = UUID.randomUUID();
        Project projectInOtherWorkspace = new Project();
        projectInOtherWorkspace.setWorkspaceId(999L);
        when(projectRepository.findByPublicId(projectPublicId)).thenReturn(Optional.of(projectInOtherWorkspace));

        assertThatThrownBy(() -> projectService.getProject(workspacePublicId, projectPublicId, currentUser))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getProject_whenProjectDoesNotExist_isNotFound() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(1L, 10L))
                .thenReturn(Optional.of(memberWithRole(WorkspaceRole.VIEWER)));

        UUID projectPublicId = UUID.randomUUID();
        when(projectRepository.findByPublicId(projectPublicId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProject(workspacePublicId, projectPublicId, currentUser))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getProject_whenCallerIsViewer_isAllowedToRead() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(1L, 10L))
                .thenReturn(Optional.of(memberWithRole(WorkspaceRole.VIEWER)));

        UUID projectPublicId = UUID.randomUUID();
        Project project = new Project();
        project.setWorkspaceId(1L);
        ProjectDto dto = new ProjectDto();
        when(projectRepository.findByPublicId(projectPublicId)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(dto);

        ProjectDto result = projectService.getProject(workspacePublicId, projectPublicId, currentUser);

        assertThat(result).isSameAs(dto);
    }

    @Test
    void archiveProject_whenCallerIsOwner_setsStatusArchived() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(1L, 10L))
                .thenReturn(Optional.of(memberWithRole(WorkspaceRole.OWNER)));

        UUID projectPublicId = UUID.randomUUID();
        Project project = new Project();
        project.setWorkspaceId(1L);
        project.setStatus(ProjectStatus.ACTIVE);
        when(projectRepository.findByPublicId(projectPublicId)).thenReturn(Optional.of(project));

        projectService.archiveProject(workspacePublicId, projectPublicId, currentUser);

        assertThat(project.getStatus()).isEqualTo(ProjectStatus.ARCHIVED);
        verify(projectRepository).save(project);
    }

    @Test
    void listProjects_returnsPagedDtosForWorkspace() {
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(1L, 10L))
                .thenReturn(Optional.of(memberWithRole(WorkspaceRole.VIEWER)));

        Project project = new Project();
        List<ProjectDto> dtos = List.of(new ProjectDto());
        Page<Project> page = new PageImpl<>(List.of(project));
        when(projectRepository.findAllByWorkspaceIdAndStatus(anyLong(), any(), any())).thenReturn(page);
        when(projectMapper.toDto(List.of(project))).thenReturn(dtos);

        PageRequestCustom pageRequest = new PageRequestCustom();
        Page<ProjectDto> result = projectService.listProjects(
                workspacePublicId, ProjectStatus.ACTIVE, pageRequest, currentUser);

        assertThat(result.getContent()).isEqualTo(dtos);
    }
}
