package com.backend.devflow.mapper;

import com.backend.devflow.dtos.WorkspaceDto;
import com.backend.devflow.models.Workspace;
import com.backend.devflow.request.WorkspaceCreateRequest;
import com.backend.devflow.request.WorkspaceUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = java.util.UUID.class,
        uses = UserMapper.class)
public interface WorkspaceMapper {
    WorkspaceMapper INSTANCE = Mappers.getMapper(WorkspaceMapper.class);

    @Mapping(target = "publicId", expression = "java(UUID.randomUUID())")
    Workspace toEntity(WorkspaceCreateRequest workspace);

    @Mapping(target = "user", source = "owner")
    WorkspaceDto toDto(Workspace workspace);

    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "status", ignore = true)
    Workspace update(@MappingTarget Workspace workspace, WorkspaceUpdateRequest workspaceUpdateRequest);
}
