package org.innovateuk.ifs.assessment.domain;

import org.innovateuk.ifs.application.domain.Application;

/**
 * Domain object that holds the total number of assessments for
 * an application that an assessor is assessing.
 */
public class ApplicationAssessmentCount {

    private Application application;
    private Assessment assessment;
    private int assessmentCount;

    public ApplicationAssessmentCount(Application application, Assessment assessment, int assessmentCount) {
        this.application = application;
        this.assessment = assessment;
        this.assessmentCount = assessmentCount;
    }

    public Application getApplication() {
        return application;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public int getAssessmentCount() {
        return assessmentCount;
    }
}
