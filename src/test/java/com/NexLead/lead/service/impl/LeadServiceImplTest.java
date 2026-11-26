package com.NexLead.lead.service.impl;
import com.NexLead.auth.entity.User;
import com.NexLead.lead.dto.CreateLeadRequestDto;
import com.NexLead.lead.dto.LeadPageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.NexLead.appointment.repository.AppointmentRepository;
import com.NexLead.auth.repository.UserRepository;
import com.NexLead.common.exception.BusinessRuleException;
import com.NexLead.common.exception.ResourceNotFoundException;
import com.NexLead.followup.repository.FollowUpRepository;
import com.NexLead.lead.dto.AssignLeadRequestDto;
import com.NexLead.lead.dto.LeadResponseDto;
import com.NexLead.lead.dto.UpdateLeadRequestDto;
import com.NexLead.lead.entity.Lead;
import com.NexLead.lead.entity.LeadActivity;
import com.NexLead.lead.entity.LeadActivityType;
import com.NexLead.lead.entity.LeadStatus;
import com.NexLead.lead.mapper.LeadMapper;
import com.NexLead.lead.repository.LeadActivityRepository;
import com.NexLead.lead.repository.LeadNoteRepository;
import com.NexLead.lead.repository.LeadRepository;
import com.NexLead.lead.service.LeadActivityService;
import com.NexLead.lead.service.LeadService;
import com.NexLead.project.entity.Project;
import com.NexLead.project.entity.ProjectStatus;
import com.NexLead.project.repository.ProjectAssignmentRepository;
import com.NexLead.project.repository.ProjectRepository;
import com.NexLead.project.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadServiceImplTest {
    @InjectMocks private LeadServiceImpl leadService;
    @Mock private LeadRepository leadRepository;
    @Mock private LeadActivityRepository leadActivityRepository;
    @Mock private LeadNoteRepository leadNoteRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private FollowUpRepository followUpRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectAssignmentRepository projectAssignmentRepository;
    @Mock private UserRepository userRepository;
    @Mock private LeadMapper leadMapper;
    @Mock private LeadActivityService leadActivityService;


    @Test
    void getLeadById_throwsWhenLeadDoesNotExist(){
        when(leadRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> leadService.getLeadById(99L));
    }
    @Test
    void createLead_recordsOnlyCreatedActivity_whenNoAssignee() {
        CreateLeadRequestDto request = mock(CreateLeadRequestDto.class);
        Lead lead = mock(Lead.class);
        Lead saved = mock(Lead.class);

        when(request.getAssignedToId()).thenReturn(null);   // unstubbed Long would return 0, not null
        when(leadMapper.toEntity(request, null)).thenReturn(lead);
        when(leadRepository.save(lead)).thenReturn(saved);

        leadService.createLead(request);

        verify(leadActivityService).recordActivity(
                eq(saved), eq(LeadActivityType.LEAD_CREATED), anyString(), any());
        verify(leadActivityService, never()).recordActivity(
                any(), eq(LeadActivityType.LEAD_ASSIGNED), anyString(), any());
    }

    @Test
    void createLead_recordsCreatedAndAssignedActivities_whenAssigneeGiven() {
        CreateLeadRequestDto request = mock(CreateLeadRequestDto.class);
        User assignee = mock(User.class);
        Lead lead = mock(Lead.class);
        Lead saved = mock(Lead.class);

        when(request.getAssignedToId()).thenReturn(7L);
        when(userRepository.findById(7L)).thenReturn(Optional.of(assignee));
        when(leadMapper.toEntity(request, assignee)).thenReturn(lead);
        when(leadRepository.save(lead)).thenReturn(saved);

        leadService.createLead(request);

        verify(leadActivityService).recordActivity(
                eq(saved), eq(LeadActivityType.LEAD_CREATED), anyString(), any());
        verify(leadActivityService).recordActivity(
                eq(saved), eq(LeadActivityType.LEAD_ASSIGNED), anyString(), any());
    }

    @Test
    void assignLead_recordsAssigned_whenLeadWasUnassigned() {
        AssignLeadRequestDto request = mock(AssignLeadRequestDto.class);
        Lead lead = mock(Lead.class);
        Lead saved = mock(Lead.class);
        User assignee = mock(User.class);

        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(request.getAssignedToId()).thenReturn(7L);
        when(userRepository.findById(7L)).thenReturn(Optional.of(assignee));
        when(assignee.getId()).thenReturn(7L);
        when(leadRepository.save(lead)).thenReturn(saved);

        leadService.assignLead(1L, request);

        verify(lead).setAssignedTo(assignee);
        verify(leadActivityService).recordActivity(
                eq(saved), eq(LeadActivityType.LEAD_ASSIGNED), anyString(), any());
    }

    @Test
    void assignLead_recordsUnassigned_whenAssigneeRemoved() {
        AssignLeadRequestDto request = mock(AssignLeadRequestDto.class);
        Lead lead = mock(Lead.class);
        Lead saved = mock(Lead.class);
        User previous = mock(User.class);

        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(lead.getAssignedTo()).thenReturn(previous);
        when(request.getAssignedToId()).thenReturn(null);
        when(leadRepository.save(lead)).thenReturn(saved);

        leadService.assignLead(1L, request);

        verify(lead).setAssignedTo(null);
        verify(leadActivityService).recordActivity(
                eq(saved), eq(LeadActivityType.LEAD_UNASSIGNED), anyString(), any());
    }

    @Test
    void assignLead_doesNotRecordActivity_whenAssigneeUnchanged() {
        AssignLeadRequestDto request = mock(AssignLeadRequestDto.class);
        Lead lead = mock(Lead.class);
        Lead saved = mock(Lead.class);
        User previous = mock(User.class);
        User assignee = mock(User.class);

        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(lead.getAssignedTo()).thenReturn(previous);
        when(previous.getId()).thenReturn(7L);
        when(request.getAssignedToId()).thenReturn(7L);
        when(userRepository.findById(7L)).thenReturn(Optional.of(assignee));
        when(assignee.getId()).thenReturn(7L);
        when(leadRepository.save(lead)).thenReturn(saved);

        leadService.assignLead(1L, request);

        verify(leadActivityService, never()).recordActivity(any(), any(), any(), any());
    }

    @Test
    void deleteLead_removesCancelledProjectsAndTheirAssignments() {
        Project project = mock(Project.class);

        when(leadRepository.findById(1L)).thenReturn(Optional.of(mock(Lead.class)));
        when(projectRepository.existsByLead_IdAndStatusNot(1L, ProjectStatus.CANCELLED)).thenReturn(false);
        when(projectRepository.findByLead_IdAndStatus(1L, ProjectStatus.CANCELLED)).thenReturn(List.of(project));
        when(project.getId()).thenReturn(10L);

        leadService.deleteLead(1L);

        verify(projectAssignmentRepository).deleteByProject_Id(10L);
        verify(projectRepository).delete(project);
        verify(leadRepository).deleteById(1L);
    }

    @Test
    void getLeads_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Lead lead = mock(Lead.class);
        LeadResponseDto dto = mock(LeadResponseDto.class);
        Page<Lead> page = new PageImpl<>(List.of(lead), pageable, 1);

        when(leadRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(leadMapper.toResponseDto(lead)).thenReturn(dto);

        LeadPageResponseDto result = leadService.getLeads(null, null, null, null, null, false, pageable);

        assertEquals(1, result.getContent().size());
        assertSame(dto, result.getContent().get(0));
    }

    @Test
    void deleteLead_throwsWhenLeadHasActiveProject() {
        // Arrange

        when(leadRepository.findById(1L)).thenReturn(Optional.of(mock(Lead.class)));
        when(projectRepository.existsByLead_IdAndStatusNot(1L, ProjectStatus.CANCELLED))
                .thenReturn(true);


        // Act + Assert
        assertThrows(BusinessRuleException.class, () -> leadService.deleteLead(1L));

        // Nothing must be deleted when the rule blocks it
        verify(leadRepository, never()).deleteById(anyLong());
        verify(leadActivityRepository, never()).deleteByLead_Id(anyLong());
    }

    @Test
    void deleteLead_deletesLeadAndRelatedData_whenNoActiveProject(){
        when(leadRepository.findById(1L)).thenReturn(Optional.of(mock(Lead.class)));
        when(projectRepository.existsByLead_IdAndStatusNot(1L, ProjectStatus.CANCELLED)).thenReturn(false);
        when(projectRepository.findByLead_IdAndStatus(1L, ProjectStatus.CANCELLED)).thenReturn(List.of());

        leadService.deleteLead(1L);

        verify(leadNoteRepository).deleteByLead_Id(1L);
        verify(leadRepository).deleteById(1L);
        verify(appointmentRepository).deleteByLead_Id(1L);
        verify(followUpRepository).deleteByLead_Id(1L);
        verify(leadActivityRepository).deleteByLead_Id(1L);
    }

    @Test
    void getLeadbyId_returnMapperDto_whenLeadExists(){
        Lead lead = mock(Lead.class);
        LeadResponseDto dto = mock(LeadResponseDto.class);

        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(leadMapper.toResponseDto(lead)).thenReturn(dto);

        LeadResponseDto result = leadService.getLeadById(1L);

        assertSame(dto, result);

    }

    @Test
    void updateLead_throwsWhenLeadDoesNotExist(){
        UpdateLeadRequestDto request = mock(UpdateLeadRequestDto.class);

        when(leadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> leadService.updateLead(1L, request));
        verify(leadRepository, never()).save(any());
    }

    @Test
    void updateLead_recordsActivity_whenStatusChanges(){
        UpdateLeadRequestDto requestDto = mock(UpdateLeadRequestDto.class);
        Lead lead = mock(Lead.class);
        Lead save = mock(Lead.class);
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(lead.getStatus()).thenReturn(LeadStatus.CONTACTED);
        when(leadRepository.save(lead)).thenReturn(save);
        when(save.getStatus()).thenReturn(LeadStatus.WON);

        leadService.updateLead(1L, requestDto);

        verify(leadActivityService).recordActivity(
                eq(save), eq(LeadActivityType.STATUS_CHANGED), anyString(), any()
        );

    }

    @Test
    void updateLead_doesNotRecordActivity_whenStatusUnchanged(){
        UpdateLeadRequestDto requestDto = mock(UpdateLeadRequestDto.class);
        Lead lead = mock(Lead.class);
        Lead save = mock(Lead.class);
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(lead.getStatus()).thenReturn(LeadStatus.CONTACTED);
        when(leadRepository.save(lead)).thenReturn(save);
        when(save.getStatus()).thenReturn(LeadStatus.CONTACTED);
        leadService.updateLead(1L, requestDto);

        verify(leadActivityService, never()).recordActivity(any(), any(), any(), any());
    }
    @Test
    void deleteLead_throwsWhenLeadDoesNotExists(){
        when(leadRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->leadService.deleteLead(1L));
        verify(leadRepository, never()).deleteById(anyLong());
    }
    @Test
    void assignLead_throwsWhenAssigneeDoesNotExist(){
        AssignLeadRequestDto request = mock(AssignLeadRequestDto.class);
        when(leadRepository.findById(1L)).thenReturn(Optional.of(mock(Lead.class)));
        when(request.getAssignedToId()).thenReturn(99L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()-> leadService.assignLead(1L, request));

        verify(leadRepository, never()).save(any());


    }



}
