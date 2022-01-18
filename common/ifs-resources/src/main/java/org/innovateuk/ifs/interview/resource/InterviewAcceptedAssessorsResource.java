package org.innovateuk.ifs.interview.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class InterviewAcceptedAssessorsResource {

    private long id;
    private String name;
    private String skillAreas;
    private long numberOfAllocatedApplications;

    public InterviewAcceptedAssessorsResource() {
    }

    public InterviewAcceptedAssessorsResource(long id, String name, String skillAreas, long numberOfAllocatedApplications) {
        this.id = id;
        this.name = name;
        this.skillAreas = skillAreas;
        this.numberOfAllocatedApplications = numberOfAllocatedApplications;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getNumberOfAllocatedApplications() {
        return numberOfAllocatedApplications;
    }

    public void setNumberOfAllocatedApplications(long numberOfAllocatedApplications) {
        this.numberOfAllocatedApplications = numberOfAllocatedApplications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewAcceptedAssessorsResource that = (InterviewAcceptedAssessorsResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(id, that.id)
                .append(name, that.name)
                .append(skillAreas, that.skillAreas)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(id)
                .append(name)
                .append(skillAreas)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("skillAreas", skillAreas)
                .toString();
    }
}
