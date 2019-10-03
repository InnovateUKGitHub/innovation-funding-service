package org.innovateuk.ifs.assessment.resource.dashboard;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentState;

public class ApplicationAssessmentResource {

    private long applicationId;
    private long assessmentId;
    private String competitionName;
    private String leadOrganisation;
    private AssessmentState state;
    private Integer overallScore;
    private boolean recommended;

    public ApplicationAssessmentResource() {}

    public ApplicationAssessmentResource(long applicationId, long assessmentId, String competitionName, String leadOrganisation, AssessmentState state, Integer overallScore, boolean recommended) {
        this.applicationId = applicationId;
        this.assessmentId = assessmentId;
        this.competitionName = competitionName;
        this.leadOrganisation = leadOrganisation;
        this.state = state;
        this.overallScore = overallScore;
        this.recommended = recommended;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public AssessmentState getState() {
        return state;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public boolean isRecommended() {
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