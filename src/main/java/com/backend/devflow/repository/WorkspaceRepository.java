package com.backend.devflow.repository;

import com.backend.devflow.models.Workspace;
import com.backend.devflow.models.WorkspaceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
    Workspace findByPublicId(UUID uuid);

    @Query("SELECT w FROM Workspace w WHERE w.status = :status")
    Page<Workspace> findAllByStatus(@Param("status") WorkspaceStatus status, Pageable pageable);

}
