package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.service.MilestoneService;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionKeyStatisticsResource;
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
        CompetitionKeyStatisticsResource competitionKeyStatisticsResource = competitionKeyStatisticsRestService.getKeyStatisticsByCompetition(competition.getId()).getSuccessObject();

        long changesSinceLastNotify = assessmentRestService.countByStateAndCompetition(AssessmentStates.CREATED, competition.getId()).getSuccessObject();
        milestones.sort(Comparator.comparing(MilestoneResource::getType));
        return new CompetitionInFlightViewModel(competition,
                simpleMap(milestones, MilestonesRowViewModel::new),
                changesSinceLastNotify, populateStatsViewModel(competition, competitionKeyStatisticsResource));
    }

    private CompetitionInFlightStatsViewModel populateStatsViewModel(CompetitionResource competitionResource, CompetitionKeyStatisticsResource competitionKeyStatisticsResource) {
        CompetitionInFlightStatsViewModel competitionInFlightStatsViewModel = new CompetitionInFlightStatsViewModel();
        switch (competitionResource.getCompetitionStatus()) {
            case READY_TO_OPEN:
                competitionInFlightStatsViewModel.setStatOne(competitionKeyStatisticsResource.getAssessorsInvited());
                competitionInFlightStatsViewModel.setStatTwo(competitionKeyStatisticsResource.getAssessorsAccepted());
                break;
            case OPEN:
                competitionInFlightStatsViewModel.setStatOne(competitionKeyStatisticsResource.getAssessorsInvited());
                competitionInFlightStatsViewModel.setStatTwo(competitionKeyStatisticsResource.getAssessorsAccepted());
                competitionInFlightStatsViewModel.setStatThree(competitionKeyStatisticsResource.getApplicationsPerAssessor());
                competitionInFlightStatsViewModel.setStatFour(competitionKeyStatisticsResource.getApplicationsStarted());
                competitionInFlightStatsViewModel.setStatFive(competitionKeyStatisticsResource.getApplicationsPastHalf());
                competitionInFlightStatsViewModel.setStatSix(competitionKeyStatisticsResource.getApplicationsSubmitted());
                break;
            case CLOSED:
                competitionInFlightStatsViewModel.setStatOne(competitionKeyStatisticsResource.getApplicationsRequiringAssessors());
                competitionInFlightStatsViewModel.setStatTwo(competitionKeyStatisticsResource.getAssignmentCount());
                competitionInFlightStatsViewModel.setStatThree(competitionKeyStatisticsResource.getAssessorsWithoutApplications());
                competitionInFlightStatsViewModel.setStatFour(competitionKeyStatisticsResource.getAssessorsInvited());
                competitionInFlightStatsViewModel.setStatFive(competitionKeyStatisticsResource.getAssessorsAccepted());
                competitionInFlightStatsViewModel.setStatSix(competitionKeyStatisticsResource.getApplicationsPerAssessor());
                break;
            case IN_ASSESSMENT:
                competitionInFlightStatsViewModel.setStatOne(competitionKeyStatisticsResource.getAssignmentCount());
                competitionInFlightStatsViewModel.setStatTwo(competitionKeyStatisticsResource.getAssignmentsWaiting());
                competitionInFlightStatsViewModel.setStatThree(competitionKeyStatisticsResource.getAssignmentsAccepted());
                competitionInFlightStatsViewModel.setStatFour(competitionKeyStatisticsResource.getAssessmentsStarted());
                competitionInFlightStatsViewModel.setStatFive(competitionKeyStatisticsResource.getAssessmentsSubmitted());
                break;
            case FUNDERS_PANEL:
                competitionInFlightStatsViewModel.setStatOne(-1L);
                competitionInFlightStatsViewModel.setStatTwo(-1L);
                competitionInFlightStatsViewModel.setStatThree(-1L);
                competitionInFlightStatsViewModel.setStatFour(-1L);
                competitionInFlightStatsViewModel.setStatFive(-1L);
                competitionInFlightStatsViewModel.setStatSix(-1L);
                break;
            case ASSESSOR_FEEDBACK:
                competitionInFlightStatsViewModel.setStatOne(-1L);
                competitionInFlightStatsViewModel.setStatTwo(-1L);
                competitionInFlightStatsViewModel.setStatThree(-1L);
                competitionInFlightStatsViewModel.setStatFour(-1L);
                competitionInFlightStatsViewModel.setStatFive(-1L);
                competitionInFlightStatsViewModel.setStatSix(-1L);
                break;
        }

        return competitionInFlightStatsViewModel;
    }
}
