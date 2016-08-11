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
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class CompetitionSetupMilestoneServiceImpl implements CompetitionSetupMilestoneService {

    @Autowired
    MilestoneService milestoneService;

    @Override
    public List<MilestoneResource> createMilestonesForCompetition(Long competitionId) {
        List<MilestoneResource> newMilestones = new ArrayList<>();
        Stream.of(MilestoneResource.MilestoneName.values()).forEach(name -> {
            MilestoneResource newMilestone = milestoneService.create(name, competitionId);
            newMilestone.setName(name);
            newMilestones.add(newMilestone);
        });
        return newMilestones;
    }

    @Override
    public List<Error> updateMilestonesForCompetition(List<MilestoneResource> milestones, List<MilestonesFormEntry> milestoneEntries, Long competitionId) {

        List<MilestoneResource> updatedMilestones = new ArrayList();

        milestones.forEach(milestoneResource -> {
            Optional<MilestonesFormEntry> milestoneWithUpdate = milestoneEntries.stream()
                    .filter(milestonesFormEntry -> milestonesFormEntry.getMilestoneName().equals(milestoneResource.getName())).findAny();

            if(milestoneWithUpdate.isPresent()) {
                LocalDateTime temp = getMilestoneDate(milestoneWithUpdate.get().getDay(), milestoneWithUpdate.get().getMonth(), milestoneWithUpdate.get().getYear());
                if (temp != null) {
                    milestoneResource.setDate(temp);
                    updatedMilestones.add(milestoneResource);
                }
            }

        });


        return milestoneService.update(updatedMilestones, competitionId);
    }

    private LocalDateTime getMilestoneDate(Integer day, Integer month, Integer year){
        if (day != null && month != null && year != null){
            return LocalDateTime.of(year, month, day, 0, 0);
        } else {
            return null;
        }
    }

    @Override
    public List<Error> validateMilestoneDates(List<MilestonesFormEntry> milestonesFormEntries) {
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
