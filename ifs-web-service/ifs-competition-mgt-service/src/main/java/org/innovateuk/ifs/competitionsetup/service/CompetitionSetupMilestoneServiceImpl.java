package org.innovateuk.ifs.competitionsetup.service;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.competitionsetup.form.MilestoneRowForm;
import org.innovateuk.ifs.competitionsetup.form.MilestonesForm;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.innovateuk.ifs.competitionsetup.form.MilestoneTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class CompetitionSetupMilestoneServiceImpl implements CompetitionSetupMilestoneService {

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Override
    public ServiceResult<List<MilestoneResource>> createMilestonesForCompetition(Long competitionId) {
        List<MilestoneResource> newMilestones = new ArrayList<>();
        Stream.of(MilestoneType.presetValues()).forEach(type ->
            newMilestones.add(milestoneRestService.create(type, competitionId).getSuccessObjectOrThrowException())
        );
        return serviceSuccess(newMilestones);
    }

    @Override
    public ServiceResult<Void> updateMilestonesForCompetition(List<MilestoneResource> milestones, Map<String, MilestoneRowForm> milestoneEntries, Long competitionId) {
        List<MilestoneResource> updatedMilestones = new ArrayList<>();

        milestones.forEach(milestoneResource -> {
            MilestoneRowForm milestoneWithUpdate = milestoneEntries.getOrDefault(milestoneResource.getType().name(), null);

            if(milestoneWithUpdate != null) {
                ZonedDateTime temp = milestoneWithUpdate.getMilestoneAsZonedDateTime();
                if (temp != null) {
                    milestoneResource.setDate(temp);
                    updatedMilestones.add(milestoneResource);
                }
            }
        });

        return milestoneRestService.updateMilestones(updatedMilestones).toServiceResult();
    }

    @Override
    public List<Error> validateMilestoneDates(Map<String, MilestoneRowForm> milestonesFormEntries) {
        List<Error> errors =  new ArrayList<>();
        milestonesFormEntries.values().forEach(milestone -> {

            Integer day = milestone.getDay();
            Integer month = milestone.getMonth();
            Integer year = milestone.getYear();

            if(!validTimeOfMiddayMilestone(milestone)) {
                if(errors.isEmpty()) {
                    errors.add(new Error("error.milestone.invalid", HttpStatus.BAD_REQUEST));
                }
            }

            if(day == null || month == null || year == null || !isMilestoneDateValid(day, month, year)) {
                if(errors.isEmpty()) {
                    errors.add(new Error("error.milestone.invalid", HttpStatus.BAD_REQUEST));
                }
            }
        });
        return errors;
    }

    private boolean validTimeOfMiddayMilestone(MilestoneRowForm milestone) {
        if(milestone.isMiddayTime()) {
           return MilestoneTime.TWELVE_PM.equals(milestone.getTime());
        }
        return true;
    }

    @Override
    public Boolean isMilestoneDateValid(Integer day, Integer month, Integer year) {
        try{
            TimeZoneUtil.fromUkTimeZone(year, month, day);
            return year <= 9999;
        }
        catch(DateTimeException dte){
            return false;
        }
    }

    public void sortMilestones(MilestonesForm milestoneForm) {
        LinkedMap<String, MilestoneRowForm> milestoneEntries = milestoneForm.getMilestoneEntries();
        milestoneForm.setMilestoneEntries(sortMilestoneEntries(milestoneEntries.values()));
    }

    private LinkedMap<String, MilestoneRowForm> sortMilestoneEntries(Collection<MilestoneRowForm> milestones) {
        List<MilestoneRowForm> sortedMilestones = milestones.stream()
                .sorted((o1, o2) -> o1.getMilestoneType().ordinal() - o2.getMilestoneType().ordinal())
                .collect(Collectors.toList());

        LinkedMap<String, MilestoneRowForm> milestoneFormEntries = new LinkedMap<>();
        sortedMilestones.stream().forEachOrdered(milestone ->
                milestoneFormEntries.put(milestone.getMilestoneType().name(), milestone)
        );

        return milestoneFormEntries;
    }
}
