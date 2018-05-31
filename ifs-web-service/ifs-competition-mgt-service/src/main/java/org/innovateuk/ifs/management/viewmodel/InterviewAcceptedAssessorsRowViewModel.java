package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsResource;

/**
 * Holder of model attributes for the assessors shown in the 'Allocate applications to assessors' page
 */
public class InterviewAcceptedAssessorsRowViewModel {
    private final long id;
    private final String name;
    private final String skillAreas;
    private final long numberOfAllocatedApplications;

    public InterviewAcceptedAssessorsRowViewModel(InterviewAcceptedAssessorsResource interviewAcceptedAssessorsResource) {
        this.id = interviewAcceptedAssessorsResource.getId();
        this.name = interviewAcceptedAssessorsResource.getName();
        this.skillAreas = interviewAcceptedAssessorsResource.getSkillAreas();
        this.numberOfAllocatedApplications = interviewAcceptedAssessorsResource.getNumberOfAllocatedApplications();
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

    public long getNumberOfAllocatedApplications() {
        return numberOfAllocatedApplications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewAcceptedAssessorsRowViewModel that = (InterviewAcceptedAssessorsRowViewModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(numberOfAllocatedApplications, that.numberOfAllocatedApplications)
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
                .append(numberOfAllocatedApplications)
                .toHashCode();
    }
}
