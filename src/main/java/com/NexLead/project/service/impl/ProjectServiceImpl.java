package com.NexLead.project.service.impl;

import com.NexLead.common.exception.BusinessRuleException;
import com.NexLead.common.exception.ResourceNotFoundException;
import com.NexLead.common.util.SecurityUtils;
import com.NexLead.auth.entity.User;
import com.NexLead.auth.repository.UserRepository;
import com.NexLead.lead.entity.Lead;
import com.NexLead.lead.entity.LeadActivityType;
import com.NexLead.lead.entity.LeadStatus;
import com.NexLead.lead.repository.LeadRepository;
import com.NexLead.lead.service.LeadActivityService;
import com.NexLead.project.dto.ConvertLeadToProjectRequestDto;
import com.NexLead.project.dto.CreateProjectAssignmentRequestDto;
import com.NexLead.project.dto.ProjectAssignmentResponseDto;
import com.NexLead.project.dto.ProjectPageResponseDto;
import com.NexLead.project.dto.ProjectResponseDto;
import com.NexLead.project.dto.UpdateProjectRequestDto;
import com.NexLead.project.entity.Project;
import com.NexLead.project.entity.ProjectStatus;
import com.NexLead.project.mapper.ProjectMapper;
import com.NexLead.project.repository.ProjectRepository;
import com.NexLead.project.service.ProjectAssignmentService;
import com.NexLead.project.service.ProjectService;
import com.NexLead.project.specification.ProjectSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final LeadRepository leadRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectAssignmentService projectAssignmentService;
    private final LeadActivityService leadActivityService;

    @Override
    @Transactional
    public ProjectResponseDto convertFromWonLead(Long leadId, ConvertLeadToProjectRequestDto request) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));

        if (lead.getStatus() != LeadStatus.WON) {
            throw new BusinessRuleException("Only WON leads can be converted to a project");
        }

        if (projectRepository.existsByLead_IdAndStatusNot(leadId, ProjectStatus.CANCELLED)) {
            throw new BusinessRuleException("Lead already has an active project");
        }

        Project project = projectMapper.toEntityFromLead(lead, request);
        Project saved = projectRepository.save(project);

        List<ProjectAssignmentResponseDto> assignments = createAssignments(saved.getId(), request.getAssignments());

        User currentUser = findCurrentUser();
        leadActivityService.recordActivity(
                lead,
                LeadActivityType.PROJECT_CREATED,
                "Project created: " + saved.getName(),
                currentUser
        );

        ProjectResponseDto response = projectMapper.toResponseDto(saved);
        response.setAssignments(assignments);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectById(Long id) {
        Project project = findProjectOrThrow(id);
        ProjectResponseDto response = projectMapper.toResponseDto(project);
        response.setAssignments(projectAssignmentService.getAssignmentsByProjectId(id));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectPageResponseDto getProjects(
            ProjectStatus status,
            Long leadId,
            String search,
            Pageable pageable
    ) {
        Specification<Project> specification = ProjectSpecification.build(status, leadId, search);
        Page<Project> page = projectRepository.findAll(specification, pageable);

        List<ProjectResponseDto> content = page.getContent().stream()
                .map(projectMapper::toResponseDto)
                .toList();

        return ProjectPageResponseDto.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public ProjectResponseDto updateProject(Long id, UpdateProjectRequestDto request) {
        Project project = findProjectOrThrow(id);
        projectMapper.updateEntity(project, request);

        if (project.getStatus() == ProjectStatus.COMPLETED && project.getActualCompletionDate() == null) {
            project.setActualCompletionDate(LocalDate.now());
        }

        Project saved = projectRepository.save(project);
        ProjectResponseDto response = projectMapper.toResponseDto(saved);
        response.setAssignments(projectAssignmentService.getAssignmentsByProjectId(id));
        return response;
    }

    private Project findProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    private List<ProjectAssignmentResponseDto> createAssignments(
            Long projectId,
            List<CreateProjectAssignmentRequestDto> assignments
    ) {
        if (assignments == null || assignments.isEmpty()) {
            return List.of();
        }

        List<ProjectAssignmentResponseDto> created = new ArrayList<>();
        for (CreateProjectAssignmentRequestDto assignmentRequest : assignments) {
            created.add(projectAssignmentService.createAssignment(projectId, assignmentRequest));
        }
        return created;
    }

    private User findCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
