package com.NexLead.lead.repository;

import com.NexLead.lead.entity.LeadActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadActivityRepository extends JpaRepository<LeadActivity, Long> {

    Page<LeadActivity> findByLeadIdOrderByCreatedAtDesc(Long leadId, Pageable pageable);
}
