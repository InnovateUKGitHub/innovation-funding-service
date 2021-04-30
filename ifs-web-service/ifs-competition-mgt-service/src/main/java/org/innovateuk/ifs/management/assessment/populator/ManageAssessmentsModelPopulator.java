package org.innovateuk.ifs.management.assessment.populator;

import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.service.CompetitionKeyAssessmentStatisticsRestService;
import org.innovateuk.ifs.commons.resource.PageResource;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.assessment.viewmodel.ManageAssessmentsViewModel;
import org.innovateuk.ifs.management.assessmentperiod.form.ManageAssessmentPeriodsForm;
import org.innovateuk.ifs.management.assessmentperiod.populator.AssessmentPeriodFormPopulator;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private AssessmentPeriodFormPopulator assessmentPeriodFormPopulator;

    @Autowired
    private AssessmentPeriodRestService assessmentPeriodRestService;

    public ManageAssessmentsViewModel populateModel(long competitionId, int page) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionInAssessmentKeyAssessmentStatisticsResource keyStatistics =
                competitionKeyAssessmentStatisticsRestService.getInAssessmentKeyStatisticsByCompetition
                        (competitionId).getSuccess();
        PageResource<AssessmentPeriodResource> periods = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId, page, 3).getSuccess();
        ManageAssessmentPeriodsForm assessmentPeriodForm = assessmentPeriodFormPopulator.populate(competitionId, periods);

        return new ManageAssessmentsViewModel(competition, keyStatistics, assessmentPeriodForm.getAssessmentPeriods(), new PaginationViewModel(periods));
    }

}
