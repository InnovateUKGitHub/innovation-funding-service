package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.competitionsetup.form.MilestonesForm;
import com.worth.ifs.competitionsetup.viewmodel.MilestoneViewModel;
import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CompetitionSetupMilestoneServiceImpl implements CompetitionSetupMilestoneService {

    @Autowired
    MilestoneService milestoneService;

    @Override
    public List<MilestoneResource> createMilestonesForCompetition(Long competitionId) {
        List<MilestoneResource> newMilestones = new ArrayList<>();
        Stream.of(MilestoneType.values()).forEach(type -> {
            MilestoneResource newMilestone = milestoneService.create(type, competitionId);
            newMilestone.setType(type);
            newMilestones.add(newMilestone);
        });
        return newMilestones;
    }

    @Override
    public List<Error> updateMilestonesForCompetition(List<MilestoneResource> milestones, LinkedMap<String, MilestoneViewModel> milestoneEntries, Long competitionId) {
        List<MilestoneResource> updatedMilestones = new ArrayList();

        milestones.forEach(milestoneResource -> {
            MilestoneViewModel milestoneWithUpdate = milestoneEntries.getOrDefault(milestoneResource.getType().name(), null);

            if(milestoneWithUpdate != null) {
                LocalDateTime temp = getMilestoneDate(milestoneWithUpdate.getDay(), milestoneWithUpdate.getMonth(), milestoneWithUpdate.getYear());
                if (temp != null) {
                    milestoneResource.setDate(temp);
                    updatedMilestones.add(milestoneResource);
                }
            }
        });

        return milestoneService.updateMilestones(updatedMilestones, competitionId);
    }

    private LocalDateTime getMilestoneDate(Integer day, Integer month, Integer year){
        if (day != null && month != null && year != null){
            return LocalDateTime.of(year, month, day, 0, 0);
        } else {
            return null;
        }
    }

    @Override
    public List<Error> validateMilestoneDates(LinkedMap<String, MilestoneViewModel> milestonesFormEntries) {
        List<Error> errors =  new ArrayList<>();
        milestonesFormEntries.values().forEach(milestone -> {

            Integer day = milestone.getDay();
            Integer month = milestone.getMonth();
            Integer year = milestone.getYear();

            if(day == null || month == null || year == null || !isMilestoneDateValid(day, month, year)) {
                if(errors.isEmpty()) {
                    errors.add(new Error("error.milestone.invalid", HttpStatus.BAD_REQUEST));
                }
            }
        });
        return errors;
    }

    @Override
    public Boolean isMilestoneDateValid(Integer day, Integer month, Integer year) {
        try{
            LocalDateTime.of(year, month, day, 0,0);
            if (year > 9999) {
                    return false;
            }
            return true;
        }
        catch(DateTimeException dte){
            return false;
        }
    }

    public void sortMilestones(MilestonesForm milestoneForm) {
        LinkedMap<String, MilestoneViewModel> milestoneEntries = milestoneForm.getMilestoneEntries();
        milestoneForm.setMilestoneEntries(sortMilestoneEntries(milestoneEntries.values()));
    }

    private LinkedMap<String, MilestoneViewModel> sortMilestoneEntries(Collection<MilestoneViewModel> milestones) {
        List<MilestoneViewModel> sortedMilestones = milestones.stream()
                .sorted((o1, o2) -> o1.getMilestoneType().ordinal() - o2.getMilestoneType().ordinal())
                .collect(Collectors.toList());

        LinkedMap<String, MilestoneViewModel> milestoneFormEntries = new LinkedMap<>();
        sortedMilestones.stream().forEachOrdered(milestone ->
                milestoneFormEntries.put(milestone.getMilestoneType().name(), milestone)
        );

        return milestoneFormEntries;
    }
}
