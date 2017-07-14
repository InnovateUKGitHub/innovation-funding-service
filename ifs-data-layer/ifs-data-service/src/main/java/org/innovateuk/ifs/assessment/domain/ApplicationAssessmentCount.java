package org.innovateuk.ifs.assessment.domain;

import org.innovateuk.ifs.application.domain.Application;

/**
 * Domain object that holds counts of the number of assessments per application.
 */
public class ApplicationAssessmentCount {

    private Application application;
    private int count;

    public ApplicationAssessmentCount(Application application, int count) {
        this.application = application;
        this.count = count;
    }

    public Application getApplication() {
        return application;
    }

    public int getCount() {
        return count;
    }
}
