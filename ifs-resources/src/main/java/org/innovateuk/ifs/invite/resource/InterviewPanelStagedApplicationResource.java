package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for a created assessor invite that is ready to be sent.
 */
public class InterviewPanelStagedApplicationResource {

    private final long id;
    private final long applicationId;
    private final String applicationName;
    private final String leadOrganisationName;

    public InterviewPanelStagedApplicationResource() {
        id = -1;
        applicationId = -1;
        applicationName = null;
        leadOrganisationName = null;
    }

    public InterviewPanelStagedApplicationResource(long id, long applicationId, String applicationName, String leadOrganisationName) {
        this.id = id;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.leadOrganisationName = leadOrganisationName;
    }

    public long getId() {
        return id;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getLeadOrganisationName() {
        return leadOrganisationName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewPanelStagedApplicationResource that = (InterviewPanelStagedApplicationResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(applicationId, that.applicationId)
                .append(applicationName, that.applicationName)
                .append(leadOrganisationName, that.leadOrganisationName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(applicationId)
                .append(applicationName)
                .append(leadOrganisationName)
                .toHashCode();
    }
}