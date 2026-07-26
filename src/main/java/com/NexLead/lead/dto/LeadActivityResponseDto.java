package com.NexLead.lead.dto;

import com.NexLead.lead.entity.LeadActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadActivityResponseDto {

    private Long id;
    private Long leadId;
    private LeadActivityType activityType;
    private String description;
    private Long performedById;
    private String performedByName;
    private LocalDateTime createdAt;
}
