package com.worth.ifs.assessment.model;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.RejectAssessmentViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Reject Assessment view.
 */
@Component
public class RejectAssessmentModelPopulator {

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private ApplicationService applicationService;

    public RejectAssessmentViewModel populateModel(Long assessmentId) {
        AssessmentResource assessment = assessmentService.getById(assessmentId);
        ApplicationResource application = applicationService.getById(assessment.getApplication());

        return new RejectAssessmentViewModel(assessment.getId(), application);
    }

}