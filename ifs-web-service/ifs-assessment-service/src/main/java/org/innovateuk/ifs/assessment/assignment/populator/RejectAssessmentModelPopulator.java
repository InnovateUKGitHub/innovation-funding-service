package org.innovateuk.ifs.assessment.assignment.populator;

import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.assessment.overview.viewmodel.RejectAssessmentViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Reject Assessment view.
 */
@Component
public class RejectAssessmentModelPopulator {

    @Autowired
    private AssessmentService assessmentService;

    public RejectAssessmentViewModel populateModel(Long assessmentId) {
        AssessmentResource assessment = assessmentService.getRejectableById(assessmentId);

        return new RejectAssessmentViewModel(assessment.getId(),
                assessment.getApplication(),
                assessment.getApplicationName(),
                assessment.getAssessmentState()
        );
    }

}
