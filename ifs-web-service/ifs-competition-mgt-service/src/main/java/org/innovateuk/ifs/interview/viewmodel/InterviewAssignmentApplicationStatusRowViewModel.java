package org.innovateuk.ifs.interview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;

/**
 * Holder of model attributes for the applications shown in the 'view status' tab of the Assign applications to interview panel view.
 */
public class InterviewAssignmentApplicationStatusRowViewModel {

    private final long id;
    private final long applicationId;
    private final String applicationName;
    private final String leadOrganisation;
    private final InterviewAssignmentState status;

    public InterviewAssignmentApplicationStatusRowViewModel(long id, long applicationId, String applicationName, String leadOrganisation, InterviewAssignmentState status) {
        this.id = id;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.leadOrganisation = leadOrganisation;
        this.status = status;
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

    public InterviewAssignmentState getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewAssignmentApplicationStatusRowViewModel that = (InterviewAssignmentApplicationStatusRowViewModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(applicationId, that.applicationId)
                .append(applicationName, that.applicationName)
                .append(leadOrganisation, that.leadOrganisation)
                .append(status, that.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(applicationId)
                .append(applicationName)
                .append(leadOrganisation)
                .append(status)
                .toHashCode();
    }
}