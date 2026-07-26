package com.NexLead.lead.repository;

import com.NexLead.lead.entity.Lead;
import com.NexLead.lead.entity.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long>, JpaSpecificationExecutor<Lead> {

    long countByCreatedAtGreaterThanEqual(LocalDateTime start);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByStatus(LeadStatus status);

    @Query("SELECT l.assignedTo.name, COUNT(l) FROM Lead l WHERE l.assignedTo IS NOT NULL GROUP BY l.assignedTo.id, l.assignedTo.name")
    List<Object[]> countLeadsGroupedByAssignee();
}
