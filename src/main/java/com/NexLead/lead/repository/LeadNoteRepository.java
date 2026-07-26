package com.NexLead.lead.repository;

import com.NexLead.lead.entity.LeadNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeadNoteRepository extends JpaRepository<LeadNote, Long> {

    Page<LeadNote> findByLeadIdOrderByCreatedAtDesc(Long leadId, Pageable pageable);

    Optional<LeadNote> findByIdAndLeadId(Long id, Long leadId);
}
