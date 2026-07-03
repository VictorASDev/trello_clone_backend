package com.victor.trello_clone.controller;

import com.victor.trello_clone.data.dto.BoardListDto;
import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.record.CreateBoardListRequest;
import com.victor.trello_clone.data.record.UpdateBoardListRequest;
import com.victor.trello_clone.service.BoardListService;
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
@RequestMapping("/api/v1/boards/{boardId}/lists")
public class BoardListController {

    private final BoardListService service;

    public BoardListController(BoardListService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PageResponse<BoardListDto>> findAll(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("boardId") String boardId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "position"));

        return ResponseEntity.ok(service.findAll(
                UUID.fromString(jwt.getSubject()),
                UUID.fromString(boardId),
                pageable
        ));
    }

    @GetMapping("/{listId}")
    public ResponseEntity<BoardListDto> findById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("boardId") String boardId,
            @PathVariable("listId") String listId
    ) {
        return ResponseEntity.ok(service.findById(
                UUID.fromString(jwt.getSubject()),
                UUID.fromString(boardId),
                Long.parseLong(listId)
        ));
    }

    @PostMapping
    public ResponseEntity<BoardListDto> createList(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("boardId") String boardId,
            @RequestBody CreateBoardListRequest request
    ) {

        var list = service.createBoardList(
                UUID.fromString(jwt.getSubject()),
                UUID.fromString(boardId),
                request
        );

        return ResponseEntity.created(
                URI.create(
                        "/api/v1/boards/"
                                + boardId
                                + "/lists/"
                                + list.getId()))
                .body(list);
    }

    @PutMapping("/{listId}")
    public ResponseEntity<BoardListDto> updateBoardList(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("boardId") String boardId,
            @PathVariable("listId") String listId,
            @RequestBody UpdateBoardListRequest request
    ) {
        return ResponseEntity.ok(
                service.updateBoardList(
                        UUID.fromString(jwt.getSubject()),
                        UUID.fromString(boardId),
                        Long.parseLong(listId),
                        request
                )
        );
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteBoardList(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("boardId") String boardId,
            @PathVariable("listId") String listId
    ) {
        service.deleteBoardList(
                UUID.fromString(jwt.getSubject()),
                UUID.fromString(boardId),
                Long.parseLong(listId)
        );
        return ResponseEntity.noContent().build();
    }

}
