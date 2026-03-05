package com.victor.trello_clone.repository;

import com.victor.trello_clone.model.Workspace;
import jdk.jfr.Registered;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

@Registered
public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {

    @Query("""
           SELECT w
           FROM Workspace w
           JOIN w.owner u
           WHERE u.id = :userId
       """)
    Page<Workspace> findUsersWorkspaces(Pageable pageable, @Param("userId") UUID userId);
}
