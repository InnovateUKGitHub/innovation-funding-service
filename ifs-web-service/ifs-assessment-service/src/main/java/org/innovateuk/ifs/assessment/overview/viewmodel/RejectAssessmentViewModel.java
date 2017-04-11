package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;

/**
 * Holder of model attributes for the Reject Assessment view.
 */
public class RejectAssessmentViewModel {

    private long assessmentId;
    private long applicationId;
    private String applicationName;
    private AssessmentStates assessmentState;

    public RejectAssessmentViewModel(long assessmentId, long applicationId, String applicationName, AssessmentStates assessmentStates) {
        this.assessmentId = assessmentId;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.assessmentState = assessmentStates;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public AssessmentStates getAssessmentState() {
        return assessmentState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RejectAssessmentViewModel that = (RejectAssessmentViewModel) o;

        return new EqualsBuilder()
                .append(assessmentId, that.assessmentId)
                .append(applicationId, that.applicationId)
                .append(applicationName, that.applicationName)
                .append(assessmentState, that.assessmentState)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentId)
                .append(applicationId)
                .append(applicationName)
                .append(assessmentState)
                .toHashCode();
    }
}
