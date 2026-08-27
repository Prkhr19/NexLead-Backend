package com.NexLead.project.specification;

import com.NexLead.project.entity.Project;
import com.NexLead.project.entity.ProjectStatus;
import org.springframework.data.jpa.domain.Specification;

public final class ProjectSpecification {

    private ProjectSpecification() {
    }

    public static Specification<Project> hasStatus(ProjectStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Project> hasLeadId(Long leadId) {
        return (root, query, cb) ->
                leadId == null ? null : cb.equal(root.get("lead").get("id"), leadId);
    }

    public static Specification<Project> matchesSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("customerName")), pattern),
                    cb.like(cb.lower(root.get("customerPhone")), pattern),
                    cb.like(cb.lower(root.get("city")), pattern)
            );
        };
    }

    public static Specification<Project> build(ProjectStatus status, Long leadId, String search) {
        return Specification.where(hasStatus(status))
                .and(hasLeadId(leadId))
                .and(matchesSearch(search));
    }
}
