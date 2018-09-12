package org.innovateuk.ifs.competitionsetup.milestone.populator;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.form.GenericMilestoneRowForm;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.competitionsetup.milestone.form.MilestoneRowForm;
import org.innovateuk.ifs.competitionsetup.milestone.form.MilestonesForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Form populator for the milestones competition setup section.
 */
@Service
public class MilestonesFormPopulator implements CompetitionSetupFormPopulator {

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Autowired
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.MILESTONES;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        MilestonesForm competitionSetupForm = new MilestonesForm();

        List<MilestoneResource> milestonesByCompetition = milestoneRestService.getAllMilestonesByCompetitionId(competitionResource.getId()).getSuccess();
        if (milestonesByCompetition.isEmpty()) {
            milestonesByCompetition.addAll(competitionSetupMilestoneService.createMilestonesForIFSCompetition(competitionResource.getId()).getSuccess());
        } else {
            milestonesByCompetition.sort(Comparator.comparing(MilestoneResource::getType));
        }

        LinkedMap<String, GenericMilestoneRowForm> milestoneFormEntries = new LinkedMap<>();
        milestonesByCompetition.stream().forEachOrdered(milestone ->
            milestoneFormEntries.put(milestone.getType().name(), populateMilestoneFormEntries(milestone, competitionResource))
        );


        competitionSetupForm.setMilestoneEntries(milestoneFormEntries);

        return competitionSetupForm;
    }

    private MilestoneRowForm populateMilestoneFormEntries(MilestoneResource milestone, CompetitionResource competitionResource) {
        return new MilestoneRowForm(milestone.getType(), milestone.getDate(), isEditable(milestone, competitionResource));
    }

    private boolean isEditable(MilestoneResource milestone, CompetitionResource competitionResource) {
        return !competitionResource.isSetupAndLive() || milestone.getDate().isAfter(ZonedDateTime.now());
    }
}


