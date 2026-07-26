package com.NexLead.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentPageResponseDto {

    private List<AppointmentResponseDto> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
