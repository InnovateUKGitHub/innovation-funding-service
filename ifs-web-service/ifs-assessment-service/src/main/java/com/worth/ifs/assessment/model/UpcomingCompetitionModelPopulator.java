package com.worth.ifs.assessment.model;

import com.worth.ifs.assessment.viewmodel.UpcomingCompetitionViewModel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.service.CompetitionsRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Upcoming Competition view.
 */
@Component
public class UpcomingCompetitionModelPopulator {

    @Autowired
    private CompetitionsRestService competitionsRestService;

    public UpcomingCompetitionViewModel populateModel(Long competitionId) {
        CompetitionResource competition = competitionsRestService.getCompetitionById(competitionId).getSuccessObjectOrThrowException();
        return new UpcomingCompetitionViewModel(competition.getName(), competition.getDescription(), competition.getStartDate(), competition.getEndDate());
    }
}
