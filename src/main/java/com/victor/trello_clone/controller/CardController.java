package com.victor.trello_clone.controller;

import com.victor.trello_clone.data.dto.CardDto;
import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.record.CreateCardRequest;
import com.victor.trello_clone.data.record.UpdateCardRequest;
import com.victor.trello_clone.service.CardService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/lists/{listId}/cards")
public class CardController {

    private final CardService service;

    public CardController(CardService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PageResponse<CardDto>> findAll(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("listId") String listId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "position"));

        return ResponseEntity.ok(service.findAll(
                UUID.fromString(jwt.getSubject()),
                Long.parseLong(listId),
                pageable
        ));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardDto> findById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("listId") String listId,
            @PathVariable("cardId") String cardId
    ) {
        return ResponseEntity.ok(service.findById(
                UUID.fromString(jwt.getSubject()),
                Long.parseLong(listId),
                Long.parseLong(cardId)
        ));
    }

    @PostMapping
    public ResponseEntity<CardDto> createCard(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("listId") String listId,
            @Valid @RequestBody CreateCardRequest request
    ) {

        var card = service.createCard(
                UUID.fromString(jwt.getSubject()),
                Long.parseLong(listId),
                request
        );

        return ResponseEntity.created(
                        URI.create(
                                "/api/v1/lists/"
                                        + listId
                                        + "/cards/"
                                        + card.getId()))
                .body(card);
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<CardDto> updateBoardList(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("listId") String listId,
            @PathVariable("cardId") String cardId,
            @Valid @RequestBody UpdateCardRequest request
    ) {
        return ResponseEntity.ok(
                service.updateCard(
                        UUID.fromString(jwt.getSubject()),
                        Long.parseLong(listId),
                        Long.parseLong(cardId),
                        request
                )
        );
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteBoardList(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("listId") String listId,
            @PathVariable("cardId") String cardId
    ) {
        service.deleteCard(
                UUID.fromString(jwt.getSubject()),
                Long.parseLong(listId),
                Long.parseLong(cardId)
        );

        return ResponseEntity.noContent().build();
    }
}
