package com.worth.ifs.competitionsetup.service.sectionupdaters;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.worth.ifs.competitionsetup.form.MilestonesFormEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        List<Error> errors = validateMilestoneDates(milestonesFormEntryList);

        for (int i = 0; i < milestoneResource.size(); i++) {
            MilestonesFormEntry thisMilestonesFormEntry = milestonesFormEntryList.get(i);
            thisMilestonesFormEntry.setMilestoneName(milestoneResource.get(i).getName());

            milestoneResource.get(i).setCompetition(competition.getId());
            LocalDateTime temp = populateDate(milestonesFormEntryList.get(i).getDay(), milestonesFormEntryList.get(i).getMonth(), milestonesFormEntryList.get(i).getYear());
            if (temp != null) {
                milestoneResource.get(i).setDate(temp);
            }
        }
        if (errors.size() > 0){
            return errors;
        }
        else {
            return milestoneService.update(milestoneResource, competition.getId());
        }
    }

    private LocalDateTime populateDate(Integer day, Integer month, Integer year){
        if (day != null && month != null && year != null){
            return LocalDateTime.of(year, month, day, 0, 0);
        } else {
            return null;
        }
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

    private List<Error> validateMilestoneDates(List<MilestonesFormEntry> milestonesFormEntries) {
        List<Error> errors =  new ArrayList<>();
        milestonesFormEntries.forEach(milestone -> {

                Integer day = milestone.getDay();
                Integer month = milestone.getMonth();
                Integer year = milestone.getYear();

                if ((day == null || 1 > day || day > 31)
                        || (month == null || month < 1 || month > 12) || (year == null || year < 1900)){
                    if(errors.size() == 0) {
                        errors.add(new Error("error.milestone.invalid", "Please enter the valid date", HttpStatus.BAD_REQUEST));
                    }
                }
            });
        return errors;
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return MilestonesForm.class.equals(clazz);
    }

}
