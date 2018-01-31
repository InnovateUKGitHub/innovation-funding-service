package org.innovateuk.ifs.management.model;


import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.InterviewPanelViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Interview Panel dashboard
 */
@Component
public class InterviewPanelModelPopulator {

    @Autowired
    private CompetitionService competitionService;

    public InterviewPanelViewModel populateModel(long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);

        return new InterviewPanelViewModel(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus());
    }
}