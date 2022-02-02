package org.innovateuk.ifs.assessment.resource;

import java.util.List;

/**
 * Aggregate assessor scores for an Application.
 */
public class ApplicationAssessmentsResource {
    private long applicationId;
    private List<ApplicationAssessmentResource> assessments;

    public ApplicationAssessmentsResource() {}

    public ApplicationAssessmentsResource(long applicationId, List<ApplicationAssessmentResource> assessments) {
        this.applicationId = applicationId;
        this.assessments = assessments;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public List<ApplicationAssessmentResource> getAssessments() {
        return assessments;
    }
}
