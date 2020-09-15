package org.innovateuk.ifs.assessment.upcoming.populator;

import org.innovateuk.ifs.assessment.upcoming.viewmodel.UpcomingCompetitionViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Upcoming Competition view.
 */
@Component
public class UpcomingCompetitionModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    public UpcomingCompetitionViewModel populateModel(Long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionAssessmentConfigResource competitionAssessmentConfigResource = competitionAssessmentConfigRestService.findOneByCompetitionId(competition.getId()).getSuccess();


        return new UpcomingCompetitionViewModel(competition, competitionAssessmentConfigResource);
    }
}
