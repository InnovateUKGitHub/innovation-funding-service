package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ApplicationAssessmentResource {

    private Long applicationId;
    private Long assessmentId;
    private String competitionName;
    private String leadOrganisation;
    private AssessmentState state;
    private int overallScore;
    private Boolean recommended;

    public ApplicationAssessmentResource() {}

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public String getDisplayLabel() {
        return competitionName;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public AssessmentState getState() {
        return state;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public Boolean getRecommended() {
        return recommended;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationAssessmentResource that = (ApplicationAssessmentResource) o;
        return new EqualsBuilder()
                .append(overallScore, that.overallScore)
                .append(applicationId, that.applicationId)
                .append(assessmentId, that.assessmentId)
                .append(competitionName, that.competitionName)
                .append(leadOrganisation, that.leadOrganisation)
                .append(state, that.state)
                .append(recommended, that.recommended)
                .isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(overallScore)
                .append(applicationId)
                .append(assessmentId)
                .append(competitionName)
                .append(leadOrganisation)
                .append(state)
                .append(recommended)
                .toHashCode();
    }
}
