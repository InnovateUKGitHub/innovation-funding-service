package org.innovateuk.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents an assessors's statistics for a competition
 */
public class AssessorCountSummaryResource extends AssessmentCountSummaryResource {
    private String name;
    private String skillAreas;
    private long assigned;
    private long accepted;
    private long submitted;

    // TODO we need to add the total here instead of just taking the sum of these double counts

    public AssessorCountSummaryResource() {
    }

    public AssessorCountSummaryResource(long id, String name, String skillAreas, long assigned, long accepted, long submitted) {
        super(id);
        this.name = name;
        this.skillAreas = skillAreas;
        this.assigned = assigned;
        this.accepted = accepted;
        this.submitted = submitted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkillAreas() {
        return skillAreas;
    }

    public void setSkillAreas(String skillAreas) {
        this.skillAreas = skillAreas;
    }

    public long getAssigned() {
        return assigned;
    }

    public void setAssigned(long assigned) {
        this.assigned = assigned;
    }

    public long getAccepted() {
        return accepted;
    }

    public void setAccepted(long accepted) {
        this.accepted = accepted;
    }

    public long getSubmitted() {
        return submitted;
    }

    public void setSubmitted(long submitted) {
        this.submitted = submitted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorCountSummaryResource that = (AssessorCountSummaryResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(assigned, that.assigned)
                .append(accepted, that.accepted)
                .append(submitted, that.submitted)
                .append(name, that.name)
                .append(skillAreas, that.skillAreas)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(name)
                .append(skillAreas)
                .append(assigned)
                .append(accepted)
                .append(submitted)
                .toHashCode();
    }
}