package com.NexLead.project.service;

import com.NexLead.project.dto.CreateProjectAssignmentRequestDto;
import com.NexLead.project.dto.ProjectAssignmentResponseDto;
import com.NexLead.project.dto.UpdateProjectAssignmentRequestDto;

import java.util.List;

public interface ProjectAssignmentService {

    ProjectAssignmentResponseDto createAssignment(Long projectId, CreateProjectAssignmentRequestDto request);

    List<ProjectAssignmentResponseDto> getAssignmentsByProjectId(Long projectId);

    ProjectAssignmentResponseDto updateAssignment(
            Long projectId,
            Long assignmentId,
            UpdateProjectAssignmentRequestDto request
    );

    void deactivateAssignment(Long projectId, Long assignmentId);
}
