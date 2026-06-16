package com.victor.trello_clone.service;

import com.victor.trello_clone.data.dto.BoardDto;
import com.victor.trello_clone.data.record.CreateBoardRequest;
import com.victor.trello_clone.model.board.Board;
import com.victor.trello_clone.model.workspace.Workspace;
import com.victor.trello_clone.repository.BoardRepository;
import com.victor.trello_clone.repository.WorkspaceMemberRepository;
import com.victor.trello_clone.repository.WorkspaceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BoardService {

    private final BoardRepository repository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public BoardService(BoardRepository repository,
                        WorkspaceRepository workspaceRepository,
                        WorkspaceMemberRepository workspaceMemberRepository) {
        this.repository = repository;
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    public BoardDto createBoard(
            UUID userId,
            UUID workspaceId,
            CreateBoardRequest request) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found"));

        //TODO: implementar método
        if (!workspaceMemberRepository.existsByWorkspaceAndUser(workspaceId, userId)) {
            throw new AccessDeniedException(
                    "User is not a member of this workspace");
        }

        Board board = new Board();

        board.setId(UUID.randomUUID());
        board.setName(request.name());
        board.setDescription(request.description());
        board.setWorkspace(workspace);

        repository.save(board);

        return new BoardDto().toDto(board);
    }

}
