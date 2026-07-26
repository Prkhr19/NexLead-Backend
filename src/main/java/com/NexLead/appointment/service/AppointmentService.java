package com.NexLead.appointment.service;

import com.NexLead.appointment.dto.AppointmentPageResponseDto;
import com.NexLead.appointment.dto.AppointmentResponseDto;
import com.NexLead.appointment.dto.CreateAppointmentRequestDto;
import com.NexLead.appointment.dto.RescheduleAppointmentRequestDto;
import com.NexLead.appointment.dto.UpdateAppointmentRequestDto;
import com.NexLead.appointment.entity.AppointmentStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AppointmentService {

    AppointmentResponseDto createAppointment(CreateAppointmentRequestDto request);

    AppointmentResponseDto getAppointmentById(Long id);

    AppointmentPageResponseDto getAppointments(
            AppointmentStatus status,
            Long leadId,
            Long assignedDesignerId,
            LocalDate appointmentDate,
            Pageable pageable
    );

    AppointmentResponseDto updateAppointment(Long id, UpdateAppointmentRequestDto request);

    AppointmentResponseDto acceptAppointment(Long id);

    AppointmentResponseDto cancelAppointment(Long id);

    AppointmentResponseDto rescheduleAppointment(Long id, RescheduleAppointmentRequestDto request);

    AppointmentResponseDto completeAppointment(Long id);
}
