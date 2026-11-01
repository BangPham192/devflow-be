package com.backend.devflow.interfaces;

import com.backend.devflow.dtos.ProjectDto;
import com.backend.devflow.http.PageRequestCustom;
import com.backend.devflow.models.ProjectStatus;
import com.backend.devflow.request.ProjectCreateRequest;
import com.backend.devflow.request.ProjectUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/projects")
public interface IProjectController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ProjectDto createProject(@PathVariable UUID workspaceId, @Valid @RequestBody ProjectCreateRequest request);

    @GetMapping
    Page<ProjectDto> getProjects(@PathVariable UUID workspaceId,
                                  @RequestParam(name = "status", defaultValue = "ACTIVE") ProjectStatus status,
                                  PageRequestCustom pageRequest);

    @GetMapping("/{projectId}")
    ProjectDto getProject(@PathVariable UUID workspaceId, @PathVariable UUID projectId);

    @PatchMapping("/{projectId}")
    ProjectDto updateProject(@PathVariable UUID workspaceId, @PathVariable UUID projectId,
                              @Valid @RequestBody ProjectUpdateRequest request);

    @PostMapping("/{projectId}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void archiveProject(@PathVariable UUID workspaceId, @PathVariable UUID projectId);
}
