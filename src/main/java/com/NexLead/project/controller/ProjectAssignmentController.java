package com.NexLead.project.controller;

import com.NexLead.common.response.ApiResponse;
import com.NexLead.project.dto.CreateProjectAssignmentRequestDto;
import com.NexLead.project.dto.ProjectAssignmentResponseDto;
import com.NexLead.project.dto.UpdateProjectAssignmentRequestDto;
import com.NexLead.project.service.ProjectAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects/{projectId}/assignments")
public class ProjectAssignmentController {

    private final ProjectAssignmentService projectAssignmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<ProjectAssignmentResponseDto>> createAssignment(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateProjectAssignmentRequestDto request
    ) {
        ProjectAssignmentResponseDto assignment = projectAssignmentService.createAssignment(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project assignment created successfully", assignment));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectAssignmentResponseDto>>> getAssignments(
            @PathVariable Long projectId
    ) {
        List<ProjectAssignmentResponseDto> assignments =
                projectAssignmentService.getAssignmentsByProjectId(projectId);
        return ResponseEntity.ok(ApiResponse.success("Project assignments fetched successfully", assignments));
    }

    @PutMapping("/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<ProjectAssignmentResponseDto>> updateAssignment(
            @PathVariable Long projectId,
            @PathVariable Long assignmentId,
            @Valid @RequestBody UpdateProjectAssignmentRequestDto request
    ) {
        ProjectAssignmentResponseDto assignment =
                projectAssignmentService.updateAssignment(projectId, assignmentId, request);
        return ResponseEntity.ok(ApiResponse.success("Project assignment updated successfully", assignment));
    }

    @DeleteMapping("/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<Void>> deactivateAssignment(
            @PathVariable Long projectId,
            @PathVariable Long assignmentId
    ) {
        projectAssignmentService.deactivateAssignment(projectId, assignmentId);
        return ResponseEntity.ok(ApiResponse.success("Project assignment removed successfully", null));
    }
}
