package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;

public class InterviewAssignmentApplicationResource {

    private final long id;
    private final long applicationId;
    private final String applicationName;
    private final String leadOrganisationName;
    private final InterviewAssignmentState status;

    public InterviewAssignmentApplicationResource(long id,
                                                  long applicationId,
                                                  String applicationName,
                                                  String leadOrganisationName,
                                                  InterviewAssignmentState status
    ) {
        this.id = id;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.leadOrganisationName = leadOrganisationName;
        this.status = status;
    }

    public InterviewAssignmentApplicationResource() {
        id = -1;
        applicationId = -1;
        applicationName = null;
        leadOrganisationName = null;
        status = null;
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

    public InterviewAssignmentState getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewAssignmentApplicationResource that = (InterviewAssignmentApplicationResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(applicationId, that.applicationId)
                .append(applicationName, that.applicationName)
                .append(leadOrganisationName, that.leadOrganisationName)
                .append(status, that.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(applicationId)
                .append(applicationName)
                .append(leadOrganisationName)
                .append(status)
                .toHashCode();
    }
}