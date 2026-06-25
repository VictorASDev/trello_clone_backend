package com.victor.trello_clone.repository;

import com.victor.trello_clone.model.board.Board;
import com.victor.trello_clone.model.boardList.BoardList;
import com.victor.trello_clone.model.card.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {

    Page<Board> findByWorkspace_WorkspaceId(UUID workspaceId, Pageable pageable);
}
