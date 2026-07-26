package com.NexLead.followup.dto;

import com.NexLead.followup.entity.FollowUpType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFollowUpRequestDto {

    @NotNull(message = "Lead ID is required")
    private Long leadId;

    @NotNull(message = "Assignee ID is required")
    private Long assignedToId;

    @NotNull(message = "Follow-up type is required")
    private FollowUpType followUpType;

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;

    private String notes;
}
