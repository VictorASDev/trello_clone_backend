package com.victor.trello_clone.service;

import com.victor.trello_clone.model.workspace.WorkspaceRole;
import com.victor.trello_clone.repository.WorkspaceMemberRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WorkspaceAuthorizationService {

    private final WorkspaceMemberRepository workspaceMemberRepository;

    public WorkspaceAuthorizationService(WorkspaceMemberRepository workspaceMemberRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    public void validateWorkspaceMember(UUID workspaceId, UUID userId) {
        if (!workspaceMemberRepository.existsByWorkspace_WorkspaceIdAndUser_Id(workspaceId, userId)) {
            throw new AccessDeniedException("User does not have permission");
        }
    }

    public void validateWorkspaceRole(UUID workspaceId, UUID userId, WorkspaceRole role) {
        var userRole = workspaceMemberRepository
                .findRoleByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new AccessDeniedException("User does not have permission"));

        if (userRole != role) {
            throw new AccessDeniedException("User does not have permission");
        }
    }
}
