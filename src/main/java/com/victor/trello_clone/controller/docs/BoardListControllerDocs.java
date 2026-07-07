package com.victor.trello_clone.controller.docs;

import com.victor.trello_clone.data.dto.BoardListDto;
import com.victor.trello_clone.data.dto.BoardViewDto;
import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.record.CreateBoardListRequest;
import com.victor.trello_clone.data.record.UpdateBoardListRequest;
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

@Tag(name = "List Endpoint", description = "Endpoint for managing lists services!")
public interface BoardListControllerDocs {


    @Operation(
            summary = "Finds a pagination of lists on data!",
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
    public ResponseEntity<PageResponse<BoardListDto>> findAll(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("boardId") String boardId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    @Operation(
            summary = "Finds a list by it´s ID on data!",
            responses = {
                    @ApiResponse(
                            description = "Success", 
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = BoardListDto.class)
                                    )
                            }
                    ),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<BoardListDto> findById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("boardId") String boardId,
            @PathVariable("listId") String listId
    );

    @Operation(
            summary = "Creates a new list on data!",
            responses = {
                    @ApiResponse(
                            description = "Created", 
                            responseCode = "201", 
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = BoardListDto.class)
                                    ),
                            }),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<BoardListDto> createList(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("boardId") String boardId,
            @RequestBody CreateBoardListRequest request
    );

    @Operation(
            summary = "Updates a list by it`s ID!",
            responses = {
                    @ApiResponse(
                            description = "Success", 
                            responseCode = "200", 
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = BoardListDto.class)
                                    ),
                            }),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<BoardListDto> updateBoardList(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("boardId") String boardId,
            @PathVariable("listId") String listId,
            @RequestBody UpdateBoardListRequest request
    );

    @Operation(
            summary = "Deletes a list on data!",
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<Void> deleteBoardList(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("boardId") String boardId,
            @PathVariable("listId") String listId
    );
}
