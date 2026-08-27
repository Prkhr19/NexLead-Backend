package com.NexLead.project.repository;

import com.NexLead.project.entity.ProjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Long> {

    List<ProjectAssignment> findByProjectIdAndActiveTrueOrderByPrimaryContactDescAssignedAtAsc(Long projectId);

    Optional<ProjectAssignment> findByIdAndProjectId(Long id, Long projectId);

    Optional<ProjectAssignment> findByProjectIdAndUserId(Long projectId, Long userId);

    boolean existsByProjectIdAndUserIdAndActiveTrue(Long projectId, Long userId);

    @Modifying
    @Query("UPDATE ProjectAssignment pa SET pa.primaryContact = false WHERE pa.project.id = :projectId AND pa.active = true")
    void clearPrimaryContacts(@Param("projectId") Long projectId);

    void deleteByProject_Id(Long projectId);
}
