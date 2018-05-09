package org.innovateuk.ifs.interview.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class InterviewApplicationResource {

    private Long id;
    private String name;
    private String leadOrganisation;
    private Long numberOfAssessors;

    public InterviewApplicationResource() {
    }

    public InterviewApplicationResource(Long id, String name, String leadOrganisation, Long numberOfAssessors) {
        this.id = id;
        this.name = name;
        this.leadOrganisation = leadOrganisation;
        this.numberOfAssessors = numberOfAssessors;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getNumberOfAssessors() {
        return numberOfAssessors;
    }

    public void setNumberOfAssessors(Long numberOfAssessors) {
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
