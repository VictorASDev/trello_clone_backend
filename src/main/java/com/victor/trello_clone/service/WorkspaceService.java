package com.victor.trello_clone.service;

import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.dto.UserDto;
import com.victor.trello_clone.data.dto.WorkspaceDto;
import com.victor.trello_clone.data.record.SendInvitationRequest;
import com.victor.trello_clone.data.record.WorkspaceRequest;
import com.victor.trello_clone.exception.UserAlreadyMemberException;
import com.victor.trello_clone.mail.EmailService;
import com.victor.trello_clone.model.user.User;
import com.victor.trello_clone.model.workspace.Workspace;
import com.victor.trello_clone.model.workspace.WorkspaceMember;
import com.victor.trello_clone.repository.WorkspaceMemberRepository;
import com.victor.trello_clone.repository.WorkspaceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceService {

    private final WorkspaceRepository repository;
    private final UserService userService;
    private final EmailService emailService;
    private final JwtDecoder jwtDecoder;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public WorkspaceService(WorkspaceRepository repository,
                            UserService userService,
                            EmailService emailService,
                            JwtDecoder jwtDecoder,
                            WorkspaceMemberRepository workspaceMemberRepository) {
        this.repository = repository;
        this.userService = userService;
        this.emailService = emailService;
        this.jwtDecoder = jwtDecoder;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    public PageResponse<WorkspaceDto> findAll(Jwt token, Pageable pageable) {

        var user = userService.findById(UUID.fromString(token.getSubject()));

        var page = repository.findUsersWorkspaces(pageable, user.getId())
                .map(workspace -> new WorkspaceDto().toDto(workspace));

        return new PageResponse<>(page);
    }

    public WorkspaceDto findAccessibleWorkspaceById(
            UUID workspaceId,
            UUID userId
    ) {
        var workspace = findById(workspaceId);

        var user = userService.findById(userId);

        if (!isMember(workspace, user))
            throw new AccessDeniedException("User does not have permission");

        return new WorkspaceDto().toDto(workspace);
    }

    public Workspace findById(UUID id) {

        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workspace with ID '" + id + "' do not exists!"));
    }

    public List<UserDto> findWorkspaceMembers(UUID userId, UUID workspaceId) {

        var workspace = repository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Workspace with id: " + workspaceId + " not found!"
                ));

        var user = userService.findById(userId);

        boolean isOwner = workspace.getOwner().getId().equals(userId);

        if (!isOwner && !isMember(workspace, user)) {
            throw new AccessDeniedException("User does not have permission");
        }

        return workspaceMemberRepository
                .findUsersByWorkspaceId(workspaceId)
                .stream()
                .map(memberUser -> new UserDto().toDto(memberUser))
                .toList();
    }

    public WorkspaceDto create(UUID id, String workspaceName) {
        var workspaceCreator = userService.findById(id);

        Workspace workspace = new Workspace();

        workspace.setName(workspaceName);
        workspace.setCreatedAt(LocalDateTime.now());
        workspace.setOwner(workspaceCreator);

        repository.save(workspace);

        return new WorkspaceDto().toDto(workspace);
    }

    public void sendWorkspaceInvite(UUID token,
                                    SendInvitationRequest request,
                                    UUID workspaceId)  {
        var user = userService.findById(token);
        var workspace = repository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace with ID: " + workspaceId + " not found on data!"));
        var invitedUser = userService.findByEmail(request.invitedEmail());

        validateWorkspaceOwner(workspace, user.getId());

        if(isMember(workspace, invitedUser))
            throw new UserAlreadyMemberException("User is already a member!");

        emailService.sendAsyncWorkspaceInvite(invitedUser, workspace);
    }


    public WorkspaceDto updateName(UUID userId, WorkspaceRequest request, UUID workspaceId) {

        var workspace = repository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace with name: " + request.workspaceName() + " not found on data!"));

        var user = userService.findById(userId);

        validateWorkspaceOwner(workspace, user.getId());

        workspace.setName(request.workspaceName());
        repository.save(workspace);

        return new WorkspaceDto().toDto(workspace);
    }

    public void delete(UUID userId, String workspaceId) {

        var user = userService.findById(userId);

        var workspace = repository.findById(UUID.fromString(workspaceId))
                .orElseThrow(() -> new EntityNotFoundException("Workspace with id: " + workspaceId + " not found on data!"));


        validateWorkspaceOwner(workspace, user.getId());

        repository.delete(workspace);
    }

    public Workspace findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Workspace " + name + " not found on data!"));
    }

    public WorkspaceDto acceptWorkspaceInvite(String token, UUID id) {

        Jwt jwt = jwtDecoder.decode(token);

        if (!"workspace_invitation".equals(jwt.getClaim("type"))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid token type"
            );
        }

        User user = userService.findById(id);

        if (!user.getEmail().equals(jwt.getClaim("invited_email"))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invitation does not belong to this user"
            );
        }

        var workspace = findById(UUID.fromString(jwt.getClaim("workspace_id")));


        WorkspaceMember member = new WorkspaceMember(
                workspace,
                user,
                "MEMBER",
                Instant.now()
        );

        workspaceMemberRepository.save(member);
        workspace.getMembers().add(member);


        return new WorkspaceDto().toDto(workspace);
    }

    public void leaveWorkspace(UUID userId, UUID workspaceId) {
        repository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace with id: " + workspaceId + " not found!"));

        int deleted = workspaceMemberRepository
                .deleteByWorkspaceAndUser(workspaceId, userId);

        if (deleted == 0) {
            throw new IllegalStateException(
                    "User is not a member of this workspace"
            );
        }
    }

    public void removeMember(UUID ownerId, UUID workspaceId, UUID removedUserId) {

        var workspace = repository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace with id: " + workspaceId + " not found on data!"));

        var removedUser = userService.findById(removedUserId);

        validateWorkspaceOwner(workspace, ownerId);

        int deleted = workspaceMemberRepository
                .deleteByWorkspaceAndUser(workspaceId, removedUserId);

        if (deleted == 0) {
            throw new IllegalStateException(
                    "User is not a member of this workspace"
            );
        }
    }


    private void validateWorkspaceOwner(Workspace workspace, UUID userId) {
        if (!workspace.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("User does not have permission");
        }
    }

    private static boolean isMember(Workspace workspace, User user) {
        return workspace.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(user.getId()));
    }
}
