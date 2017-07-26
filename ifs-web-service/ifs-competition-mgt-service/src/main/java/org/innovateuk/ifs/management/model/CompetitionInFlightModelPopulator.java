package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightViewModel;
import org.innovateuk.ifs.management.viewmodel.MilestonesRowViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
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
    private CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator;

    @Autowired
    private MilestoneRestService milestoneRestService;

    public CompetitionInFlightViewModel populateModel(Long competitionId, UserResource user) {
        return populateModel(competitionService.getById(competitionId), user);
    }

    public CompetitionInFlightViewModel populateModel(CompetitionResource competition, UserResource user) {
        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competition.getId()).getSuccessObjectOrThrowException();
        CompetitionInFlightStatsViewModel statsViewModel = competitionInFlightStatsModelPopulator.populateStatsViewModel(competition);

        long changesSinceLastNotify = assessmentRestService.countByStateAndCompetition(AssessmentStates.CREATED, competition.getId()).getSuccessObjectOrThrowException();
        milestones.sort(Comparator.comparing(MilestoneResource::getType));
        return new CompetitionInFlightViewModel(competition,
                simpleMap(milestones, MilestonesRowViewModel::new),
                changesSinceLastNotify, statsViewModel, user.hasRole(UserRoleType.SUPPORT) || user.hasRole(UserRoleType.INNOVATION_LEAD));
    }
}
