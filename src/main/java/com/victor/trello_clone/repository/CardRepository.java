package com.victor.trello_clone.repository;

import com.victor.trello_clone.model.card.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findByList_Id(Long listId, Pageable pageable);

    Optional<Card> findByIdAndList_Id(Long cardId, Long listId);

    @Query("""
        select coalesce(max(c.position), -1)
        from Card c
        where c.list.id = :listId
    """)
    Long findMaxPositionByListId(Long listId);
}
