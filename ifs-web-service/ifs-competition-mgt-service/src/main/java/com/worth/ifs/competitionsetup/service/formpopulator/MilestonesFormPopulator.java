package com.worth.ifs.competitionsetup.service.formpopulator;

import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.MilestonesForm;
import com.worth.ifs.competitionsetup.form.MilestonesFormEntry;
import com.worth.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

        List<MilestoneResource> milestonesByCompetition = milestoneService.getAllDatesByCompetitionId(competitionResource.getId());
        if (milestonesByCompetition.isEmpty()) {
            milestonesByCompetition.addAll(competitionSetupMilestoneService.createMilestonesForCompetition(competitionResource.getId()));
        } else {
            milestonesByCompetition.sort((c1, c2) -> c1.getType().compareTo(c2.getType()));
        }

        List<MilestonesFormEntry> milestoneFormEntries = new ArrayList<>();
        milestonesByCompetition.forEach(milestone -> {
            milestoneFormEntries.add(populateMilestoneFormEntries(milestone));
        });
        competitionSetupForm.setMilestonesFormEntryList(milestoneFormEntries);

        return competitionSetupForm;
    }

    private MilestonesFormEntry populateMilestoneFormEntries(MilestoneResource milestone) {
        MilestonesFormEntry newMilestone = new MilestonesFormEntry();
        newMilestone.setMilestoneType(milestone.getType());
        if (milestone.getDate() != null) {
            newMilestone.setDay(milestone.getDate().getDayOfMonth());
            newMilestone.setMonth(milestone.getDate().getMonthValue());
            newMilestone.setYear(milestone.getDate().getYear());
            newMilestone.setDayOfWeek(newMilestone.getDayOfWeek());
        }
        return newMilestone;
    }
}


