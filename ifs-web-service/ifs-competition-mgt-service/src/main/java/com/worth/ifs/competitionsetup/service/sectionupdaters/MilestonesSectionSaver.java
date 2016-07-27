package com.worth.ifs.competitionsetup.service.sectionupdaters;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.worth.ifs.competitionsetup.form.MilestonesFormEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneResource.MilestoneName;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.MilestonesForm;

/**
 * Competition setup section saver for the milestones section.
 */
@Service
public class MilestonesSectionSaver implements CompetitionSetupSectionSaver {

    @Autowired
    private MilestoneService milestoneService;

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.MILESTONES;
	}

	@Override
	public List<Error> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {

        MilestonesForm milestonesForm = (MilestonesForm) competitionSetupForm;
        List<MilestonesFormEntry> milestonesFormEntryList = milestonesForm.getMilestonesFormEntryList();
        List<MilestoneResource> milestoneResource = milestoneService.getAllDatesByCompetitionId(competition.getId());

        if (milestoneResource == null || milestoneResource.isEmpty()) {
            milestoneResource.addAll(createMilestonesForCompetition());
        }

        List<MilestoneResource> updateMilestoneList = new ArrayList<>();

        for (int i = 0; i < milestoneResource.size(); i++) {
            milestoneResource.get(i).setCompetition(competition.getId());
            LocalDateTime temp = populateDate(milestonesFormEntryList.get(i).getDay(), milestonesFormEntryList.get(i).getMonth(), milestonesFormEntryList.get(i).getYear());
            milestoneResource.get(i).setDate(temp);

            MilestonesFormEntry thisMilestonesFormEntry = milestonesFormEntryList.get(i);
            thisMilestonesFormEntry.setDayOfWeek(getNameOfDay(temp));
            thisMilestonesFormEntry.setMilestoneName(milestoneResource.get(i).getName());
        }
        return milestoneService.update(milestoneResource, competition.getId());
    }

    private LocalDateTime populateDate(Integer day, Integer month, Integer year){
        return LocalDateTime.of(year, month, day, 0, 0);
    }

    private List<MilestoneResource> createMilestonesForCompetition() {
        List<MilestoneResource> newMilestones = new ArrayList<>();
        Stream.of(MilestoneName.values()).forEach(name -> {
            MilestoneResource newMilestone = milestoneService.create();
            newMilestone.setName(name);
            newMilestones.add(newMilestone);
        });
        return newMilestones;
    }

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return MilestonesForm.class.equals(clazz);
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
