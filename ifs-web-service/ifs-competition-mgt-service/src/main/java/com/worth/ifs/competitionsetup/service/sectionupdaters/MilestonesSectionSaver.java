package com.worth.ifs.competitionsetup.service.sectionupdaters;

import java.time.DateTimeException;
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
        List<MilestonesFormEntry> milestoneEntries = milestonesForm.getMilestonesFormEntryList();
        List<MilestoneResource> milestones = milestoneService.getAllDatesByCompetitionId(competition.getId());
        milestones.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));

        List<Error> errors = validateMilestoneDates(milestoneEntries);
        return updateMilestonesForCompetition(milestones, milestoneEntries, competition, errors);
    }

    private List<Error> updateMilestonesForCompetition(List<MilestoneResource> milestones, List<MilestonesFormEntry> milestoneEntries, CompetitionResource competition, List<Error> errors) {

        for (int i = 0; i < milestones.size(); i++) {
            MilestonesFormEntry thisMilestonesFormEntry = milestoneEntries.get(i);
            thisMilestonesFormEntry.setMilestoneName(milestones.get(i).getName());

            milestones.get(i).setCompetition(competition.getId());
            LocalDateTime temp = getMilestoneDate(milestoneEntries.get(i).getDay(), milestoneEntries.get(i).getMonth(), milestoneEntries.get(i).getYear());
            if (temp != null) {
                milestones.get(i).setDate(temp);
            }
        }
        if (errors.size() > 0){
            return errors;
        }
        else {
            return milestoneService.update(milestones, competition.getId());
        }
    }

    private LocalDateTime getMilestoneDate(Integer day, Integer month, Integer year){
        if (day != null && month != null && year != null){
            return LocalDateTime.of(year, month, day, 0, 0);
        } else {
            return null;
        }
    }

    private List<Error> validateMilestoneDates(List<MilestonesFormEntry> milestonesFormEntries) {
        List<Error> errors =  new ArrayList<>();
        milestonesFormEntries.forEach(milestone -> {

                Integer day = milestone.getDay();
                Integer month = milestone.getMonth();
                Integer year = milestone.getYear();

                if ((day == null || 1 > day || day > 31)
                        || (month == null || month < 1 || month > 12) || (year == null || year < 1900)
                        || !isMilestoneDateValid(day, month, year)){
                        if(errors.size() == 0) {
                            errors.add(new Error("error.milestone.invalid", "Please enter the valid date(s)", HttpStatus.BAD_REQUEST));
                        }}
            });
        return errors;
    }

    private Boolean isMilestoneDateValid(Integer day, Integer month, Integer year) {
        try{
            LocalDateTime.of(year, month, day, 0,0);
            return true;
        }
        catch(DateTimeException dte){
            return false;
        }
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) { return MilestonesForm.class.equals(clazz); }
}
