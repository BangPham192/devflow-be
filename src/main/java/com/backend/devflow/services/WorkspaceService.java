package com.backend.devflow.services;

import com.backend.devflow.dtos.WorkspaceDto;
import com.backend.devflow.http.PageRequestCustom;
import com.backend.devflow.mapper.WorkspaceMapper;
import com.backend.devflow.models.*;
import com.backend.devflow.repository.UserRepository;
import com.backend.devflow.repository.WorkspaceMemberRepository;
import com.backend.devflow.repository.WorkspaceRepository;
import com.backend.devflow.request.WorkspaceCreateRequest;
import com.backend.devflow.request.WorkspaceUpdateRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;

    @Autowired
    public WorkspaceService(WorkspaceRepository workspaceRepository, WorkspaceMemberRepository workspaceMemberRepository, UserRepository userRepository) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userRepository = userRepository;
    }

    public Page<WorkspaceDto> getAllWorkspaces(PageRequestCustom pageRequest) {
        PageRequest page = PageRequest.of(pageRequest.getPage(), pageRequest.getPageSize(), Sort.by(Sort.Direction.ASC, "createdAt"));

        Page<Workspace> workspacesPage = workspaceRepository.findAllByStatus(WorkspaceStatus.OPEN, page);
        if (workspacesPage.getTotalElements() == 0) {
            return new PageImpl<>(new ArrayList<>());
        }
        List<WorkspaceDto> workspaceDtos = workspacesPage.getContent().stream()
            .map(WorkspaceMapper.INSTANCE::toDto).toList();
        return new  PageImpl<>(workspaceDtos, page, workspacesPage.getTotalElements());
    }

    @Transactional
    public WorkspaceDto createWorkspace(WorkspaceCreateRequest request, UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        // create workspace
        Workspace workspace = WorkspaceMapper.INSTANCE.toEntity(request);
        workspace.setOwner(user);
        workspace.setStatus(WorkspaceStatus.OPEN);
        workspaceRepository.save(workspace);

        //create workspace membership
        WorkspaceMember member = new WorkspaceMember();
        member.setUser(user);
        member.setWorkspace(workspace);
        member.setRoleName(WorkspaceRole.OWNER);
        workspaceMemberRepository.save(member);
        return WorkspaceMapper.INSTANCE.toDto(workspace);
    }

    public WorkspaceDto getWorkspaceByPublicId(UUID id) {
        Workspace workspace = workspaceRepository.findByPublicId(id);
        return WorkspaceMapper.INSTANCE.toDto(workspace);
    }


    public WorkspaceDto updateWorkspace(UUID id, UserDetails currentUser, WorkspaceUpdateRequest request) {
        User user = userRepository.findByEmail(currentUser.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Workspace workspace = workspaceRepository.findByPublicId(id);
        workspace = WorkspaceMapper.INSTANCE.update(workspace, request);
        workspaceRepository.save(workspace);

        return WorkspaceMapper.INSTANCE.toDto(workspace);

    }
}
