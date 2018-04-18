package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * View model for partner project location
 */
public class PartnerProjectLocationViewModel {
    private long projectId;
    private String projectName;
    private long organisationId;

    public PartnerProjectLocationViewModel(long projectId, String projectName, long organisationId) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.organisationId = organisationId;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(long organisationId) {
        this.organisationId = organisationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PartnerProjectLocationViewModel that = (PartnerProjectLocationViewModel) o;

        return new EqualsBuilder()
                .append(projectId, that.projectId)
                .append(projectName, that.projectName)
                .append(organisationId, that.organisationId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(projectName)
                .append(organisationId)
                .toHashCode();
    }
}


