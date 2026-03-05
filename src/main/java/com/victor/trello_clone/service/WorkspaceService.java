package com.victor.trello_clone.service;

import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.dto.WorkspaceDto;
import com.victor.trello_clone.model.Workspace;
import com.victor.trello_clone.repository.WorkspaceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WorkspaceService {

    private final WorkspaceRepository repository;
    private final JwtDecoder jwtDecoder;
    private final UserService userService;

    public WorkspaceService(WorkspaceRepository repository,
                            JwtDecoder jwtDecoder,
                            UserService userService) {
        this.repository = repository;
        this.jwtDecoder = jwtDecoder;
        this.userService = userService;
    }

    public PageResponse<WorkspaceDto> findAll(Jwt token, Pageable pageable) {

        var user = userService.findById(UUID.fromString(token.getSubject()));

        var page = repository.findUsersWorkspaces(pageable, user.getId())
                .map(workspace -> {
                    return new WorkspaceDto(
                            workspace.getWorkspaceId(),
                            workspace.getName(),
                            workspace.getCreatedAt(),
                            workspace.getOwner()
                    );
                });

        return new PageResponse<>(page);
    }

    public WorkspaceDto findById(UUID id) {
        var workspace = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workspace with ID '" + id + "' do not exists!"));

        return new WorkspaceDto(
                workspace.getWorkspaceId(),
                workspace.getName(),
                workspace.getCreatedAt(),
                workspace.getOwner()
        );
    }

    public WorkspaceDto create(String token, String workspaceName) {
        Jwt tokenJwt = jwtDecoder.decode(token);

        var workspaceCreator = userService.findById(UUID.fromString(tokenJwt.getSubject()));

        Workspace workspace = new Workspace();

        workspace.setName(workspaceName);
        workspace.setCreatedAt(LocalDateTime.now());
        workspace.setOwner(workspaceCreator);

        repository.save(workspace);

        return new WorkspaceDto(
                workspace.getWorkspaceId(),
                workspace.getName(),
                workspace.getCreatedAt(),
                workspace.getOwner()
        );
    }
}
