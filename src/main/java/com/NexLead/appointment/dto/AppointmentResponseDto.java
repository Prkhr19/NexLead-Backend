package com.NexLead.appointment.dto;

import com.NexLead.appointment.entity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {

    private Long id;
    private Long leadId;
    private String customerName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String address;
    private Long assignedDesignerId;
    private String assignedDesignerName;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
