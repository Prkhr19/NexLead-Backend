package com.NexLead.common.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor
@Data
public class ErrorResponse {
    private boolean success ;
    private String message;
    private Integer status;
    private LocalDateTime timestamp = LocalDateTime.now();
    private Map<String, String>errors;
}
