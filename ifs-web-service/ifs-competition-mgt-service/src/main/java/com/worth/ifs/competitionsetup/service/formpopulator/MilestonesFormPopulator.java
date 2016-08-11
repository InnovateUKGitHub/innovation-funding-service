package com.worth.ifs.competitionsetup.service.formpopulator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.competitionsetup.form.MilestonesFormEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.MilestonesForm;

/**
 * Form populator for the milestones competition setup section.
 */
@Service
public class MilestonesFormPopulator implements CompetitionSetupFormPopulator {

    @Autowired
    private MilestoneService milestoneService;

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.MILESTONES;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        MilestonesForm competitionSetupForm = new MilestonesForm();

        List<MilestoneResource> allMilestonesByCompetitionId = milestoneService.getAllDatesByCompetitionId(competitionResource.getId());

        if (allMilestonesByCompetitionId == null || allMilestonesByCompetitionId.isEmpty()) {
            allMilestonesByCompetitionId.addAll(createMilestonesForCompetition(competitionResource));
        }
        else {
            allMilestonesByCompetitionId.sort((c1, c2) -> c1.getType().compareTo(c2.getType()));
        }

        List<MilestonesFormEntry> milestoneFormEntries = new ArrayList<>();

        allMilestonesByCompetitionId.forEach(milestone -> {
            milestoneFormEntries.add(populateMilestoneFormEntries(milestone));

        });
        competitionSetupForm.setMilestonesFormEntryList(milestoneFormEntries);
        return competitionSetupForm;
    }

    private List<MilestoneResource> createMilestonesForCompetition(CompetitionResource competitionResource) {
        List<MilestoneResource> newMilestones = new ArrayList<>();
        Stream.of(MilestoneType.values()).forEach(type -> {
            MilestoneResource newMilestone = milestoneService.create(type, competitionResource.getId());
            newMilestone.setType(type);
            newMilestones.add(newMilestone);
        });
        return newMilestones;
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


