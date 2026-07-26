package com.NexLead.lead.controller;

import com.NexLead.common.response.ApiResponse;
import com.NexLead.lead.dto.LeadActivityPageResponseDto;
import com.NexLead.lead.service.LeadActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/leads/{leadId}/timeline")
public class LeadTimelineController {

    private final LeadActivityService leadActivityService;

    @GetMapping
    public ResponseEntity<ApiResponse<LeadActivityPageResponseDto>> getTimeline(
            @PathVariable Long leadId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        LeadActivityPageResponseDto timeline = leadActivityService.getTimeline(leadId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Lead timeline fetched successfully", timeline));
    }
}
