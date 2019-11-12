package org.innovateuk.ifs.assessment.resource.dashboard;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentState;

import static java.lang.Integer.compare;

public class ApplicationAssessmentResource implements Comparable<ApplicationAssessmentResource> {

    private long applicationId;
    private long assessmentId;
    private String applicationName;
    private String leadOrganisation;
    private AssessmentState state;
    private int overallScore;
    private Boolean recommended;

    public ApplicationAssessmentResource() {}

    public ApplicationAssessmentResource(long applicationId,
                                         long assessmentId,
                                         String applicationName,
                                         String leadOrganisation,
                                         AssessmentState state,
                                         int overallScore,
                                         Boolean recommended) {
        this.applicationId = applicationId;
        this.assessmentId = assessmentId;
        this.applicationName = applicationName;
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

    public String getApplicationName() {
        return applicationName;
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

    public Boolean isRecommended() {
        return recommended;
    }

    public boolean isReadyToSubmit() {
        return state == AssessmentState.READY_TO_SUBMIT;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationAssessmentResource that = (ApplicationAssessmentResource) o;
        return new EqualsBuilder()
                .append(overallScore, that.overallScore)
                .append(applicationId, that.applicationId)
                .append(assessmentId, that.assessmentId)
                .append(applicationName, that.applicationName)
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
                .append(applicationName)
                .append(leadOrganisation)
                .append(state)
                .append(recommended)
                .toHashCode();
    }

    @Override
    public int compareTo(ApplicationAssessmentResource o) {
        return compare(this.getState().getPriority(), o.getState().getPriority());
    }
}