package com.victor.trello_clone.controller.docs;

import com.victor.trello_clone.data.dto.BoardViewDto;
import com.victor.trello_clone.data.dto.CardDto;
import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.record.CreateCardRequest;
import com.victor.trello_clone.data.record.MoveCardRequest;
import com.victor.trello_clone.data.record.UpdateCardRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Card Endpoint", description = "Endpoint for managing card services!")
public interface CardControllerDocs {


    @Operation(
            summary = "Finds a pagination of cards on data!",
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
    public ResponseEntity<PageResponse<CardDto>> findAll(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("listId") String listId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    @Operation(
            summary = "Finds a card by it`s ID on data!",
            responses = {
                    @ApiResponse(
                            description = "Success", 
                            responseCode = "200", 
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = CardDto.class)
                                    ),
                            }),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<CardDto> findById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("listId") String listId,
            @PathVariable("cardId") String cardId
    );

    @Operation(
            summary = "Creates a new card on data!",
            responses = {
                    @ApiResponse(
                            description = "Created", 
                            responseCode = "201", 
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = CardDto.class)
                                    ),
                            }),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<CardDto> createCard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("listId") String listId,
            @Valid @RequestBody CreateCardRequest request
    );

    @Operation(
            summary = "Updates a card by it`s ID on data!",
            responses = {
                    @ApiResponse(
                            description = "Success", 
                            responseCode = "200", 
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = CardDto.class)
                                    ),
                            }),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<CardDto> updateBoardList(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("listId") String listId,
            @PathVariable("cardId") String cardId,
            @Valid @RequestBody UpdateCardRequest request
    );

    @Operation(
            summary = "Deletes a card by it´s ID on data!",
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
            @PathVariable("listId") String listId,
            @PathVariable("cardId") String cardId
    );

    @Operation(
            summary = "Control cards position",
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<Void> moveCard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("listId") String listId,
            @PathVariable("cardId") String cardId,
            @RequestBody MoveCardRequest request);
}
