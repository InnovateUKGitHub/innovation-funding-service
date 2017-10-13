package org.innovateuk.ifs.competition.resource;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;

/**
 * Represents open Queries of a Competition
 */
public class CompetitionOpenQueryResource {

    private Long applicationId;

    private Long organisationId;

    private String organisationName;

    private Long projectId;

    private String projectName;

    ZonedDateTime createdOn;

    // constructor compatible with SQL types
    public CompetitionOpenQueryResource(long applicationId, long organisationId, String organisationName, long projectId, String projectName, ZonedDateTime createdOn) {
        this.applicationId = applicationId;
        this.organisationId = organisationId;
        this.organisationName = organisationName;

        this.projectId = projectId;
        this.projectName = projectName;
        this.createdOn = createdOn;
    }

    public CompetitionOpenQueryResource() {}

    public Long getApplicationId() { return applicationId; }

    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public Long getOrganisationId() { return organisationId; }

    public void setOrganisationId(Long organisationId) { this.organisationId = organisationId; }

    public String getOrganisationName() { return organisationName; }

    public void setOrganisationName(String organisationName) { this.organisationName = organisationName; }

    public Long getProjectId() { return projectId; }

    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }

    public void setProjectName(String projectName) { this.projectName = projectName; }

    public ZonedDateTime getCreatedOn() { return createdOn; }

    public void setCreatedOn(ZonedDateTime createdOn) { this.createdOn = createdOn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionOpenQueryResource that = (CompetitionOpenQueryResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(applicationId, that.applicationId)
                .append(organisationId, that.organisationId)
                .append(organisationName, that.organisationName)
                .append(projectId, that.projectId)
                .append(projectName, that.projectName)
                .append(createdOn, that.createdOn)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(applicationId)
                .append(organisationId)
                .append(organisationName)
                .append(projectId)
                .append(projectName)
                .append(createdOn)
                .toHashCode();
    }
}
