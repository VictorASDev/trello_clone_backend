package com.victor.trello_clone.service;

import com.victor.trello_clone.data.dto.BoardDto;
import com.victor.trello_clone.data.dto.PageResponse;
import com.victor.trello_clone.data.record.CreateBoardRequest;
import com.victor.trello_clone.data.record.UpdateBoardRequest;
import com.victor.trello_clone.model.board.Board;
import com.victor.trello_clone.model.workspace.Workspace;
import com.victor.trello_clone.model.workspace.WorkspaceRole;
import com.victor.trello_clone.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BoardService {

    private final BoardRepository repository;
    private final WorkspaceService workspaceService;
    private final WorkspaceAuthorizationService workspaceAuthorizationService;


    public BoardService(BoardRepository repository,
                        WorkspaceService workspaceService,
                        WorkspaceAuthorizationService workspaceAuthorizationService) {
        this.repository = repository;
        this.workspaceService = workspaceService;
        this.workspaceAuthorizationService = workspaceAuthorizationService;
    }

    public PageResponse<BoardDto> findAll(UUID userId, UUID workspaceId, Pageable pageable) {

        workspaceService.findById(workspaceId);

        workspaceAuthorizationService.validateWorkspaceMember(workspaceId, userId);

        var page = repository.findByWorkspace_WorkspaceId(workspaceId, pageable)
                .map(board -> new BoardDto().toDto(board));

        return new PageResponse<>(page);
    }

    public BoardDto createBoard(
            UUID userId,
            UUID workspaceId,
            CreateBoardRequest request) {

        Workspace workspace = workspaceService.findById(workspaceId);

        workspaceAuthorizationService.validateWorkspaceMember(workspaceId, userId);

        Board board = new Board();

        board.setName(request.name());
        board.setDescription(request.description());
        board.setWorkspace(workspace);

        repository.save(board);

        return new BoardDto().toDto(board);
    }

    public void deleteBoard(
            UUID userId,
            UUID boardId) {

        var board = repository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        workspaceAuthorizationService.validateWorkspaceRole(
                board.getWorkspace().getWorkspaceId(),
                userId,
                WorkspaceRole.ADMIN
        );

        repository.delete(board);
    }

    public void updateBoard(
            UUID userId,
            UUID boardId,
            UpdateBoardRequest request
    ) {

        var board = repository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        workspaceAuthorizationService.validateWorkspaceMember(
                board.getWorkspace().getWorkspaceId(),
                userId
        );

        board.setName(request.name());
        board.setDescription(request.description());
        board.setBackgroundColor(request.backgroundColor());

        repository.save(board);
    }

}
