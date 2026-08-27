package com.NexLead.project.dto;

import com.NexLead.lead.entity.ServiceInterested;
import com.NexLead.project.entity.ProjectStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectRequestDto {

    @NotBlank(message = "Project name is required")
    private String name;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private String city;

    private String address;

    private ServiceInterested serviceInterested;

    @NotNull(message = "Status is required")
    private ProjectStatus status;

    @Positive(message = "Contract amount must be greater than zero")
    private BigDecimal contractAmount;

    @NotNull(message = "Progress is required")
    @Min(value = 0, message = "Progress must be between 0 and 100")
    @Max(value = 100, message = "Progress must be between 0 and 100")
    private Integer progress;

    private LocalDate startDate;

    private LocalDate expectedCompletionDate;

    private LocalDate actualCompletionDate;

    private String notes;
}
