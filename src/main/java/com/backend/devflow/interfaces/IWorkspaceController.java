package com.backend.devflow.interfaces;

import com.backend.devflow.dtos.WorkspaceDto;
import com.backend.devflow.http.PageRequestCustom;
import com.backend.devflow.request.WorkspaceCreateRequest;
import com.backend.devflow.request.WorkspaceUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces")
public interface IWorkspaceController {

    @GetMapping("/getAll")
    Page<WorkspaceDto> getAllWorkspaces(PageRequestCustom pageRequest);

    @PostMapping
    WorkspaceDto createWorkspace(@RequestBody @Valid WorkspaceCreateRequest workspace);

    @GetMapping("/{workspaceId}")
    WorkspaceDto getWorkspace(@PathVariable UUID workspaceId);

    @PatchMapping("/{workspaceId}")
    WorkspaceDto updateWorkspace(@PathVariable UUID workspaceId, @RequestBody @Valid WorkspaceUpdateRequest workspace);

    @DeleteMapping("/{workspaceId}")
    void deleteWorkspace(@PathVariable UUID workspaceId);

}
