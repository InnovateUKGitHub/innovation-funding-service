package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Resource to hold Bank Details review fields.
 */
public class BankDetailsReviewResource {

    private Long applicationId;

    private Long competitionId;

    private String competitionName;

    private Long projectId;

    private String projectName;

    private Long organisationId;

    private String organisationName;


    public BankDetailsReviewResource() {
    }

    public BankDetailsReviewResource(Long applicationId, Long competitionId, String competitionName,
                                     Long projectId, String projectName,
                                     Long organisationId, String organisationName) {
        this.applicationId = applicationId;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.projectId = projectId;
        this.projectName = projectName;
        this.organisationId = organisationId;
        this.organisationName = organisationName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        BankDetailsReviewResource that = (BankDetailsReviewResource) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(projectId, that.projectId)
                .append(projectName, that.projectName)
                .append(organisationId, that.organisationId)
                .append(organisationName, that.organisationName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(competitionId)
                .append(competitionName)
                .append(projectId)
                .append(projectName)
                .append(organisationId)
                .append(organisationName)
                .toHashCode();
    }
}

