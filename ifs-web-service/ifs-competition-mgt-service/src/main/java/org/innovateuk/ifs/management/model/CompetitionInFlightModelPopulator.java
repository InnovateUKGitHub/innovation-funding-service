package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.service.MilestoneService;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.CompetitionKeyStatisticsRestService;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightViewModel;
import org.innovateuk.ifs.management.viewmodel.MilestonesRowViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Competition Management Competition in flight dashboard.
 */
@Component
public class CompetitionInFlightModelPopulator {

    @Autowired
    private AssessmentRestService assessmentRestService;

    @Autowired
    private CompetitionKeyStatisticsRestService competitionKeyStatisticsRestService;

    @Autowired
    private MilestoneService milestoneService;

    public CompetitionInFlightViewModel populateModel(CompetitionResource competition) {
        List<MilestoneResource> milestones = milestoneService.getAllMilestonesByCompetitionId(competition.getId());
        CompetitionInFlightStatsViewModel statsViewModel = populateStatsViewModel(competition);

        long changesSinceLastNotify = assessmentRestService.countByStateAndCompetition(AssessmentStates.CREATED, competition.getId()).getSuccessObject();
        milestones.sort(Comparator.comparing(MilestoneResource::getType));
        return new CompetitionInFlightViewModel(competition,
                simpleMap(milestones, MilestonesRowViewModel::new),
                changesSinceLastNotify, statsViewModel);
    }

    private CompetitionInFlightStatsViewModel populateStatsViewModel(CompetitionResource competitionResource) {
        switch (competitionResource.getCompetitionStatus()) {
            case READY_TO_OPEN:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getReadyToOpenKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObject());
            case OPEN:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getOpenKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObject());
            case CLOSED:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getClosedKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObject());
            case IN_ASSESSMENT:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getInAssessmentKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObject());
            case FUNDERS_PANEL:
            case ASSESSOR_FEEDBACK:
                break;
        }
        return new CompetitionInFlightStatsViewModel();
    }
}
