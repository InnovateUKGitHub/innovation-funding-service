package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents projects for which Spend Profile generation is pending, for a given competition
 */
public class CompetitionPendingSpendProfilesResource {

    private Long applicationId;

    private Long projectId;

    private String projectName;

    public CompetitionPendingSpendProfilesResource() {
    }

    public CompetitionPendingSpendProfilesResource(Long applicationId, Long projectId, String projectName) {
        this.applicationId = applicationId;
        this.projectId = projectId;
        this.projectName = projectName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionPendingSpendProfilesResource that = (CompetitionPendingSpendProfilesResource) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(projectId, that.projectId)
                .append(projectName, that.projectName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(projectId)
                .append(projectName)
                .toHashCode();
    }
}
