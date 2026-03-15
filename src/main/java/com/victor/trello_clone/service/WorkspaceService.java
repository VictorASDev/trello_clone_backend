package com.victor.trello_clone.service;

import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.dto.WorkspaceDto;
import com.victor.trello_clone.data.record.SendInvitationRequest;
import com.victor.trello_clone.data.record.WorkspaceRequest;
import com.victor.trello_clone.mail.EmailService;
import com.victor.trello_clone.model.user.User;
import com.victor.trello_clone.model.workspace.Workspace;
import com.victor.trello_clone.model.workspace.WorkspaceMember;
import com.victor.trello_clone.repository.WorkspaceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WorkspaceService {

    private final WorkspaceRepository repository;
    private final UserService userService;
    private final EmailService emailService;
    private final JwtDecoder jwtDecoder;

    public WorkspaceService(WorkspaceRepository repository,
                            UserService userService,
                            EmailService emailService,
                            JwtDecoder jwtDecoder) {
        this.repository = repository;
        this.userService = userService;
        this.emailService = emailService;
        this.jwtDecoder = jwtDecoder;
    }

    public PageResponse<WorkspaceDto> findAll(Jwt token, Pageable pageable) {

        var user = userService.findById(UUID.fromString(token.getSubject()));

        var page = repository.findUsersWorkspaces(pageable, user.getId())
                .map(workspace -> new WorkspaceDto().toDto(workspace));

        return new PageResponse<>(page);
    }

    public WorkspaceDto findById(Jwt jwt, UUID id) {

        var user = userService.findById(UUID.fromString(jwt.getSubject()));

        var workspace = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workspace with ID '" + id + "' do not exists!"));

        boolean isMember = workspace.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(user.getId()));

        if (!isMember)
            throw new AccessDeniedException("User does not have permission");

        return new WorkspaceDto().toDto(workspace);
    }

    public WorkspaceDto create(Jwt token, String workspaceName) {
        var workspaceCreator = userService.findById(UUID.fromString(token.getSubject()));

        Workspace workspace = new Workspace();

        workspace.setName(workspaceName);
        workspace.setCreatedAt(LocalDateTime.now());
        workspace.setOwner(workspaceCreator);

        repository.save(workspace);

        return new WorkspaceDto().toDto(workspace);
    }

    public void sendWorkspaceInvite(UUID userID,
                                    SendInvitationRequest request,
                                    UUID workspaceId) {
        var user = userService.findById(userID);
        var workspace = repository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace with ID: " + workspaceId + " not found on data!"));

        validateWorkspaceOwner(workspace, user.getId());

        emailService.sendAsyncWorkspaceInvite(request.invitedEmail(), workspace.getName());

    }

    public WorkspaceDto updateName(UUID userID, WorkspaceRequest request, UUID workspaceId) {

        var workspace = repository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace with name: " + request.workspaceName() + " not found on data!"));

        var user = userService.findById(userID);

        validateWorkspaceOwner(workspace, user.getId());

        workspace.setName(request.workspaceName());
        repository.save(workspace);

        return new WorkspaceDto().toDto(workspace);
    }

    public void delete(UUID userID, String workspaceId) {

        var user = userService.findById(userID);

        var workspace = repository.findById(UUID.fromString(workspaceId))
                .orElseThrow(() -> new EntityNotFoundException("Workspace with id: " + workspaceId + " not found on data!"));


        validateWorkspaceOwner(workspace, user.getId());

        repository.delete(workspace);
    }


    private void validateWorkspaceOwner(Workspace workspace, UUID userId) {
        if (!workspace.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("User does not have permission");
        }
    }

    public Workspace findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Workspace " + name + " not found on data!"));
    }

    public void acceptWorkspaceInvite(String token) {

        Jwt jwt = jwtDecoder.decode(token);

        if (!"workspace_invitation".equals(jwt.getClaim("type"))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid token type"
            );
        }

        User user = userService.findById(UUID.fromString(jwt.getSubject()));

        if (!user.getEmail().equals(jwt.getClaim("invited_email"))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid token type"
            );
        }

        var workspace = findByName(jwt.getClaim("workspace"));

        workspace.getMembers().add(new WorkspaceMember(
                workspace,
                user,
                "User",
                Instant.now()
        ));
    }
}
