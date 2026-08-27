package com.NexLead.project.service;

import com.NexLead.project.dto.ConvertLeadToProjectRequestDto;
import com.NexLead.project.dto.ProjectPageResponseDto;
import com.NexLead.project.dto.ProjectResponseDto;
import com.NexLead.project.dto.UpdateProjectRequestDto;
import com.NexLead.project.entity.ProjectStatus;
import org.springframework.data.domain.Pageable;

public interface ProjectService {

    ProjectResponseDto convertFromWonLead(Long leadId, ConvertLeadToProjectRequestDto request);

    ProjectResponseDto getProjectById(Long id);

    ProjectPageResponseDto getProjects(ProjectStatus status, Long leadId, String search, Pageable pageable);

    ProjectResponseDto updateProject(Long id, UpdateProjectRequestDto request);
}
