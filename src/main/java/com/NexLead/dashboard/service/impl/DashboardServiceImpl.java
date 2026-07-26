package com.NexLead.dashboard.service.impl;

import com.NexLead.appointment.entity.AppointmentStatus;
import com.NexLead.appointment.repository.AppointmentRepository;
import com.NexLead.dashboard.dto.AssigneeLeadCountDto;
import com.NexLead.dashboard.dto.DashboardStatsResponseDto;
import com.NexLead.dashboard.dto.LeadStatusCountDto;
import com.NexLead.dashboard.service.DashboardService;
import com.NexLead.followup.repository.FollowUpRepository;
import com.NexLead.lead.entity.LeadStatus;
import com.NexLead.lead.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final LeadRepository leadRepository;
    private final AppointmentRepository appointmentRepository;
    private final FollowUpRepository followUpRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponseDto getStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = today.plusMonths(1).withDayOfMonth(1).atStartOfDay();

        long todaysLeads = leadRepository.countByCreatedAtGreaterThanEqual(startOfToday);
        long monthlyLeads = leadRepository.countByCreatedAtBetween(startOfMonth, endOfMonth);
        long totalLeads = leadRepository.count();

        long upcomingAppointments = appointmentRepository.countByAppointmentDateGreaterThanEqualAndStatusIn(
                today,
                List.of(AppointmentStatus.PENDING, AppointmentStatus.ACCEPTED, AppointmentStatus.RESCHEDULED)
        );

        long todaysFollowUps = followUpRepository.countByDueDateAndCompletedFalse(today);
        long overdueFollowUps = followUpRepository.countByDueDateBeforeAndCompletedFalse(today);

        List<LeadStatusCountDto> leadsByStatus = Arrays.stream(LeadStatus.values())
                .map(status -> LeadStatusCountDto.builder()
                        .status(status)
                        .count(leadRepository.countByStatus(status))
                        .build())
                .toList();

        List<AssigneeLeadCountDto> leadsPerAssignee = leadRepository.countLeadsGroupedByAssignee().stream()
                .map(row -> AssigneeLeadCountDto.builder()
                        .assigneeName((String) row[0])
                        .leadCount((Long) row[1])
                        .build())
                .toList();

        return DashboardStatsResponseDto.builder()
                .todaysLeads(todaysLeads)
                .monthlyLeads(monthlyLeads)
                .totalLeads(totalLeads)
                .upcomingAppointments(upcomingAppointments)
                .todaysFollowUps(todaysFollowUps)
                .overdueFollowUps(overdueFollowUps)
                .leadsByStatus(leadsByStatus)
                .leadsPerAssignee(leadsPerAssignee)
                .build();
    }
}
