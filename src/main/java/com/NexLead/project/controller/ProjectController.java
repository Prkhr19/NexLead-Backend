package com.NexLead.project.controller;

import com.NexLead.common.response.ApiResponse;
import com.NexLead.project.dto.ProjectPageResponseDto;
import com.NexLead.project.dto.ProjectResponseDto;
import com.NexLead.project.dto.UpdateProjectRequestDto;
import com.NexLead.project.entity.ProjectStatus;
import com.NexLead.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDto>> getProjectById(@PathVariable Long id) {
        ProjectResponseDto project = projectService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success("Project fetched successfully", project));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProjectPageResponseDto>> getProjects(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) Long leadId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ProjectPageResponseDto projects = projectService.getProjects(status, leadId, search, pageable);
        return ResponseEntity.ok(ApiResponse.success("Projects fetched successfully", projects));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<ProjectResponseDto>> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequestDto request
    ) {
        ProjectResponseDto project = projectService.updateProject(id, request);
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", project));
    }
}
