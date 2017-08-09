package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Resource describing an assessor's assessment of an application.
 *
 * This should be used in the context of creating screens for
 * Competition Management, specifically the Assessor Progress page.
 */
public class AssessorAssessmentResource {

    private long applicationId;
    private String applicationName;
    private String leadOrganisation;
    private int totalAssessors;
    private AssessmentStates state;
    private long assessmentId;

    public AssessorAssessmentResource() {
    }

    public AssessorAssessmentResource(long applicationId,
                                      String applicationName,
                                      String leadOrganisation,
                                      int totalAssessors,
                                      AssessmentStates state,
                                      long assessmentId) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.leadOrganisation = leadOrganisation;
        this.totalAssessors = totalAssessors;
        this.state = state;
        this.assessmentId = assessmentId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(String leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public int getTotalAssessors() {
        return totalAssessors;
    }

    public void setTotalAssessors(int totalAssessors) {
        this.totalAssessors = totalAssessors;
    }

    public AssessmentStates getState() {
        return state;
    }

    public void setState(AssessmentStates state) {
        this.state = state;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorAssessmentResource that = (AssessorAssessmentResource) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(totalAssessors, that.totalAssessors)
                .append(assessmentId, that.assessmentId)
                .append(applicationName, that.applicationName)
                .append(leadOrganisation, that.leadOrganisation)
                .append(state, that.state)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(applicationName)
                .append(leadOrganisation)
                .append(totalAssessors)
                .append(state)
                .append(assessmentId)
                .toHashCode();
    }
}
