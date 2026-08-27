package com.NexLead.project.dto;

import com.NexLead.project.entity.ProjectStatus;
import com.NexLead.lead.entity.ServiceInterested;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDto {

    private Long id;
    private String name;
    private Long leadId;
    private String leadName;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String city;
    private String address;
    private ServiceInterested serviceInterested;
    private ProjectStatus status;
    private BigDecimal contractAmount;
    private LocalDate startDate;
    private LocalDate expectedCompletionDate;
    private LocalDate actualCompletionDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProjectAssignmentResponseDto> assignments;
}
