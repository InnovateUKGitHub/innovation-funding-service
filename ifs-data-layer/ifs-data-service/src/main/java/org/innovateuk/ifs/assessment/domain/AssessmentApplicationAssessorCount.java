package org.innovateuk.ifs.assessment.domain;

import org.innovateuk.ifs.application.domain.Application;

/**
 * Domain object that holds the total number of assessments for
 * an application that an assessor is assessing.
 */
public class AssessmentApplicationAssessorCount {

    private Assessment assessment;
    private Application application;
    private int assessorCount;

    public AssessmentApplicationAssessorCount(Assessment assessment, Application application, int assessorCount) {
        this.assessment = assessment;
        this.application = application;
        this.assessorCount = assessorCount;
    }

    public AssessmentApplicationAssessorCount() {
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public Application getApplication() {
        return application;
    }

    public int getAssessorCount() {
        return assessorCount;
    }
}
