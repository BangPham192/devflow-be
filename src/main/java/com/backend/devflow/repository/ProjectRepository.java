package com.backend.devflow.repository;

import com.backend.devflow.models.Project;
import com.backend.devflow.models.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByPublicId(UUID publicId);

    Page<Project> findAllByWorkspaceIdAndStatus(Long workspaceId, ProjectStatus status, Pageable pageable);
}
