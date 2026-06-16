package com.victor.trello_clone.service;

import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.dto.WorkspaceDto;
import com.victor.trello_clone.data.dto.WorkspaceMemberDto;
import com.victor.trello_clone.data.record.SendInvitationRequest;
import com.victor.trello_clone.data.record.WorkspaceRequest;
import com.victor.trello_clone.exception.UserAlreadyMemberException;
import com.victor.trello_clone.mail.EmailService;
import com.victor.trello_clone.model.user.User;
import com.victor.trello_clone.model.workspace.Workspace;
import com.victor.trello_clone.model.workspace.WorkspaceMember;
import com.victor.trello_clone.model.workspace.WorkspaceRole;
import com.victor.trello_clone.repository.WorkspaceMemberRepository;
import com.victor.trello_clone.repository.WorkspaceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    private final WorkspaceAuthorizationService workspaceAuthorizationService;

    public WorkspaceService(WorkspaceRepository repository,
                            UserService userService,
                            EmailService emailService,
                            JwtDecoder jwtDecoder,
                            WorkspaceMemberRepository workspaceMemberRepository,
                            WorkspaceAuthorizationService workspaceAuthorizationService) {
        this.repository = repository;
        this.userService = userService;
        this.emailService = emailService;
        this.jwtDecoder = jwtDecoder;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workspaceAuthorizationService = workspaceAuthorizationService;
    }

    public PageResponse<WorkspaceDto> findAll(UUID userId, Pageable pageable) {

        var user = userService.findById(userId);

        var page = repository.findUsersWorkspaces(pageable, user.getId())
                .map(workspace -> new WorkspaceDto().toDto(workspace));

        return new PageResponse<>(page);
    }

    public WorkspaceDto findAccessibleWorkspaceById(
            UUID workspaceId,
            UUID userId
    ) {
        workspaceAuthorizationService.validateWorkspaceMember(workspaceId, userId);

        var workspace = findById(workspaceId);
        return new WorkspaceDto().toDto(workspace);
    }

    public Workspace findById(UUID id) {

        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workspace with ID '" + id + "' do not exists!"));
    }

    public List<WorkspaceMemberDto> findWorkspaceMembers(UUID userId, UUID workspaceId) {

        repository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Workspace with id: " + workspaceId + " not found!"
                ));

        workspaceAuthorizationService.validateWorkspaceMember(workspaceId, userId);

        return workspaceMemberRepository
                .findMembersByWorkspaceId(workspaceId)
                .stream()
                .map(member -> new WorkspaceMemberDto().toDto(member))
                .toList();
    }

    public WorkspaceDto create(UUID id, String workspaceName) {
        var workspaceCreator = userService.findById(id);

        Workspace workspace = new Workspace();

        workspace.setName(workspaceName);
        workspace.setCreatedAt(LocalDateTime.now());

        repository.save(workspace);

        WorkspaceMember adminMember = new WorkspaceMember(
                workspace,
                workspaceCreator,
                WorkspaceRole.ADMIN,
                Instant.now()
        );

        workspaceMemberRepository.save(adminMember);
        workspace.getMembers().add(adminMember);

        return new WorkspaceDto().toDto(workspace);
    }

    public void sendWorkspaceInvite(UUID token,
                                    SendInvitationRequest request,
                                    UUID workspaceId)  {
        var user = userService.findById(token);
        var workspace = repository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace with ID: " + workspaceId + " not found on data!"));
        var invitedUser = userService.findByEmail(request.invitedEmail());

        workspaceAuthorizationService.validateWorkspaceRole(workspaceId, user.getId(), WorkspaceRole.ADMIN);

        if (workspaceMemberRepository.existsByWorkspace_WorkspaceIdAndUser_Id(workspaceId, invitedUser.getId()))
            throw new UserAlreadyMemberException("User is already a member!");

        emailService.sendAsyncWorkspaceInvite(invitedUser, workspace);
    }


    public WorkspaceDto updateName(UUID userId, WorkspaceRequest request, UUID workspaceId) {

        var workspace = repository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace with name: " + request.workspaceName() + " not found on data!"));

        workspaceAuthorizationService.validateWorkspaceRole(workspaceId, userId, WorkspaceRole.ADMIN);

        workspace.setName(request.workspaceName());
        repository.save(workspace);

        return new WorkspaceDto().toDto(workspace);
    }

    public void delete(UUID userId, String workspaceId) {

        userService.findById(userId);

        var workspace = repository.findById(UUID.fromString(workspaceId))
                .orElseThrow(() -> new EntityNotFoundException("Workspace with id: " + workspaceId + " not found on data!"));

        workspaceAuthorizationService.validateWorkspaceRole(
                UUID.fromString(workspaceId),
                userId,
                WorkspaceRole.ADMIN
        );

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

        if (workspaceMemberRepository.existsByWorkspace_WorkspaceIdAndUser_Id(
                workspace.getWorkspaceId(),
                user.getId()
        )) {
            throw new UserAlreadyMemberException("User is already a member!");
        }

        WorkspaceMember member = new WorkspaceMember(
                workspace,
                user,
                WorkspaceRole.MEMBER,
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

    public void removeMember(UUID adminId, UUID workspaceId, UUID removedUserId) {

        repository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace with id: " + workspaceId + " not found on data!"));

        userService.findById(removedUserId);

        workspaceAuthorizationService.validateWorkspaceRole(workspaceId, adminId, WorkspaceRole.ADMIN);

        int deleted = workspaceMemberRepository
                .deleteByWorkspaceAndUser(workspaceId, removedUserId);

        if (deleted == 0) {
            throw new IllegalStateException(
                    "User is not a member of this workspace"
            );
        }
    }
}
