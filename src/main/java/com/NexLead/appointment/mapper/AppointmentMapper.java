package com.NexLead.appointment.mapper;

import com.NexLead.appointment.dto.AppointmentResponseDto;
import com.NexLead.appointment.dto.CreateAppointmentRequestDto;
import com.NexLead.appointment.dto.UpdateAppointmentRequestDto;
import com.NexLead.appointment.entity.Appointment;
import com.NexLead.appointment.entity.AppointmentStatus;
import com.NexLead.auth.entity.User;
import com.NexLead.lead.entity.Lead;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public Appointment toEntity(CreateAppointmentRequestDto request, Lead lead, User assignedDesigner) {
        return Appointment.builder()
                .lead(lead)
                .customerName(lead.getName())
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .address(request.getAddress())
                .assignedDesigner(assignedDesigner)
                .status(AppointmentStatus.PENDING)
                .build();
    }

    public void updateEntity(Appointment appointment, UpdateAppointmentRequestDto request, User assignedDesigner) {
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setAddress(request.getAddress());
        appointment.setAssignedDesigner(assignedDesigner);
        appointment.setStatus(request.getStatus());
    }

    public AppointmentResponseDto toResponseDto(Appointment appointment) {
        User designer = appointment.getAssignedDesigner();

        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .leadId(appointment.getLead().getId())
                .customerName(appointment.getCustomerName())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .address(appointment.getAddress())
                .assignedDesignerId(designer != null ? designer.getId() : null)
                .assignedDesignerName(designer != null ? designer.getName() : null)
                .status(appointment.getStatus())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
