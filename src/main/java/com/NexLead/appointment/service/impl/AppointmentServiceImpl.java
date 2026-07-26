package com.NexLead.appointment.service.impl;

import com.NexLead.appointment.dto.AppointmentPageResponseDto;
import com.NexLead.appointment.dto.AppointmentResponseDto;
import com.NexLead.appointment.dto.CreateAppointmentRequestDto;
import com.NexLead.appointment.dto.RescheduleAppointmentRequestDto;
import com.NexLead.appointment.dto.UpdateAppointmentRequestDto;
import com.NexLead.appointment.entity.Appointment;
import com.NexLead.appointment.entity.AppointmentStatus;
import com.NexLead.appointment.mapper.AppointmentMapper;
import com.NexLead.appointment.repository.AppointmentRepository;
import com.NexLead.appointment.service.AppointmentService;
import com.NexLead.appointment.specification.AppointmentSpecification;
import com.NexLead.auth.entity.User;
import com.NexLead.auth.repository.UserRepository;
import com.NexLead.common.exception.BusinessRuleException;
import com.NexLead.common.exception.ResourceNotFoundException;
import com.NexLead.lead.entity.Lead;
import com.NexLead.lead.entity.LeadStatus;
import com.NexLead.lead.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private static final Set<AppointmentStatus> ACCEPTABLE_STATUSES = Set.of(
            AppointmentStatus.PENDING,
            AppointmentStatus.RESCHEDULED
    );

    private static final Set<AppointmentStatus> CANCELLABLE_STATUSES = Set.of(
            AppointmentStatus.PENDING,
            AppointmentStatus.ACCEPTED,
            AppointmentStatus.RESCHEDULED
    );

    private static final Set<AppointmentStatus> RESCHEDULABLE_STATUSES = Set.of(
            AppointmentStatus.PENDING,
            AppointmentStatus.ACCEPTED,
            AppointmentStatus.RESCHEDULED
    );

    private final AppointmentRepository appointmentRepository;
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    @Transactional
    public AppointmentResponseDto createAppointment(CreateAppointmentRequestDto request) {
        Lead lead = findLeadOrThrow(request.getLeadId());
        User designer = findDesignerIfPresent(request.getAssignedDesignerId());

        Appointment appointment = appointmentMapper.toEntity(request, lead, designer);
        Appointment saved = appointmentRepository.save(appointment);

        lead.setStatus(LeadStatus.MEETING_SCHEDULED);
        leadRepository.save(lead);

        return appointmentMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDto getAppointmentById(Long id) {
        return appointmentMapper.toResponseDto(findAppointmentOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentPageResponseDto getAppointments(
            AppointmentStatus status,
            Long leadId,
            Long assignedDesignerId,
            LocalDate appointmentDate,
            Pageable pageable
    ) {
        Specification<Appointment> specification = AppointmentSpecification.build(
                status, leadId, assignedDesignerId, appointmentDate
        );
        Page<Appointment> page = appointmentRepository.findAll(specification, pageable);

        List<AppointmentResponseDto> content = page.getContent().stream()
                .map(appointmentMapper::toResponseDto)
                .toList();

        return AppointmentPageResponseDto.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public AppointmentResponseDto updateAppointment(Long id, UpdateAppointmentRequestDto request) {
        Appointment appointment = findAppointmentOrThrow(id);
        User designer = findDesignerIfPresent(request.getAssignedDesignerId());
        appointmentMapper.updateEntity(appointment, request, designer);
        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public AppointmentResponseDto acceptAppointment(Long id) {
        Appointment appointment = findAppointmentOrThrow(id);
        validateStatus(appointment, ACCEPTABLE_STATUSES, "accept");
        appointment.setStatus(AppointmentStatus.ACCEPTED);
        return appointmentMapper.toResponseDto(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponseDto cancelAppointment(Long id) {
        Appointment appointment = findAppointmentOrThrow(id);
        validateStatus(appointment, CANCELLABLE_STATUSES, "cancel");
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentMapper.toResponseDto(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponseDto rescheduleAppointment(Long id, RescheduleAppointmentRequestDto request) {
        Appointment appointment = findAppointmentOrThrow(id);
        validateStatus(appointment, RESCHEDULABLE_STATUSES, "reschedule");
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        return appointmentMapper.toResponseDto(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponseDto completeAppointment(Long id) {
        Appointment appointment = findAppointmentOrThrow(id);
        if (appointment.getStatus() != AppointmentStatus.ACCEPTED) {
            throw new BusinessRuleException("Only accepted appointments can be marked as completed");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentMapper.toResponseDto(appointmentRepository.save(appointment));
    }

    private Appointment findAppointmentOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }

    private Lead findLeadOrThrow(Long leadId) {
        return leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));
    }

    private User findDesignerIfPresent(Long designerId) {
        if (designerId == null) {
            return null;
        }
        return userRepository.findById(designerId)
                .orElseThrow(() -> new ResourceNotFoundException("Designer not found with id: " + designerId));
    }

    private void validateStatus(Appointment appointment, Set<AppointmentStatus> allowed, String action) {
        if (!allowed.contains(appointment.getStatus())) {
            throw new BusinessRuleException(
                    "Cannot " + action + " appointment with status: " + appointment.getStatus()
            );
        }
    }
}
