package com.NexLead.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponseDto {

    private long todaysLeads;
    private long monthlyLeads;
    private long totalLeads;
    private long upcomingAppointments;
    private long todaysFollowUps;
    private long overdueFollowUps;
    private List<LeadStatusCountDto> leadsByStatus;
    private List<AssigneeLeadCountDto> leadsPerAssignee;
}
