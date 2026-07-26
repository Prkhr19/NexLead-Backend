package com.NexLead.appointment.repository;

import com.NexLead.appointment.entity.Appointment;
import com.NexLead.appointment.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.Collection;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    long countByAppointmentDateGreaterThanEqualAndStatusIn(LocalDate date, Collection<AppointmentStatus> statuses);
}
