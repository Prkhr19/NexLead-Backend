package com.NexLead.project.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConvertLeadToProjectRequestDto {

    private String name;

    @Positive(message = "Contract amount must be greater than zero")
    private BigDecimal contractAmount;

    private String address;

    private LocalDate startDate;

    private LocalDate expectedCompletionDate;

    private String notes;

    @Valid
    private List<CreateProjectAssignmentRequestDto> assignments;
}
