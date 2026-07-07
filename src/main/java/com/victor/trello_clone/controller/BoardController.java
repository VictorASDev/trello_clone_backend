package com.victor.trello_clone.controller;

import com.victor.trello_clone.controller.docs.BoardControllerDocs;
import com.victor.trello_clone.data.dto.BoardDto;
import com.victor.trello_clone.data.dto.BoardViewDto;
import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.record.CreateBoardRequest;
import com.victor.trello_clone.data.record.UpdateBoardRequest;
import com.victor.trello_clone.service.BoardService;
import com.victor.trello_clone.service.BoardViewService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/boards")
public class BoardController implements BoardControllerDocs {

    private final BoardService service;
    private final BoardViewService boardViewService;

    public BoardController(BoardService service, BoardViewService boardViewService) {
        this.service = service;
        this.boardViewService = boardViewService;
    }

    @GetMapping
    @Override
    public ResponseEntity<PageResponse<BoardDto>> findAll(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @PathVariable("workspaceId") String workspaceId
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));

        return ResponseEntity.ok(
                service.findAll(
                        UUID.fromString(jwt.getSubject()),
                        UUID.fromString(workspaceId),
                        pageable
                )
        );
    }

    @GetMapping("/{boardId}")
    @Override
    public ResponseEntity<BoardDto> findById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID boardId
    ) {
        return ResponseEntity.ok(
                service.findById(
                        UUID.fromString(jwt.getSubject()),
                        boardId
                )
        );
    }

    @GetMapping("/{boardId}/view")
    @Override
    public ResponseEntity<BoardViewDto> getBoardView(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID boardId
    ) {
        return ResponseEntity.ok(
                boardViewService.getBoardView(
                        UUID.fromString(jwt.getSubject()),
                        boardId
                )
        );
    }

    @PostMapping
    @Override
    public ResponseEntity<BoardDto> createBoard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("workspaceId") String workspaceId,
            @RequestBody CreateBoardRequest request) {

        BoardDto board = service.createBoard(
                UUID.fromString(jwt.getSubject()),
                UUID.fromString(workspaceId),
                request
        );

        return ResponseEntity.created(
                URI.create(
                        "/api/v1/workspaces/" +
                                workspaceId +
                                "/boards/" +
                                board.getId()
                )
        ).body(board);
    }

    @DeleteMapping("/{boardId}")
    @Override
    public ResponseEntity<Void> deleteBoard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("boardId") String boardId
    ) {

        service.deleteBoard(
                UUID.fromString(jwt.getSubject()),
                UUID.fromString(boardId)
        );

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{boardId}")
    @Override
    public ResponseEntity<Void> updateBoard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("boardId") String boardId,
            @RequestBody UpdateBoardRequest request
    ) {
        service.updateBoard(
                UUID.fromString(jwt.getSubject()),
                UUID.fromString(boardId),
                request
        );

        return ResponseEntity.ok().build();
    }
}
