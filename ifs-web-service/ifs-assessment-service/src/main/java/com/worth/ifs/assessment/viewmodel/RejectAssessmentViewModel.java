package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;

/**
 * Holder of model attributes for the Reject Assessment view.
 */
public class RejectAssessmentViewModel {

    private Long assessmentId;
    private ApplicationResource application;

    public RejectAssessmentViewModel(Long assessmentId, ApplicationResource application) {
        this.assessmentId = assessmentId;
        this.application = application;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }
}