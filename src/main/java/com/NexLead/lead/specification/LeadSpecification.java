package com.NexLead.lead.specification;

import com.NexLead.lead.entity.Lead;
import com.NexLead.lead.entity.LeadStatus;
import com.NexLead.lead.entity.ServiceInterested;
import org.springframework.data.jpa.domain.Specification;

public final class LeadSpecification {

    private LeadSpecification() {
    }

    public static Specification<Lead> hasStatus(LeadStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Lead> hasCity(String city) {
        return (root, query, cb) ->
                city == null || city.isBlank() ? null : cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }

    public static Specification<Lead> hasServiceInterested(ServiceInterested serviceInterested) {
        return (root, query, cb) ->
                serviceInterested == null ? null : cb.equal(root.get("serviceInterested"), serviceInterested);
    }

    public static Specification<Lead> matchesSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("phone")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("city")), pattern)
            );
        };
    }

    public static Specification<Lead> build(
            LeadStatus status,
            String city,
            ServiceInterested serviceInterested,
            String search
    ) {
        return Specification.where(hasStatus(status))
                .and(hasCity(city))
                .and(hasServiceInterested(serviceInterested))
                .and(matchesSearch(search));
    }
}
