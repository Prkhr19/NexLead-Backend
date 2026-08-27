package com.NexLead.project.service.impl;

import com.NexLead.auth.entity.Role;
import com.NexLead.auth.entity.User;
import com.NexLead.auth.repository.UserRepository;
import com.NexLead.common.exception.BusinessRuleException;
import com.NexLead.common.exception.ResourceNotFoundException;
import com.NexLead.common.util.SecurityUtils;
import com.NexLead.project.dto.CreateProjectAssignmentRequestDto;
import com.NexLead.project.dto.ProjectAssignmentResponseDto;
import com.NexLead.project.dto.UpdateProjectAssignmentRequestDto;
import com.NexLead.project.entity.Project;
import com.NexLead.project.entity.ProjectAssignment;
import com.NexLead.project.mapper.ProjectAssignmentMapper;
import com.NexLead.project.repository.ProjectAssignmentRepository;
import com.NexLead.project.repository.ProjectRepository;
import com.NexLead.project.service.ProjectAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectAssignmentServiceImpl implements ProjectAssignmentService {

    private static final Set<Role> ASSIGNABLE_ROLES = Set.of(Role.STAFF, Role.HR);

    private final ProjectRepository projectRepository;
    private final ProjectAssignmentRepository projectAssignmentRepository;
    private final UserRepository userRepository;
    private final ProjectAssignmentMapper projectAssignmentMapper;

    @Override
    @Transactional
    public ProjectAssignmentResponseDto createAssignment(Long projectId, CreateProjectAssignmentRequestDto request) {
        Project project = findProjectOrThrow(projectId);
        User user = findAssignableUserOrThrow(request.getUserId());
        User assignedBy = findCurrentUser();

        boolean primaryContact = Boolean.TRUE.equals(request.getPrimaryContact());

        ProjectAssignment existing = projectAssignmentRepository
                .findByProjectIdAndUserId(projectId, user.getId())
                .orElse(null);

        if (existing != null) {
            if (existing.isActive()) {
                throw new BusinessRuleException("User is already assigned to this project");
            }
            existing.setRole(request.getRole());
            existing.setActive(true);
            existing.setAssignedBy(assignedBy);
            if (primaryContact) {
                clearOtherPrimaryContacts(projectId);
                existing.setPrimaryContact(true);
            }
            return projectAssignmentMapper.toResponseDto(projectAssignmentRepository.save(existing));
        }

        if (primaryContact) {
            clearOtherPrimaryContacts(projectId);
        }

        ProjectAssignment assignment = projectAssignmentMapper.toEntity(
                project,
                user,
                assignedBy,
                request.getRole(),
                primaryContact
        );
        ProjectAssignment saved = projectAssignmentRepository.save(assignment);
        return projectAssignmentMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectAssignmentResponseDto> getAssignmentsByProjectId(Long projectId) {
        findProjectOrThrow(projectId);
        return projectAssignmentRepository.findByProjectIdAndActiveTrueOrderByPrimaryContactDescAssignedAtAsc(projectId)
                .stream()
                .map(projectAssignmentMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ProjectAssignmentResponseDto updateAssignment(
            Long projectId,
            Long assignmentId,
            UpdateProjectAssignmentRequestDto request
    ) {
        ProjectAssignment assignment = findAssignmentOrThrow(projectId, assignmentId);

        if (request.getRole() != null) {
            assignment.setRole(request.getRole());
        }

        if (request.getPrimaryContact() != null) {
            if (request.getPrimaryContact()) {
                clearOtherPrimaryContacts(projectId);
                assignment.setPrimaryContact(true);
            } else {
                assignment.setPrimaryContact(false);
            }
        }

        if (request.getActive() != null) {
            if (!request.getActive() && assignment.isPrimaryContact()) {
                assignment.setPrimaryContact(false);
            }
            assignment.setActive(request.getActive());
        }

        ProjectAssignment saved = projectAssignmentRepository.save(assignment);
        return projectAssignmentMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void deactivateAssignment(Long projectId, Long assignmentId) {
        ProjectAssignment assignment = findAssignmentOrThrow(projectId, assignmentId);
        if (!assignment.isActive()) {
            throw new BusinessRuleException("Assignment is already inactive");
        }
        if (assignment.isPrimaryContact()) {
            assignment.setPrimaryContact(false);
        }
        assignment.setActive(false);
        projectAssignmentRepository.save(assignment);
    }

    private void clearOtherPrimaryContacts(Long projectId) {
        projectAssignmentRepository.clearPrimaryContacts(projectId);
    }

    private Project findProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
    }

    private ProjectAssignment findAssignmentOrThrow(Long projectId, Long assignmentId) {
        return projectAssignmentRepository.findByIdAndProjectId(assignmentId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project assignment not found with id: " + assignmentId
                ));
    }

    private User findAssignableUserOrThrow(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!user.isEnabled()) {
            throw new BusinessRuleException("Cannot assign a disabled user to a project");
        }

        if (!ASSIGNABLE_ROLES.contains(user.getRole())) {
            throw new BusinessRuleException("Only staff or HR users can be assigned to a project");
        }

        return user;
    }

    private User findCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
