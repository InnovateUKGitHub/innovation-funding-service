package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.innovateuk.ifs.interview.resource.InterviewAssessorAllocateApplicationsResource;

/**
 * Holder of model attributes for the assessors shown in the 'Allocate applications to assessors' page
 */
public class InterviewAllocateApplicationsRowViewModel {
    private final long id;
    private final String name;
    private final String skillAreas;

    public InterviewAllocateApplicationsRowViewModel(InterviewAssessorAllocateApplicationsResource interviewAssessorAllocateApplicationsResource) {
        this.id = interviewAssessorAllocateApplicationsResource.getId();
        this.name = interviewAssessorAllocateApplicationsResource.getName();
        this.skillAreas = interviewAssessorAllocateApplicationsResource.getSkillAreas();
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

        InterviewAllocateApplicationsRowViewModel that = (InterviewAllocateApplicationsRowViewModel) o;

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
