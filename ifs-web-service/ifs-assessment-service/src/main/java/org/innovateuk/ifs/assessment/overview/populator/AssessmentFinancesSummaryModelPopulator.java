package org.innovateuk.ifs.assessment.overview.populator;

import org.innovateuk.ifs.application.finance.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.overview.viewmodel.AssessmentFinancesSummaryViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssessmentFinancesSummaryModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;
    @Autowired
    private AssessmentService assessmentService;
    @Autowired
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;
    @Autowired
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;

    public AssessmentFinancesSummaryViewModel populateModel(Long assessmentId, UserResource user) {
        AssessmentResource assessment = assessmentService.getById(assessmentId);
        CompetitionResource competition = competitionRestService.getCompetitionById(assessment.getCompetition()).getSuccess();

        return new AssessmentFinancesSummaryViewModel(assessmentId, assessment.getApplication(),
                assessment.getApplicationName(),
                competition.getAssessmentDaysLeft(),
                competition.getAssessmentDaysLeftPercentage(),
                competition.getFundingType(),
                applicationFinanceSummaryViewModelPopulator.populate(assessment.getApplication(), user),
                applicationFundingBreakdownViewModelPopulator.populate(assessment.getApplication(), user)
        );
    }
}

