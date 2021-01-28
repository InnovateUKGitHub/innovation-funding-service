package org.innovateuk.ifs.management.assessment.populator;

import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.service.CompetitionKeyAssessmentStatisticsRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.assessment.viewmodel.ManageAssessmentsViewModel;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;
import org.innovateuk.ifs.management.assessmentperiod.service.AssessmentPeriodService;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestonesForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Populates the model for the 'Manage assessments' page.
 */
@Component
public class ManageAssessmentsModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionKeyAssessmentStatisticsRestService competitionKeyAssessmentStatisticsRestService;

    @Autowired
    private AssessmentPeriodService assessmentPeriodService;

    public ManageAssessmentsViewModel populateModel(long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionInAssessmentKeyAssessmentStatisticsResource keyStatistics =
                competitionKeyAssessmentStatisticsRestService.getInAssessmentKeyStatisticsByCompetition
                        (competitionId).getSuccess();

        List<AssessmentPeriodForm> milestonesForms = assessmentPeriodService.getAssessmentPeriodMilestonesForms(competitionId);

        return new ManageAssessmentsViewModel(competition, keyStatistics, milestonesForms);
    }

}
