package com.NexLead.businessprofile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBusinessProfileRequestDto {

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String logo;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Phone number must be a valid 10-digit Indian mobile number"
    )
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "WhatsApp number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "WhatsApp number must be a valid 10-digit Indian mobile number"
    )
    private String whatsAppNumber;

    private String address;

    private String googleMapsUrl;

    private String workingHours;

    private String socialLinks;
}
