package com.victor.trello_clone.service;

import com.victor.trello_clone.data.dto.BoardListDto;
import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.record.CreateBoardListRequest;
import com.victor.trello_clone.data.record.UpdateBoardListRequest;
import com.victor.trello_clone.model.boardList.BoardList;
import com.victor.trello_clone.repository.BoardListRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BoardListService {

    private final BoardListRepository repository;
    private final WorkspaceAuthorizationService workspaceAuthorizationService;
    private final BoardService boardService;

    public BoardListService(BoardListRepository repository,
                            WorkspaceAuthorizationService workspaceAuthorizationService,
                            BoardService boardService) {
        this.repository = repository;
        this.workspaceAuthorizationService = workspaceAuthorizationService;
        this.boardService = boardService;
    }

    public BoardListDto findById(UUID userId, UUID boardId, Long boardListId) {
        BoardList list =
                repository
                        .findByIdAndBoard_Id(
                                boardListId,
                                boardId
                        ).orElseThrow(() -> new EntityNotFoundException("List not found on data"));


        workspaceAuthorizationService
                .validateWorkspaceMember(
                        list.getBoard()
                                .getWorkspace()
                                .getWorkspaceId(),
                        userId
                );

        return BoardListDto.toDto(list);

    }

    public PageResponse<BoardListDto> findAll(UUID userId, UUID boardId, Pageable pageable) {

        var boardDto = boardService.findById(boardId);

        workspaceAuthorizationService.validateWorkspaceMember(
            boardDto.getWorkspace().getWorkspaceId(),
            userId
        );

        var page = repository.findByBoard_Id(boardId, pageable)
                .map(BoardListDto::toDto);

        return new PageResponse<>(page);
    }

    public BoardListDto createBoardList(UUID userId, UUID boardId, CreateBoardListRequest request) {

        var board = boardService.findEntityById(boardId);

        workspaceAuthorizationService.validateWorkspaceMember(
                board.getWorkspace().getWorkspaceId(),
                userId
        );

        var list = new BoardList();
        list.setTitle(request.title());

        Long maxPosition =
                repository.findMaxPositionByBoardId(boardId);

        System.out.println("Max position: " + maxPosition);

        var nextPosition = maxPosition == -1
                ? 1000L
                : maxPosition + 1000L;

        list.setPosition(nextPosition);
        list.setBoard(board);

        return BoardListDto.toDto(repository.save(list));
    }

    public BoardListDto updateBoardList(UUID userId, UUID boardId, Long listId, UpdateBoardListRequest request) {
        var list = repository.findByIdAndBoard_Id(listId, boardId)
                .orElseThrow(() -> new EntityNotFoundException("List not found on data!"));

        workspaceAuthorizationService.validateWorkspaceMember(
                list.getBoard().getWorkspace().getWorkspaceId(),
                userId
        );

        list.setTitle(request.title());
        list.setUpdatedAt(LocalDateTime.now());

        return BoardListDto.toDto(repository.save(list));
    }

    public void deleteBoardList(UUID userId, UUID boardId, Long listId) {
        var list = repository.findByIdAndBoard_Id(listId, boardId)
                .orElseThrow(() -> new EntityNotFoundException("List not found on data!"));

        workspaceAuthorizationService.validateWorkspaceMember(
                list.getBoard().getWorkspace().getWorkspaceId(),
                userId
        );

        repository.deleteById(listId);
    }
}
