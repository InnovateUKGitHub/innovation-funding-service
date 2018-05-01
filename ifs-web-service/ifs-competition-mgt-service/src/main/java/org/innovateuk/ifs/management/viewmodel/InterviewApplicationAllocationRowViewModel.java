package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.interview.resource.AssessorInterviewAllocationResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;

/**
 * Holder of model attributes for the applications shown in the 'Manage applications' page
 */
public class InterviewApplicationAllocationRowViewModel {
    private final long id;
    private final String name;
    private final String skillAreas;

    public InterviewApplicationAllocationRowViewModel(AssessorInterviewAllocationResource assessorInterviewAllocationResource) {
        this.id = assessorInterviewAllocationResource.getId();
        this.name = assessorInterviewAllocationResource.getName();
        this.skillAreas = assessorInterviewAllocationResource.getSkillAreas();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewApplicationAllocationRowViewModel that = (InterviewApplicationAllocationRowViewModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
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
                .toHashCode();
    }
}
