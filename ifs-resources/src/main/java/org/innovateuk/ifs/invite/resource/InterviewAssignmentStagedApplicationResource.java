package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for a created assessor invite that is ready to be sent.
 */
public class InterviewAssignmentStagedApplicationResource {

    private final long id;
    private final long applicationId;
    private final String applicationName;
    private final String leadOrganisationName;
    private final String filename;

    public InterviewAssignmentStagedApplicationResource() {
        id = -1;
        applicationId = -1;
        applicationName = null;
        leadOrganisationName = null;
        filename = null;
    }

    public InterviewAssignmentStagedApplicationResource(long id, long applicationId, String applicationName, String leadOrganisationName, String filename) {
        this.id = id;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.leadOrganisationName = leadOrganisationName;
        this.filename = filename;
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

    public String getFilename() {
        return filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewAssignmentStagedApplicationResource that = (InterviewAssignmentStagedApplicationResource) o;

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