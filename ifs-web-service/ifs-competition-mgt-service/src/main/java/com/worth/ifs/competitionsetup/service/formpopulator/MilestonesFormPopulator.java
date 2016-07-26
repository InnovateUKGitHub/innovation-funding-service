package com.worth.ifs.competitionsetup.service.formpopulator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.worth.ifs.competitionsetup.form.MilestonesFormEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneResource.MilestoneName;
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

        List<MilestoneResource> allDatesByCompetitionId = milestoneService.getAllDatesByCompetitionId(competitionResource.getId());

        List<MilestonesFormEntry> milestoneFormEntries = new ArrayList<>();

        allDatesByCompetitionId.forEach(milestone -> {
            milestoneFormEntries.add(addMilestone(milestone));
        });
        competitionSetupForm.setMilestonesFormEntryList(milestoneFormEntries);
        return competitionSetupForm;
    }

    private MilestonesFormEntry addMilestone(MilestoneResource milestone) {
        MilestonesFormEntry newMilestone = new MilestonesFormEntry();
        newMilestone.setMilestoneName(milestone.getName());
        newMilestone.setDay(milestone.getDate().getDayOfMonth());
        newMilestone.setMonth(milestone.getDate().getMonthValue());
        newMilestone.setYear(milestone.getDate().getYear());
        newMilestone.setDayOfWeek(getNameOfDay(LocalDateTime.of(newMilestone.getYear(),
                newMilestone.getMonth(), newMilestone.getDay(), 0, 0)));
        return newMilestone;
    }

    /*
	 * Returns the first free letters of the name of the weekday
	 */
    private String getNameOfDay(LocalDateTime localDateTime) {
        String shortDayName = "-";
        try {
            String dayOfWeek = localDateTime.getDayOfWeek().name();
            shortDayName = dayOfWeek.substring(0, 1) + dayOfWeek.substring(1, 3).toLowerCase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return shortDayName;
    }
}

