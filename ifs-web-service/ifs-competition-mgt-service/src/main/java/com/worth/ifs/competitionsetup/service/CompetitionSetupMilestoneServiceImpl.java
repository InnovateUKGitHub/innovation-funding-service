package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competitionsetup.form.MilestonesFormEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompetitionSetupMilestoneServiceImpl implements CompetitionSetupMilestoneService {

    @Autowired
    MilestoneService milestoneService;

    @Override
    public List<Error> updateMilestonesForCompetition(List<MilestoneResource> milestones, List<MilestonesFormEntry> milestoneEntries, Long competitionId) {

        List<Error> errors = validateMilestoneDates(milestoneEntries);
        if(!errors.isEmpty()) {
            return errors;
        }

        for (int i = 0; i < milestones.size(); i++) {
            MilestonesFormEntry thisMilestonesFormEntry = milestoneEntries.get(i);
            thisMilestonesFormEntry.setMilestoneType(milestones.get(i).getType());

            milestones.get(i).setCompetition(competitionId);
            LocalDateTime temp = getMilestoneDate(milestoneEntries.get(i).getDay(), milestoneEntries.get(i).getMonth(), milestoneEntries.get(i).getYear());
            if (temp != null) {
                milestones.get(i).setDate(temp);
            }
        }

        return milestoneService.update(milestones, competitionId);
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
            if(!isMilestoneDateValid(milestone.getDay(), milestone.getMonth(), milestone.getYear()));{
                if(errors.isEmpty()) {
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
}
