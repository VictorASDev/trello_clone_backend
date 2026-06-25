package com.victor.trello_clone.service;

import com.victor.trello_clone.data.dto.BoardListViewDto;
import com.victor.trello_clone.data.dto.BoardViewDto;
import com.victor.trello_clone.data.dto.CardDto;
import com.victor.trello_clone.model.boardList.BoardList;
import com.victor.trello_clone.model.card.Card;
import com.victor.trello_clone.repository.BoardListRepository;
import com.victor.trello_clone.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BoardViewService {

    private final BoardService boardService;
    private final BoardListRepository boardListRepository;
    private final CardRepository cardRepository;
    private final WorkspaceAuthorizationService authorizationService;

    public BoardViewService(
            BoardService boardService,
            BoardListRepository boardListRepository,
            CardRepository cardRepository,
            WorkspaceAuthorizationService authorizationService
    ) {
        this.boardService = boardService;
        this.boardListRepository = boardListRepository;
        this.cardRepository = cardRepository;
        this.authorizationService = authorizationService;
    }

    public BoardViewDto getBoardView(
            UUID userId,
            UUID boardId
    ) {

        var board = boardService.findEntityById(boardId);

        authorizationService.validateWorkspaceMember(
                board.getWorkspace().getWorkspaceId(),
                userId
        );

        List<BoardList> lists =
                boardListRepository
                        .findByBoard_IdOrderByPosition(boardId);

        List<Card> cards =
                cardRepository.findByBoardId(boardId);

        Map<Long, List<CardDto>> cardsByList =
                cards.stream()
                        .map(CardDto::toDto)
                        .collect(Collectors.groupingBy(
                                card -> card.getList().getId()
                        ));

        List<BoardListViewDto> listViews =
                lists.stream()
                        .map(list -> {

                            var dto = new BoardListViewDto();

                            dto.setId(list.getId());
                            dto.setTitle(list.getTitle());
                            dto.setPosition(list.getPosition());

                            dto.setCards(
                                    cardsByList.getOrDefault(
                                            list.getId(),
                                            Collections.emptyList()
                                    )
                            );

                            return dto;
                        })
                        .toList();

        var response = new BoardViewDto();

        response.setId(board.getId());
        response.setName(board.getName());
        response.setBackgroundColor(board.getBackgroundColor());
        response.setLists(listViews);

        return response;
    }
}