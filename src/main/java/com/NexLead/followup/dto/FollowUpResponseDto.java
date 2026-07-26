package com.NexLead.followup.dto;

import com.NexLead.followup.entity.FollowUpType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpResponseDto {

    private Long id;
    private Long leadId;
    private String leadName;
    private Long assignedToId;
    private String assignedToName;
    private Long createdById;
    private String createdByName;
    private FollowUpType followUpType;
    private LocalDate dueDate;
    private String notes;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
