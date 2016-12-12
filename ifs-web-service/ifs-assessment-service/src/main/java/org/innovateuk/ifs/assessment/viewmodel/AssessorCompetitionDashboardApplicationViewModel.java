package org.innovateuk.ifs.assessment.viewmodel;

import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;

/**
 * Holder of model attributes for the applications shown on the Assessor Competition Dashboard.
 */
public class AssessorCompetitionDashboardApplicationViewModel {

    private Long applicationId;
    private Long assessmentId;
    private String displayLabel;
    private String leadOrganisation;
    private AssessmentStates state;
    private int overallScore;
    private boolean recommended;

    public AssessorCompetitionDashboardApplicationViewModel(Long applicationId, Long assessmentId, String displayLabel, String leadOrganisation, AssessmentStates state, int overallScore, boolean recommended) {
        this.applicationId = applicationId;
        this.assessmentId = assessmentId;
        this.displayLabel = displayLabel;
        this.leadOrganisation = leadOrganisation;
        this.state = state;
        this.overallScore = overallScore;
        this.recommended = recommended;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(String leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public AssessmentStates getState() {
        return state;
    }

    public void setState(AssessmentStates state) {
        this.state = state;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public boolean isPending() {
        return isState(PENDING);
    }

    public boolean isAccepted() {
        return isState(ACCEPTED);
    }

    public boolean isOpen() {
        return isState(OPEN);
    }

    public boolean isReadyToSubmit() {
        return isState(READY_TO_SUBMIT);
    }

    public boolean isSubmitted() {
        return isState(SUBMITTED);
    }

    private boolean isState(AssessmentStates state) {
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

        AssessorCompetitionDashboardApplicationViewModel that = (AssessorCompetitionDashboardApplicationViewModel) o;

        return new EqualsBuilder()
                .append(recommended, that.recommended)
                .append(applicationId, that.applicationId)
                .append(assessmentId, that.assessmentId)
                .append(displayLabel, that.displayLabel)
                .append(leadOrganisation, that.leadOrganisation)
                .append(state, that.state)
                .append(overallScore, that.overallScore)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(assessmentId)
                .append(displayLabel)
                .append(leadOrganisation)
                .append(state)
                .append(overallScore)
                .append(recommended)
                .toHashCode();
    }
}
