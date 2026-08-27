package com.NexLead.project.dto;

import com.NexLead.project.entity.ProjectAssignmentRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectAssignmentRequestDto {

    private ProjectAssignmentRole role;

    private Boolean primaryContact;

    private Boolean active;
}
