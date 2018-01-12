package org.innovateuk.ifs.assessment.review.populator;

import org.innovateuk.ifs.assessment.review.viewmodel.AssessmentReviewApplicationSummaryViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Application under review view.
 */
@Component
public class AssessmentReviewApplicationSummaryModelPopulator {

    public AssessmentReviewApplicationSummaryViewModel populateModel(long applicationId) {
        return new AssessmentReviewApplicationSummaryViewModel();
    }
}
