package com.NexLead.lead.dto;

import com.NexLead.lead.entity.LeadStatus;
import com.NexLead.lead.entity.ServiceInterested;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadResponseDto {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String city;
    private String propertyType;
    private ServiceInterested serviceInterested;
    private BigDecimal budget;
    private String projectStage;
    private String notes;
    private LeadStatus status;
    private Long assignedToId;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
