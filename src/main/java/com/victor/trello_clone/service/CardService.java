package com.victor.trello_clone.service;

import com.victor.trello_clone.data.dto.CardDto;
import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.record.CreateCardRequest;
import com.victor.trello_clone.data.record.UpdateCardRequest;
import com.victor.trello_clone.model.card.Card;
import com.victor.trello_clone.repository.CardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CardService {

    private final CardRepository repository;
    private final WorkspaceAuthorizationService workspaceAuthorizationService;
    private final BoardListService boardListService;

    public CardService(CardRepository repository,
                       WorkspaceAuthorizationService workspaceAuthorizationService,
                       BoardListService boardListService) {
        this.repository = repository;
        this.workspaceAuthorizationService = workspaceAuthorizationService;
        this.boardListService = boardListService;
    }

    public PageResponse<CardDto> findAll(UUID userId, Long listId, Pageable pageable) {
        var list = boardListService.findEntityById(listId);

        workspaceAuthorizationService.validateWorkspaceMember(
                list.getBoard().getWorkspace().getWorkspaceId(),
                userId
        );

        var cards = repository.findByList_Id(listId, pageable)
                .map(CardDto::toDto);

        return new PageResponse<>(cards);
    }

    public CardDto findById(UUID userId, Long listId, Long cardId) {
        var card =findEntityById(listId,cardId);

        workspaceAuthorizationService.validateWorkspaceMember(
                card.getList().getBoard().getWorkspace().getWorkspaceId(),
                userId
        );

        return CardDto.toDto(card);
    }

    public CardDto createCard(UUID userId, Long listId, CreateCardRequest request) {

        var list = boardListService.findEntityById(listId);

        workspaceAuthorizationService.validateWorkspaceMember(
                list.getBoard().getWorkspace().getWorkspaceId(),
                userId
        );

        var newCard = new Card();

        newCard.setTitle(request.title());
        newCard.setDescription(request.description());
        newCard.setList(list);

        Long maxPosition =
                repository.findMaxPositionByListId(listId);

        var nextPosition = maxPosition == -1
                ? 1000L
                : maxPosition + 1000L;

        newCard.setPosition(nextPosition);

        repository.save(newCard);

        return CardDto.toDto(newCard);
    }

    public CardDto updateCard(UUID userId, Long listId, Long cardId, UpdateCardRequest request) {

        var card =findEntityById(listId,cardId);

        workspaceAuthorizationService.validateWorkspaceMember(
                card.getList().getBoard().getWorkspace().getWorkspaceId(),
                userId
        );

        card.setTitle(request.title());
        card.setDescription(request.description());

        repository.save(card);

        return CardDto.toDto(card);
    }

    public void deleteCard(UUID userId, Long listId, Long cardId) {
        var card =findEntityById(listId,cardId);

        workspaceAuthorizationService.validateWorkspaceMember(
                card.getList().getBoard().getWorkspace().getWorkspaceId(),
                userId
        );

        repository.delete(card);
    }

    public Card findEntityById(Long listId, Long cardId) {
        return repository.findByIdAndList_Id(cardId, listId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Card not found"));
    }
}
