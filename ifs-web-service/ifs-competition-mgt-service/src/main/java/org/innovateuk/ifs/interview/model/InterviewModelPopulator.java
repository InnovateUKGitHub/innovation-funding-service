package org.innovateuk.ifs.interview.model;


import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.viewmodel.InterviewViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Interview Panel dashboard
 */
@Component
public class InterviewModelPopulator {

    @Autowired
    private CompetitionService competitionService;

    public InterviewViewModel populateModel(long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);

        return new InterviewViewModel(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus());
    }
}