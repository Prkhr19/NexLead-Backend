package com.NexLead.followup.controller;

import com.NexLead.common.response.ApiResponse;
import com.NexLead.followup.dto.CreateFollowUpRequestDto;
import com.NexLead.followup.dto.FollowUpPageResponseDto;
import com.NexLead.followup.dto.FollowUpResponseDto;
import com.NexLead.followup.dto.UpdateFollowUpRequestDto;
import com.NexLead.followup.service.FollowUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follow-ups")
public class FollowUpController {

    private final FollowUpService followUpService;

    @PostMapping
    public ResponseEntity<ApiResponse<FollowUpResponseDto>> createFollowUp(
            @Valid @RequestBody CreateFollowUpRequestDto request
    ) {
        FollowUpResponseDto followUp = followUpService.createFollowUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Follow-up created successfully", followUp));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FollowUpResponseDto>> getFollowUpById(@PathVariable Long id) {
        FollowUpResponseDto followUp = followUpService.getFollowUpById(id);
        return ResponseEntity.ok(ApiResponse.success("Follow-up fetched successfully", followUp));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<FollowUpPageResponseDto>> getFollowUps(
            @RequestParam(required = false) Long leadId,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(defaultValue = "false") boolean assignedToMe,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Boolean overdue,
            @RequestParam(required = false) Boolean dueToday,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dueDate"));
        FollowUpPageResponseDto followUps = followUpService.getFollowUps(
                leadId, assignedToId, assignedToMe, completed, overdue, dueToday, pageable
        );
        return ResponseEntity.ok(ApiResponse.success("Follow-ups fetched successfully", followUps));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FollowUpResponseDto>> updateFollowUp(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFollowUpRequestDto request
    ) {
        FollowUpResponseDto followUp = followUpService.updateFollowUp(id, request);
        return ResponseEntity.ok(ApiResponse.success("Follow-up updated successfully", followUp));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<FollowUpResponseDto>> completeFollowUp(@PathVariable Long id) {
        FollowUpResponseDto followUp = followUpService.completeFollowUp(id);
        return ResponseEntity.ok(ApiResponse.success("Follow-up marked as completed", followUp));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFollowUp(@PathVariable Long id) {
        followUpService.deleteFollowUp(id);
        return ResponseEntity.ok(ApiResponse.success("Follow-up deleted successfully", null));
    }
}
