package com.victor.trello_clone.controller;

import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.dto.WorkspaceDto;
import com.victor.trello_clone.data.record.SendInvitationRequest;
import com.victor.trello_clone.data.record.WorkspaceRequest;
import com.victor.trello_clone.service.WorkspaceService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController {

    private final WorkspaceService service;

    public WorkspaceController(WorkspaceService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<PageResponse<WorkspaceDto>> findAll(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));

        return ResponseEntity.ok(service.findAll(jwt, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceDto> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping("/{workspaceId}/invitations")
    public ResponseEntity<Void> sendInvitation(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody SendInvitationRequest request,
            @PathVariable UUID workspaceId) {
        service.sendWorkspaceInvite(UUID.fromString(jwt.getSubject()), request, workspaceId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{workspaceId}/updateName")
    public ResponseEntity<WorkspaceDto> updateWorkspaceName(@AuthenticationPrincipal Jwt jwt,
                                                            @RequestBody WorkspaceRequest workspaceRequest,
                                                            @PathVariable("workspaceId") UUID workspaceId) {

        return ResponseEntity.ok(service.updateName(
                UUID.fromString(jwt.getSubject()),
                workspaceRequest,
                workspaceId));
    }

    @PostMapping
    public ResponseEntity<WorkspaceDto> create(@RequestBody WorkspaceRequest workspaceRequest,
                                               @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(service.create(jwt, workspaceRequest.workspaceName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable("id") String workspaceId) {
        service.delete(
                UUID.fromString(jwt.getSubject()),
                workspaceId);
        return ResponseEntity.noContent().build();
    }
}
