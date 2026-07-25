package com.NexLead.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCompanyReqDto {
    @NotBlank(message = "company name is required")
    private String companyName;

    private String website;

    @NotBlank(message = "email is required")
    @Email
    private String email;

    private String industry;

    @Pattern(

            regexp = "^[6-9]\\d{9}$",

            message = "Phone number must be a valid 10-digit Indian mobile number")
    @NotBlank(message = "Phone Number is required")
    private String phoneNumber;


    private String location;
}
