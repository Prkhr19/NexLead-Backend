package com.NexLead.followup.specification;

import com.NexLead.followup.entity.FollowUp;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class FollowUpSpecification {

    private FollowUpSpecification() {
    }

    public static Specification<FollowUp> hasLeadId(Long leadId) {
        return (root, query, cb) ->
                leadId == null ? null : cb.equal(root.get("lead").get("id"), leadId);
    }

    public static Specification<FollowUp> hasAssignedToId(Long assignedToId) {
        return (root, query, cb) ->
                assignedToId == null ? null : cb.equal(root.get("assignedTo").get("id"), assignedToId);
    }

    public static Specification<FollowUp> isCompleted(Boolean completed) {
        return (root, query, cb) ->
                completed == null ? null : cb.equal(root.get("completed"), completed);
    }

    public static Specification<FollowUp> isDueToday(LocalDate today) {
        return (root, query, cb) ->
                today == null ? null : cb.equal(root.get("dueDate"), today);
    }

    public static Specification<FollowUp> isOverdue(LocalDate today) {
        return (root, query, cb) -> {
            if (today == null) {
                return null;
            }
            return cb.and(
                    cb.lessThan(root.get("dueDate"), today),
                    cb.isFalse(root.get("completed"))
            );
        };
    }

    public static Specification<FollowUp> build(
            Long leadId,
            Long assignedToId,
            Boolean completed,
            Boolean overdue,
            Boolean dueToday,
            LocalDate today
    ) {
        Specification<FollowUp> spec = Specification.where(hasLeadId(leadId))
                .and(hasAssignedToId(assignedToId))
                .and(isCompleted(completed));

        if (Boolean.TRUE.equals(overdue)) {
            spec = spec.and(isOverdue(today));
        }
        if (Boolean.TRUE.equals(dueToday)) {
            spec = spec.and(isDueToday(today));
        }

        return spec;
    }
}
