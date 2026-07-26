package com.NexLead.dashboard.dto;

import com.NexLead.lead.entity.LeadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadStatusCountDto {

    private LeadStatus status;
    private long count;
}
