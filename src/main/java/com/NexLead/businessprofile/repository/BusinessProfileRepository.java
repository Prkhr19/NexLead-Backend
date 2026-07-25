package com.NexLead.businessprofile.repository;

import com.NexLead.businessprofile.entity.BusinessProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, Long> {
}
