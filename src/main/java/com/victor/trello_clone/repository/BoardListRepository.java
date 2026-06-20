package com.victor.trello_clone.repository;

import com.victor.trello_clone.model.boardList.BoardList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardListRepository extends JpaRepository<BoardList, Long> {

    Optional<BoardList> findByIdAndBoard_Id(
            Long listId,
            UUID boardId
    );

    @Query("""
        select coalesce(max(l.position), -1)
        from BoardList l
        where l.board.id = :boardId
    """)
    Long findMaxPositionByBoardId(UUID boardId);

    Page<BoardList> findByBoard_Id(UUID boardId, Pageable pageable);
}
