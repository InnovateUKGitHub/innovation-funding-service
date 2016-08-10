package com.worth.ifs.assessment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the applications shown on the Assessor Competition Dashboard.
 */
public class AssessorCompetitionDashboardApplicationViewModel {

    private Long applicationId;
    private Long assessmentId;
    private String displayLabel;
    private String leadOrganisation;

    public AssessorCompetitionDashboardApplicationViewModel(Long applicationId, Long assessmentId, String displayLabel, String leadOrganisation) {
        this.applicationId = applicationId;
        this.assessmentId = assessmentId;
        this.displayLabel = displayLabel;
        this.leadOrganisation = leadOrganisation;
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
                .append(applicationId, that.applicationId)
                .append(assessmentId, that.assessmentId)
                .append(displayLabel, that.displayLabel)
                .append(leadOrganisation, that.leadOrganisation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(assessmentId)
                .append(displayLabel)
                .append(leadOrganisation)
                .toHashCode();
    }
}
