package org.innovateuk.ifs.assessment.model;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.assessment.viewmodel.RejectAssessmentViewModel;
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

        return new RejectAssessmentViewModel(assessment.getId(),
                application.getId(),
                application.getApplicationDisplayName()
        );
    }

}
