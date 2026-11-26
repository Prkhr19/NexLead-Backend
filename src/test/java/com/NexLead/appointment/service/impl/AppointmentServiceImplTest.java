package com.NexLead.appointment.service.impl;
import com.NexLead.appointment.dto.CreateAppointmentRequestDto;
import com.NexLead.appointment.entity.Appointment;
import com.NexLead.appointment.entity.AppointmentStatus;
import com.NexLead.appointment.mapper.AppointmentMapper;
import com.NexLead.appointment.repository.AppointmentRepository;
import com.NexLead.auth.repository.UserRepository;
import com.NexLead.common.exception.BusinessRuleException;
import com.NexLead.common.exception.ResourceNotFoundException;
import com.NexLead.lead.entity.Lead;
import com.NexLead.lead.entity.LeadActivityType;
import com.NexLead.lead.entity.LeadStatus;
import com.NexLead.lead.repository.LeadRepository;
import com.NexLead.lead.service.LeadActivityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private LeadRepository leadRepository;
    @Mock private UserRepository userRepository;
    @Mock private AppointmentMapper appointmentMapper;
    @Mock private LeadActivityService leadActivityService;

    @InjectMocks private AppointmentServiceImpl appointmentService;

    @Test
    void getAppointmentById_throwsWhenAppointmentDoesNotExist() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.getAppointmentById(99L));
    }

    @Test
    void acceptAppointment_acceptsPendingAppointmentAndRecordsActivity() {
        Appointment appointment = mock(Appointment.class);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointment.getStatus()).thenReturn(AppointmentStatus.PENDING);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        appointmentService.acceptAppointment(1L);

        verify(appointment).setStatus(AppointmentStatus.ACCEPTED);
        verify(leadActivityService).recordActivity(
                any(), eq(LeadActivityType.APPOINTMENT_ACCEPTED), anyString(), any());
    }

    @Test
    void acceptAppointment_throwsWhenStatusCannotBeAccepted() {
        Appointment appointment = mock(Appointment.class);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointment.getStatus()).thenReturn(AppointmentStatus.COMPLETED);

        assertThrows(BusinessRuleException.class, () -> appointmentService.acceptAppointment(1L));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void cancelAppointment_throwsWhenAppointmentAlreadyCompleted() {
        Appointment appointment = mock(Appointment.class);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointment.getStatus()).thenReturn(AppointmentStatus.COMPLETED);

        assertThrows(BusinessRuleException.class, () -> appointmentService.cancelAppointment(1L));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void completeAppointment_throwsUnlessAppointmentIsAccepted() {
        Appointment appointment = mock(Appointment.class);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointment.getStatus()).thenReturn(AppointmentStatus.PENDING);

        assertThrows(BusinessRuleException.class, () -> appointmentService.completeAppointment(1L));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void completeAppointment_completesAcceptedAppointmentAndRecordsActivity() {
        Appointment appointment = mock(Appointment.class);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointment.getStatus()).thenReturn(AppointmentStatus.ACCEPTED);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        appointmentService.completeAppointment(1L);

        verify(appointment).setStatus(AppointmentStatus.COMPLETED);
        verify(leadActivityService).recordActivity(
                any(), eq(LeadActivityType.APPOINTMENT_COMPLETED), anyString(), any());
    }

    @Test
    void createAppointment_throwsWhenLeadDoesNotExist() {
        CreateAppointmentRequestDto request = mock(CreateAppointmentRequestDto.class);
        when(request.getLeadId()).thenReturn(5L);
        when(leadRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.createAppointment(request));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_setsLeadStatusToMeetingScheduled() {
        CreateAppointmentRequestDto request = mock(CreateAppointmentRequestDto.class);
        Lead lead = mock(Lead.class);
        Appointment appointment = mock(Appointment.class);

        when(request.getLeadId()).thenReturn(5L);
        when(request.getAssignedDesignerId()).thenReturn(null);   // see the note below
        when(leadRepository.findById(5L)).thenReturn(Optional.of(lead));
        when(appointmentMapper.toEntity(request, lead, null)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        appointmentService.createAppointment(request);

        verify(lead).setStatus(LeadStatus.MEETING_SCHEDULED);
        verify(leadRepository).save(lead);
        verify(leadActivityService).recordActivity(
                eq(lead), eq(LeadActivityType.APPOINTMENT_BOOKED), anyString(), any());
    }
}