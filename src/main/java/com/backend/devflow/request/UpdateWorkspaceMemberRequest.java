package com.backend.devflow.request;

import com.backend.devflow.models.WorkspaceRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWorkspaceMemberRequest {

    @NotNull
    private WorkspaceRole roleName;
}
