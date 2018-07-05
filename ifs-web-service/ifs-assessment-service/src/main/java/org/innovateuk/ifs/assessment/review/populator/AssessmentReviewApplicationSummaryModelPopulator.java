package org.innovateuk.ifs.assessment.review.populator;

import org.innovateuk.ifs.application.common.populator.SummaryViewModelFragmentPopulator;
import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.review.viewmodel.AssessmentReviewApplicationSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Application under review view.
 */
@Component
public class AssessmentReviewApplicationSummaryModelPopulator {

    @Autowired
    private SummaryViewModelFragmentPopulator summaryViewModelPopulator;

    @Autowired
    private CompetitionService competitionService;

    public AssessmentReviewApplicationSummaryViewModel populateModel(ApplicationForm form, UserResource user, long applicationId) {
        form.setAdminMode(true);
        SummaryViewModel viewModel = summaryViewModelPopulator.populate(applicationId, user, form);
        CompetitionResource competition = competitionService.getById(viewModel.getCurrentApplication().getCompetition());
        return new AssessmentReviewApplicationSummaryViewModel(viewModel, competition);
    }
}
