package com.NexLead.followup.dto;

import com.NexLead.followup.entity.FollowUpType;
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
public class UpdateFollowUpRequestDto {

    @NotNull(message = "Assignee ID is required")
    private Long assignedToId;

    @NotNull(message = "Follow-up type is required")
    private FollowUpType followUpType;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private String notes;
}
