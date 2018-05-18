package org.innovateuk.ifs.interview.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The resource to describe applications that are assigned to an interview panel in the context of allocating to assessors.
 */
public class InterviewApplicationResource {

    private long id;
    private String name;
    private String leadOrganisation;
    private long numberOfAssessors;

    public InterviewApplicationResource() {
    }

    public InterviewApplicationResource(long id, String name, String leadOrganisation, long numberOfAssessors) {
        this.id = id;
        this.name = name;
        this.leadOrganisation = leadOrganisation;
        this.numberOfAssessors = numberOfAssessors;
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

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(String leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public long getNumberOfAssessors() {
        return numberOfAssessors;
    }

    public void setNumberOfAssessors(long numberOfAssessors) {
        this.numberOfAssessors = numberOfAssessors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewApplicationResource that = (InterviewApplicationResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(numberOfAssessors, that.numberOfAssessors)
                .append(name, that.name)
                .append(leadOrganisation, that.leadOrganisation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(leadOrganisation)
                .append(numberOfAssessors)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("leadOrganisation", leadOrganisation)
                .append("numberOfAssessors", numberOfAssessors)
                .toString();
    }
}
