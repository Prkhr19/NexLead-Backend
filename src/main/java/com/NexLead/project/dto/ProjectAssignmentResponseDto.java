package com.NexLead.project.dto;

import com.NexLead.project.entity.ProjectAssignmentRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAssignmentResponseDto {

    private Long id;
    private Long projectId;
    private Long userId;
    private String userName;
    private ProjectAssignmentRole role;
    private boolean primaryContact;
    private boolean active;
    private Long assignedById;
    private String assignedByName;
    private LocalDateTime assignedAt;
    private LocalDateTime updatedAt;
}
