package org.innovateuk.ifs.management.competition.inflight.populator;

import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.inflight.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.competition.inflight.viewmodel.CompetitionInFlightViewModel;
import org.innovateuk.ifs.management.competition.inflight.viewmodel.MilestonesRowViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Competition Management Competition in flight dashboard.
 */
@Component
public class CompetitionInFlightModelPopulator {

    @Autowired
    private AssessmentRestService assessmentRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    @Autowired
    private CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator;

    @Autowired
    private MilestoneRestService milestoneRestService;

    public CompetitionInFlightViewModel populateModel(Long competitionId, UserResource user) {
        CompetitionResource competititon = competitionRestService.getCompetitionById(competitionId).getSuccess();
        return populateModel(competititon, user);
    }

    public CompetitionInFlightViewModel populateModel(CompetitionResource competition, UserResource user) {
        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competition.getId()).getSuccess();
        CompetitionInFlightStatsViewModel statsViewModel = competitionInFlightStatsModelPopulator.populateStatsViewModel(competition);
        CompetitionAssessmentConfigResource competitionAssessmentConfigResource = competitionAssessmentConfigRestService.findOneByCompetitionId(competition.getId()).getSuccess();

        long changesSinceLastNotify = assessmentRestService.countByStateAndCompetition(AssessmentState.CREATED, competition.getId()).getSuccess();
        milestones.sort(Comparator.comparing(MilestoneResource::getType));
        return new CompetitionInFlightViewModel(
                competition,
                competitionAssessmentConfigResource,
                simpleMap(milestones, MilestonesRowViewModel::new),
                changesSinceLastNotify,
                statsViewModel,
                user.hasRole(SUPPORT) || user.hasRole(INNOVATION_LEAD) || user.hasRole(STAKEHOLDER));
    }
}
