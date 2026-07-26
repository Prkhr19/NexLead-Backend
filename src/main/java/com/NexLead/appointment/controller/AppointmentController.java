package com.NexLead.appointment.controller;

import com.NexLead.appointment.dto.AppointmentPageResponseDto;
import com.NexLead.appointment.dto.AppointmentResponseDto;
import com.NexLead.appointment.dto.CreateAppointmentRequestDto;
import com.NexLead.appointment.dto.RescheduleAppointmentRequestDto;
import com.NexLead.appointment.dto.UpdateAppointmentRequestDto;
import com.NexLead.appointment.entity.AppointmentStatus;
import com.NexLead.appointment.service.AppointmentService;
import com.NexLead.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> createAppointment(
            @Valid @RequestBody CreateAppointmentRequestDto request
    ) {
        AppointmentResponseDto appointment = appointmentService.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment booked successfully", appointment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> getAppointmentById(@PathVariable Long id) {
        AppointmentResponseDto appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Appointment fetched successfully", appointment));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AppointmentPageResponseDto>> getAppointments(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) Long leadId,
            @RequestParam(required = false) Long assignedDesignerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "appointmentDate", "appointmentTime"));
        AppointmentPageResponseDto appointments = appointmentService.getAppointments(
                status, leadId, assignedDesignerId, appointmentDate, pageable
        );
        return ResponseEntity.ok(ApiResponse.success("Appointments fetched successfully", appointments));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentRequestDto request
    ) {
        AppointmentResponseDto appointment = appointmentService.updateAppointment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Appointment updated successfully", appointment));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> acceptAppointment(@PathVariable Long id) {
        AppointmentResponseDto appointment = appointmentService.acceptAppointment(id);
        return ResponseEntity.ok(ApiResponse.success("Appointment accepted successfully", appointment));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> cancelAppointment(@PathVariable Long id) {
        AppointmentResponseDto appointment = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(ApiResponse.success("Appointment cancelled successfully", appointment));
    }

    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> rescheduleAppointment(
            @PathVariable Long id,
            @Valid @RequestBody RescheduleAppointmentRequestDto request
    ) {
        AppointmentResponseDto appointment = appointmentService.rescheduleAppointment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Appointment rescheduled successfully", appointment));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> completeAppointment(@PathVariable Long id) {
        AppointmentResponseDto appointment = appointmentService.completeAppointment(id);
        return ResponseEntity.ok(ApiResponse.success("Appointment completed successfully", appointment));
    }
}
