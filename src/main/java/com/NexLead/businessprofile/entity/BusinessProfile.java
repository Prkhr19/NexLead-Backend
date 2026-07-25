package com.NexLead.businessprofile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "business_profile")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProfile {

    public static final Long SINGLETON_ID = 1L;

    @Id
    private Long id = SINGLETON_ID;

    @Column(nullable = false)
    private String companyName;

    private String logo;

    private String phone;

    private String email;

    private String whatsAppNumber;

    private String address;

    private String googleMapsUrl;

    @Column(columnDefinition = "TEXT")
    private String workingHours;

    @Column(columnDefinition = "TEXT")
    private String socialLinks;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
