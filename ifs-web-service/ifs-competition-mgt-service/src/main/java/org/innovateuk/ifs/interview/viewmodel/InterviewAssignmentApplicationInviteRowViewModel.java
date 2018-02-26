package org.innovateuk.ifs.interview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the available assessors shown in the 'Find' tab of the Assessment Interview Panel Invite Assessors view.
 */
public class InterviewAssignmentApplicationInviteRowViewModel {

    private final long id;
    private final long applicationId;
    private final String applicationName;
    private final String leadOrganisation;

    public InterviewAssignmentApplicationInviteRowViewModel(long id, long applicationId, String applicationName, String leadOrganisation) {
        this.id = id;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.leadOrganisation = leadOrganisation;
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

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewAssignmentApplicationInviteRowViewModel that = (InterviewAssignmentApplicationInviteRowViewModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(applicationId, that.applicationId)
                .append(applicationName, that.applicationName)
                .append(leadOrganisation, that.leadOrganisation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(applicationId)
                .append(applicationName)
                .append(leadOrganisation)
                .toHashCode();
    }
}