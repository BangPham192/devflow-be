package com.backend.devflow.mapper;

import com.backend.devflow.dtos.ProjectDto;
import com.backend.devflow.models.Project;
import com.backend.devflow.models.ProjectStatus;
import com.backend.devflow.request.ProjectCreateRequest;
import com.backend.devflow.request.ProjectUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {UUID.class, ProjectStatus.class})
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "publicId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "status", expression = "java(ProjectStatus.ACTIVE)")
    Project toEntity(ProjectCreateRequest request, Long workspaceId);

    ProjectDto toDto(Project project);

    List<ProjectDto> toDto(List<Project> projects);

    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "workspaceId", ignore = true)
    @Mapping(target = "status", ignore = true)
    Project update(@MappingTarget Project project, ProjectUpdateRequest request);
}
