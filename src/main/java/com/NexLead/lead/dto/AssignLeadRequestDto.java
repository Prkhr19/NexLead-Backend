package com.NexLead.lead.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignLeadRequestDto {

    /** Null unassigns the lead from any staff member. */
    private Long assignedToId;
}
