package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState;

import static org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState.ACCEPTED;
import static org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState.PENDING;

/**
 * Holder of model attributes for the applications shown on the Assessor Competition for Panel Dashboard.
 */
public class AssessorCompetitionForPanelDashboardApplicationViewModel {

    private Long applicationId;
    private Long reviewId;
    private String displayLabel;
    private String leadOrganisation;
    private AssessmentReviewState state;

    public AssessorCompetitionForPanelDashboardApplicationViewModel(Long applicationId,
                                                                    Long reviewId,
                                                                    String displayLabel,
                                                                    String leadOrganisation,
                                                                    AssessmentReviewState state) {
        this.applicationId = applicationId;
        this.reviewId = reviewId;
        this.displayLabel = displayLabel;
        this.leadOrganisation = leadOrganisation;
        this.state = state;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public AssessmentReviewState getState() {
        return state;
    }

    public boolean isPending() {
        return isState(PENDING);
    }

    public boolean isAccepted() {
        return isState(ACCEPTED);
    }

    private boolean isState(AssessmentReviewState state) {
        return state == this.state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorCompetitionForPanelDashboardApplicationViewModel that = (AssessorCompetitionForPanelDashboardApplicationViewModel) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(reviewId, that.reviewId)
                .append(displayLabel, that.displayLabel)
                .append(leadOrganisation, that.leadOrganisation)
                .append(state, that.state)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(reviewId)
                .append(displayLabel)
                .append(leadOrganisation)
                .append(state)
                .toHashCode();
    }
}
