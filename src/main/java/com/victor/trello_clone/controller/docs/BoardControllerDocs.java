package com.victor.trello_clone.controller.docs;

import com.victor.trello_clone.data.dto.BoardDto;
import com.victor.trello_clone.data.dto.BoardViewDto;
import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.record.CreateBoardRequest;
import com.victor.trello_clone.data.record.UpdateBoardRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Board Endpoint", description = "Endpoint for managing boards services!")
public interface BoardControllerDocs {

    @Operation(
            summary = "Finds a pagination of boards on data!",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = PageResponse.class)
                                    ),
                            }),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<PageResponse<BoardDto>> findAll(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @PathVariable("workspaceId") String workspaceId
    );

    @Operation(
            summary = "Find a board by it´s ID!",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = BoardDto.class)
                                    ),
                            }),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<BoardDto> findById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID boardId
    );

    @Operation(
            summary = "Find a board view by it´s ID!",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = BoardViewDto.class)
                                    ),
                            }),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<BoardViewDto> getBoardView(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID boardId
    );

    @Operation(
            summary = "Creates a new board on data!",
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = BoardDto.class)
                                    ),
                            }
                    ),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<BoardDto> createBoard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("workspaceId") String workspaceId,
            @RequestBody CreateBoardRequest request);

    @Operation(
            summary = "Find a board by it´s ID!",
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<Void> deleteBoard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("boardId") String boardId
    );

    @Operation(
            summary = "Find a board by it´s ID!",
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<Void> updateBoard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("boardId") String boardId,
            @RequestBody UpdateBoardRequest request
    );
}
