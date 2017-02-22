package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.service.CompetitionService;
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
    private CompetitionService competitionService;

    @Autowired
    private CompetitionKeyStatisticsRestService competitionKeyStatisticsRestService;

    @Autowired
    private MilestoneService milestoneService;

    public CompetitionInFlightViewModel populateModel(Long competitionId) {
        return populateModel(competitionService.getById(competitionId));
    }

    public CompetitionInFlightViewModel populateModel(CompetitionResource competition) {
        List<MilestoneResource> milestones = milestoneService.getAllMilestonesByCompetitionId(competition.getId());
        CompetitionInFlightStatsViewModel statsViewModel = populateStatsViewModel(competition);

        long changesSinceLastNotify = assessmentRestService.countByStateAndCompetition(AssessmentStates.CREATED, competition.getId()).getSuccessObjectOrThrowException();
        milestones.sort(Comparator.comparing(MilestoneResource::getType));
        return new CompetitionInFlightViewModel(competition,
                simpleMap(milestones, MilestonesRowViewModel::new),
                changesSinceLastNotify, statsViewModel);
    }

    private CompetitionInFlightStatsViewModel populateStatsViewModel(CompetitionResource competitionResource) {
        switch (competitionResource.getCompetitionStatus()) {
            case READY_TO_OPEN:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getReadyToOpenKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObjectOrThrowException());
            case OPEN:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getOpenKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObjectOrThrowException());
            case CLOSED:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getClosedKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObjectOrThrowException());
            case IN_ASSESSMENT:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getInAssessmentKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObjectOrThrowException());
            case FUNDERS_PANEL:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getFundedKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObjectOrThrowException());
            case ASSESSOR_FEEDBACK:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getFundedKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObjectOrThrowException());
        }
        return new CompetitionInFlightStatsViewModel();
    }
}
