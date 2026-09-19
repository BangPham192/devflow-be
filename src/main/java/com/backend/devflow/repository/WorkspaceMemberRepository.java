package com.backend.devflow.repository;

import com.backend.devflow.models.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long>
{
}
