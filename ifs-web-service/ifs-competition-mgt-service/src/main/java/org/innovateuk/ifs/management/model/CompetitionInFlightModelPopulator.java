package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.service.MilestoneService;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
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
    private MilestoneService milestoneService;

    public CompetitionInFlightViewModel populateModel(CompetitionResource competition) {
        List<MilestoneResource> milestones = milestoneService.getAllMilestonesByCompetitionId(competition.getId());

        long changesSinceLastNotify = assessmentRestService.countByStateAndCompetition(AssessmentStates.CREATED, competition.getId()).getSuccessObject();
        milestones.sort(Comparator.comparing(MilestoneResource::getType));
        return new CompetitionInFlightViewModel(competition,
                simpleMap(milestones, MilestonesRowViewModel::new),
                changesSinceLastNotify);
    }
}
