package com.backend.devflow.interfaces;

import com.backend.devflow.dtos.WorkspaceMemberDto;
import com.backend.devflow.request.UpdateWorkspaceMemberRequest;
import jakarta.validation.Valid;
import org.hibernate.sql.Update;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/members")
public interface IWorkspaceMemberController {

    @GetMapping
    List<WorkspaceMemberDto> getMembers(@PathVariable UUID workspaceId);

    @PatchMapping("/{userId}")
    WorkspaceMemberDto updateMemberRole(@PathVariable UUID workspaceId,
                                        @PathVariable UUID userId,
                                        @Valid @RequestBody UpdateWorkspaceMemberRequest request);

    @DeleteMapping("{userId}")
    void removeMember(@PathVariable UUID workspaceId, @PathVariable UUID userId);
}
