package com.NexLead.followup.repository;

import com.NexLead.followup.entity.FollowUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface FollowUpRepository extends JpaRepository<FollowUp, Long>, JpaSpecificationExecutor<FollowUp> {

    long countByDueDateBeforeAndCompletedFalse(LocalDate date);

    long countByDueDateAndCompletedFalse(LocalDate date);

    void deleteByLead_Id(Long leadId);
}
