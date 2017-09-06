package org.innovateuk.ifs.management.model;


import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.AssessmentPanelViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Assessment Panel dashboard
 */
@Component
public class AssessmentPanelModelPopulator {

    @Autowired
    private CompetitionService competitionService;

    public AssessmentPanelViewModel populateModel(long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);

        return new AssessmentPanelViewModel(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus());
    }
}
