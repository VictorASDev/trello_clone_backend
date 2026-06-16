package com.victor.trello_clone.controller;

import com.victor.trello_clone.controller.docs.WorkspaceControllerDocs;
import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.dto.WorkspaceMemberDto;
import com.victor.trello_clone.data.dto.WorkspaceDto;
import com.victor.trello_clone.data.record.SendInvitationRequest;
import com.victor.trello_clone.data.record.TokenRequest;
import com.victor.trello_clone.data.record.WorkspaceRequest;
import com.victor.trello_clone.service.WorkspaceService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController implements WorkspaceControllerDocs {

    private final WorkspaceService service;

    public WorkspaceController(WorkspaceService service) {
        this.service = service;
    }

    @GetMapping
    @Override
    public ResponseEntity<PageResponse<WorkspaceDto>> findAll(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "workspace.name"));

        return ResponseEntity.ok(service.findAll(UUID.fromString(jwt.getSubject()), pageable));
    }

    @GetMapping("/{workspaceId}")
    @Override
    public ResponseEntity<WorkspaceDto> findById(@PathVariable("workspaceId") UUID workspaceId,
                                                 @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.findAccessibleWorkspaceById(
                workspaceId,
                UUID.fromString(jwt.getSubject())));
    }

    @GetMapping("/{workspaceId}/members")
    @Override
    public ResponseEntity<List<WorkspaceMemberDto>> getMembers(@PathVariable("workspaceId") UUID workspaceId,
                                                    @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(service.findWorkspaceMembers(
                UUID.fromString(jwt.getSubject()),
                workspaceId));
    }

    @PostMapping("/{workspaceId}/invitations")
    @Override
    public ResponseEntity<Void> sendInvitation(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody SendInvitationRequest request,
            @PathVariable UUID workspaceId) {
        service.sendWorkspaceInvite(
                UUID.fromString(jwt.getSubject()),
                request,
                workspaceId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/invitations/accept")
    @Override
    public ResponseEntity<WorkspaceDto> acceptInvitation(@AuthenticationPrincipal Jwt jwt,
                                                         @RequestBody TokenRequest invitationToken) {
        var response = service.acceptWorkspaceInvite(
                invitationToken.token(),
                UUID.fromString(jwt.getSubject()));

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{workspaceId}/updateName")
    @Override
    public ResponseEntity<WorkspaceDto> updateWorkspaceName(@AuthenticationPrincipal Jwt jwt,
                                                            @RequestBody WorkspaceRequest workspaceRequest,
                                                            @PathVariable("workspaceId") String workspaceId) {

        return ResponseEntity.ok(service.updateName(
                UUID.fromString(jwt.getSubject()),
                workspaceRequest,
                UUID.fromString(workspaceId)));
    }

    @PostMapping
    @Override
    public ResponseEntity<WorkspaceDto> create(@RequestBody WorkspaceRequest workspaceRequest,
                                               @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(service.create(
                UUID.fromString(jwt.getSubject()),
                workspaceRequest.workspaceName())
        );
    }

    @DeleteMapping("/{workspaceId}/leave")
    @Override
    public ResponseEntity<Void> leaveWorkspace(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("workspaceId") String workspaceId) {

        service.leaveWorkspace(
                UUID.fromString(jwt.getSubject()),
                UUID.fromString(workspaceId)
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{workspaceId}/members/{memberId}")
    @Override
    public ResponseEntity<Void> deleteWorkspaceMember(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("memberId") String memberId) {

         service.removeMember(
                UUID.fromString(jwt.getSubject()),
                UUID.fromString(workspaceId),
                UUID.fromString(memberId)
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteWorkspace(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable("id") String workspaceId) {
        service.delete(
                UUID.fromString(jwt.getSubject()),
                workspaceId);
        return ResponseEntity.noContent().build();
    }
}
