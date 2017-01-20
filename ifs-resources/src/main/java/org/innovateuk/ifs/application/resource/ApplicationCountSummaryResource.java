package org.innovateuk.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents an application's statistics
 */
public class ApplicationCountSummaryResource {
    private Long id;
    private String name;
    private Long leadOrganisationId;
    private long assessors;
    private long accepted;
    private long submitted;

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

    public Long getLeadOrganisationId() {
        return leadOrganisationId;
    }

    public void setLeadOrganisationId(Long leadOrganisationId) {
        this.leadOrganisationId = leadOrganisationId;
    }

    public long getAssessors() {
        return assessors;
    }

    public void setAssessors(long assessors) {
        this.assessors = assessors;
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

        ApplicationCountSummaryResource that = (ApplicationCountSummaryResource) o;

        return new EqualsBuilder()
                .append(assessors, that.assessors)
                .append(accepted, that.accepted)
                .append(submitted, that.submitted)
                .append(id, that.id)
                .append(name, that.name)
                .append(leadOrganisationId, that.leadOrganisationId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(leadOrganisationId)
                .append(assessors)
                .append(accepted)
                .append(submitted)
                .toHashCode();
    }
}
