package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;

/**
 * Holder of model attributes for the applications shown in the 'Manage applications' page
 */
public class ManageAssessorsRowViewModel {
    private final long id;
    private final String name;
    private final String skillAreas;
    private final long total;
    private final long assigned;
    private final long accepted;
    private final long submitted;

    public ManageAssessorsRowViewModel(AssessorCountSummaryResource assessorCountSummaryResource) {
        this.id = assessorCountSummaryResource.getId();
        this.name = assessorCountSummaryResource.getName();
        this.skillAreas = assessorCountSummaryResource.getSkillAreas();
        this.total = assessorCountSummaryResource.getTotalAssigned();
        this.assigned = assessorCountSummaryResource.getAssigned();
        this.accepted = assessorCountSummaryResource.getAccepted();
        this.submitted = assessorCountSummaryResource.getSubmitted();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSkillAreas() {
        return skillAreas;
    }

    public long getTotal() {
        return total;
    }

    public long getAssigned() {
        return assigned;
    }

    public long getAccepted() {
        return accepted;
    }

    public long getSubmitted() {
        return submitted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ManageAssessorsRowViewModel that = (ManageAssessorsRowViewModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(total, that.total)
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
                .append(id)
                .append(name)
                .append(skillAreas)
                .append(total)
                .append(assigned)
                .append(accepted)
                .append(submitted)
                .toHashCode();
    }
}