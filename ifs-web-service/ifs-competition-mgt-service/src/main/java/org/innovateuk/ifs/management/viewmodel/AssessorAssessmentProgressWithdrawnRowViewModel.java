package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for a previously assigned applications in the Assessor Progress view.
 */
public class AssessorAssessmentProgressWithdrawnRowViewModel {

    private long applicationId;
    private String applicationName;
    private String leadOrganisation;
    private long totalAssessors;

    public AssessorAssessmentProgressWithdrawnRowViewModel(long applicationId,
                                                           String applicationName,
                                                           String leadOrganisation,
                                                           long totalAssessors) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.leadOrganisation = leadOrganisation;
        this.totalAssessors = totalAssessors;
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

    public long getTotalAssessors() {
        return totalAssessors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorAssessmentProgressWithdrawnRowViewModel that = (AssessorAssessmentProgressWithdrawnRowViewModel) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(totalAssessors, that.totalAssessors)
                .append(applicationName, that.applicationName)
                .append(leadOrganisation, that.leadOrganisation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(applicationName)
                .append(leadOrganisation)
                .append(totalAssessors)
                .toHashCode();
    }
}