package com.victor.trello_clone.controller.docs;

import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.dto.UserDto;
import com.victor.trello_clone.data.dto.WorkspaceDto;
import com.victor.trello_clone.data.record.SendInvitationRequest;
import com.victor.trello_clone.data.record.TokenRequest;
import com.victor.trello_clone.data.record.WorkspaceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Workspace Endpoint",
        description = "Endpoint responsible for managing workspaces, members, invitations and workspace operations."
)
public interface WorkspaceControllerDocs {

    @Operation(
            summary = "List all user workspaces",
            description = "Returns a paginated list of all workspaces accessible by the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<PageResponse<WorkspaceDto>> findAll(
            Jwt jwt,
            Integer page,
            Integer size,
            String direction
    );

    @Operation(
            summary = "Find workspace by id",
            description = "Returns a workspace if the authenticated user has access to it.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Workspace Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<WorkspaceDto> findById(UUID workspaceId, Jwt jwt);

    @Operation(
            summary = "List workspace members",
            description = "Returns all members belonging to a workspace accessible by the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Workspace Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<List<UserDto>> getMembers(UUID workspaceId, Jwt jwt);

    @Operation(
            summary = "Send workspace invitation",
            description = "Sends an invitation to a user so they can join the workspace.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Invitation Sent", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Workspace or User Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<Void> sendInvitation(
            Jwt jwt,
            SendInvitationRequest request,
            UUID workspaceId
    );

    @Operation(
            summary = "Accept workspace invitation",
            description = "Validates the invitation token and adds the authenticated user to the workspace.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Invitation Accepted", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Invitation Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<WorkspaceDto> acceptInvitation(
            Jwt jwt,
            TokenRequest invitationToken
    );

    @Operation(
            summary = "Update workspace name",
            description = "Updates the name of an existing workspace.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Workspace Updated", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Workspace Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<WorkspaceDto> updateWorkspaceName(
            Jwt jwt,
            WorkspaceRequest workspaceRequest,
            String workspaceId
    );

    @Operation(
            summary = "Create workspace",
            description = "Creates a new workspace for the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Workspace Created", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<WorkspaceDto> create(
            WorkspaceRequest workspaceRequest,
            Jwt jwt
    );

    @Operation(
            summary = "Leave workspace",
            description = "Removes the authenticated user from the workspace membership.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Workspace Left Successfully", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Workspace Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<Void> leaveWorkspace(
            Jwt jwt,
            String workspaceId
    );

    @Operation(
            summary = "Remove workspace member",
            description = "Removes a member from the workspace. Requires appropriate permissions.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Member Removed", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Workspace or Member Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<Void> deleteWorkspaceMember(
            Jwt jwt,
            String workspaceId,
            String memberId
    );

    @Operation(
            summary = "Delete workspace",
            description = "Deletes a workspace and all associated resources.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Workspace Deleted", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Workspace Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<Void> deleteWorkspace(
            Jwt jwt,
            String workspaceId
    );
}