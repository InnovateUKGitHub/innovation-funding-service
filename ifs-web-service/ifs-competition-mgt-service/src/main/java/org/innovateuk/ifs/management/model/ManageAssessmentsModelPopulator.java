package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.competition.resource.CompetitionInAssessmentKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionKeyStatisticsRestService;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.innovateuk.ifs.management.viewmodel.ManageAssessmentsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Populates the model for the 'Manage assessments' page.
 */
@Component
public class ManageAssessmentsModelPopulator {

    @Autowired
    private CompetitionsRestService competitionsRestService;

    @Autowired
    private CompetitionKeyStatisticsRestService competitionKeyStatisticsRestService;

    public ManageAssessmentsViewModel populateModel(long competitionId) {
        CompetitionResource competition = competitionsRestService.getCompetitionById(competitionId).getSuccessObjectOrThrowException();
        CompetitionInAssessmentKeyStatisticsResource keyStatistics = competitionKeyStatisticsRestService.getInAssessmentKeyStatisticsByCompetition(competitionId).getSuccessObjectOrThrowException();

        return new ManageAssessmentsViewModel(competition, keyStatistics);
    }
}
