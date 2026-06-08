package com.victor.trello_clone.repository;

import com.victor.trello_clone.model.user.User;
import com.victor.trello_clone.model.workspace.WorkspaceMember;
import com.victor.trello_clone.model.workspace.WorkspaceMemberId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, WorkspaceMemberId> {

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM WorkspaceMember wm
        WHERE wm.workspace.workspaceId = :workspaceId
          AND wm.user.id = :userId
    """)
    int deleteByWorkspaceAndUser(
            @Param("workspaceId") UUID workspaceId,
            @Param("userId") UUID userId
    );

    @Query("""
        SELECT wm.user
        FROM WorkspaceMember wm
        WHERE wm.workspace.workspaceId = :workspaceId
    """)
    List<User> findUsersByWorkspaceId(@Param("workspaceId") UUID workspaceId);
}


