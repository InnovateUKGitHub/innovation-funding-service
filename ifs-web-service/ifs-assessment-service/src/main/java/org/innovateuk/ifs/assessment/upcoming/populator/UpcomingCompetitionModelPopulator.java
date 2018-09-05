package org.innovateuk.ifs.assessment.upcoming.populator;

import org.innovateuk.ifs.assessment.upcoming.viewmodel.UpcomingCompetitionViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
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

    public UpcomingCompetitionViewModel populateModel(Long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        return new UpcomingCompetitionViewModel(competition);
    }
}
