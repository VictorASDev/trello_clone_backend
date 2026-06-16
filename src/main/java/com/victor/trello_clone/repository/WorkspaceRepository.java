package com.victor.trello_clone.repository;

import com.victor.trello_clone.model.workspace.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {

    @Query("""
           SELECT wm.workspace
           FROM WorkspaceMember wm
           WHERE wm.user.id = :userId
       """)
    Page<Workspace> findUsersWorkspaces(Pageable pageable, @Param("userId") UUID userId);

    @Query("SELECT w FROM Workspace w where w.name = :workspaceName")
    Optional<Workspace> findByName(@Param("workspaceName") String workspaceName);

}
