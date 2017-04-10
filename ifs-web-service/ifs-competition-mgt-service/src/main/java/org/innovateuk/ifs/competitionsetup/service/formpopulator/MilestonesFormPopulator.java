package org.innovateuk.ifs.competitionsetup.service.formpopulator;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.application.service.MilestoneService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.MilestoneRowForm;
import org.innovateuk.ifs.competitionsetup.form.MilestonesForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Form populator for the milestones competition setup section.
 */
@Service
public class MilestonesFormPopulator implements CompetitionSetupFormPopulator {

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.MILESTONES;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        MilestonesForm competitionSetupForm = new MilestonesForm();

        List<MilestoneResource> milestonesByCompetition = milestoneService.getAllMilestonesByCompetitionId(competitionResource.getId());
        if (milestonesByCompetition.isEmpty()) {
            milestonesByCompetition.addAll(competitionSetupMilestoneService.createMilestonesForCompetition(competitionResource.getId()).getSuccessObjectOrThrowException());
        } else {
            milestonesByCompetition.sort(Comparator.comparing(MilestoneResource::getType));
        }

        LinkedMap<String, MilestoneRowForm> milestoneFormEntries = new LinkedMap<>();
        milestonesByCompetition.stream().forEachOrdered(milestone -> {
            milestoneFormEntries.put(milestone.getType().name(), populateMilestoneFormEntries(milestone));
        });


        competitionSetupForm.setMilestoneEntries(milestoneFormEntries);

        return competitionSetupForm;
    }

    private MilestoneRowForm populateMilestoneFormEntries(MilestoneResource milestone) {
        return new MilestoneRowForm(milestone.getType(), milestone.getDate());
    }
}


