package com.NexLead.project.mapper;

import com.NexLead.auth.entity.User;
import com.NexLead.project.dto.ProjectAssignmentResponseDto;
import com.NexLead.project.entity.Project;
import com.NexLead.project.entity.ProjectAssignment;
import com.NexLead.project.entity.ProjectAssignmentRole;
import org.springframework.stereotype.Component;

@Component
public class ProjectAssignmentMapper {

    public ProjectAssignment toEntity(
            Project project,
            User user,
            User assignedBy,
            ProjectAssignmentRole role,
            boolean primaryContact
    ) {
        return ProjectAssignment.builder()
                .project(project)
                .user(user)
                .assignedBy(assignedBy)
                .role(role)
                .primaryContact(primaryContact)
                .active(true)
                .build();
    }

    public ProjectAssignmentResponseDto toResponseDto(ProjectAssignment assignment) {
        User user = assignment.getUser();
        User assignedBy = assignment.getAssignedBy();

        return ProjectAssignmentResponseDto.builder()
                .id(assignment.getId())
                .projectId(assignment.getProject().getId())
                .userId(user.getId())
                .userName(user.getName())
                .role(assignment.getRole())
                .primaryContact(assignment.isPrimaryContact())
                .active(assignment.isActive())
                .assignedById(assignedBy != null ? assignedBy.getId() : null)
                .assignedByName(assignedBy != null ? assignedBy.getName() : null)
                .assignedAt(assignment.getAssignedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }
}
