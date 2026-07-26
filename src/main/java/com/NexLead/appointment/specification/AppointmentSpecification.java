package com.NexLead.appointment.specification;

import com.NexLead.appointment.entity.Appointment;
import com.NexLead.appointment.entity.AppointmentStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class AppointmentSpecification {

    private AppointmentSpecification() {
    }

    public static Specification<Appointment> hasStatus(AppointmentStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Appointment> hasLeadId(Long leadId) {
        return (root, query, cb) ->
                leadId == null ? null : cb.equal(root.get("lead").get("id"), leadId);
    }

    public static Specification<Appointment> hasAssignedDesignerId(Long designerId) {
        return (root, query, cb) ->
                designerId == null ? null : cb.equal(root.get("assignedDesigner").get("id"), designerId);
    }

    public static Specification<Appointment> hasAppointmentDate(LocalDate date) {
        return (root, query, cb) ->
                date == null ? null : cb.equal(root.get("appointmentDate"), date);
    }

    public static Specification<Appointment> build(
            AppointmentStatus status,
            Long leadId,
            Long assignedDesignerId,
            LocalDate appointmentDate
    ) {
        return Specification.where(hasStatus(status))
                .and(hasLeadId(leadId))
                .and(hasAssignedDesignerId(assignedDesignerId))
                .and(hasAppointmentDate(appointmentDate));
    }
}
