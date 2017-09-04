package org.innovateuk.ifs.management.model;


import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.service.CompetitionAssessmentPanelService;
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

    @Autowired
    private CompetitionAssessmentPanelService competitionAssessmentPanelService;

    public AssessmentPanelViewModel populateModel(long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        AssessmentPanelKeyStatisticsResource keyStatistics = competitionAssessmentPanelService.getAssessmentPanelKeyStatistics(competitionId);


        return new AssessmentPanelViewModel(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                keyStatistics.getApplicationsInPanel(),
                keyStatistics.getAssessorsPending(),
                keyStatistics.getAssessorsAccepted());
    }
}
