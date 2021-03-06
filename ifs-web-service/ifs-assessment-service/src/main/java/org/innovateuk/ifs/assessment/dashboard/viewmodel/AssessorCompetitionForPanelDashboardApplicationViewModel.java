package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.review.resource.ReviewState;

import static org.innovateuk.ifs.review.resource.ReviewState.ACCEPTED;
import static org.innovateuk.ifs.review.resource.ReviewState.PENDING;

/**
 * Holder of model attributes for the applications shown on the Assessor Competition for Panel Dashboard.
 */
public class AssessorCompetitionForPanelDashboardApplicationViewModel {

    private long applicationId;
    private long reviewId;
    private String displayLabel;
    private String leadOrganisation;
    private ReviewState state;

    public AssessorCompetitionForPanelDashboardApplicationViewModel(long applicationId,
                                                                    long reviewId,
                                                                    String displayLabel,
                                                                    String leadOrganisation,
                                                                    ReviewState state) {
        this.applicationId = applicationId;
        this.reviewId = reviewId;
        this.displayLabel = displayLabel;
        this.leadOrganisation = leadOrganisation;
        this.state = state;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getReviewId() {
        return reviewId;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public ReviewState getState() {
        return state;
    }

    public boolean isPending() {
        return isState(PENDING);
    }

    public boolean isAccepted() {
        return isState(ACCEPTED);
    }

    private boolean isState(ReviewState state) {
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
