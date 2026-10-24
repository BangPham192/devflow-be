package com.backend.devflow.mapper;

import com.backend.devflow.dtos.WorkspaceMemberDto;
import com.backend.devflow.models.Workspace;
import com.backend.devflow.models.WorkspaceMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkspaceMemberMapper {

    @Mapping(target = "userId", source = "user.publicId")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "avatarUrl", source = "user.avatarUrl")
    WorkspaceMemberDto toDto(WorkspaceMember workspaceMember);

    List<WorkspaceMemberDto> toDto(List<Workspace> workspaces);
}
