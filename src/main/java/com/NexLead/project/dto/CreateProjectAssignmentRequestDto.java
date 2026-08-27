package com.NexLead.project.dto;

import com.NexLead.project.entity.ProjectAssignmentRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectAssignmentRequestDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Role is required")
    private ProjectAssignmentRole role;

    private Boolean primaryContact;
}
