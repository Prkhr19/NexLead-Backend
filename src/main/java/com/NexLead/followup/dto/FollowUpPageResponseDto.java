package com.NexLead.followup.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpPageResponseDto {

    private List<FollowUpResponseDto> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
