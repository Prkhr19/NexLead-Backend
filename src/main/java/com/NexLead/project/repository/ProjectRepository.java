package com.NexLead.project.repository;

import com.NexLead.project.entity.Project;
import com.NexLead.project.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    boolean existsByLead_Id(Long leadId);

    boolean existsByLead_IdAndStatusNot(Long leadId, ProjectStatus status);

    List<Project> findByLead_IdAndStatus(Long leadId, ProjectStatus status);

    long countByStatusNotIn(List<ProjectStatus> statuses);
}
