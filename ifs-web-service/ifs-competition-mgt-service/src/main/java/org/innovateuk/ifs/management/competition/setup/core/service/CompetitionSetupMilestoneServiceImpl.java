package org.innovateuk.ifs.management.competition.setup.core.service;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.GenericMilestoneRowForm;
import org.innovateuk.ifs.management.competition.setup.core.form.MilestoneTime;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestonesForm;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.sort;

@Service
public class CompetitionSetupMilestoneServiceImpl implements CompetitionSetupMilestoneService {
    private static final Log LOG = LogFactory.getLog(CompetitionSetupMilestoneServiceImpl.class);

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Override
    public ServiceResult<List<MilestoneResource>> createMilestonesForIFSCompetition(Long competitionId) {
        List<MilestoneResource> newMilestones = new ArrayList<>();
        Stream.of(MilestoneType.presetValues()).filter(milestoneType -> !milestoneType.isOnlyNonIfs()).forEach(type ->
            newMilestones.add(milestoneRestService.create(type, competitionId).getSuccess())
        );
        return serviceSuccess(newMilestones);
    }

    @Override
    public ServiceResult<Void> updateMilestonesForCompetition(List<MilestoneResource> milestones, Map<String, GenericMilestoneRowForm> milestoneEntries, Long competitionId) {
        List<MilestoneResource> updatedMilestones = new ArrayList<>();

        milestones.forEach(milestoneResource -> {
            GenericMilestoneRowForm milestoneWithUpdate = milestoneEntries.getOrDefault(milestoneResource.getType().name(), null);

            if(milestoneWithUpdate != null) {
                ZonedDateTime temp = milestoneWithUpdate.getMilestoneAsZonedDateTime();
                if (temp != null) {
                    milestoneResource.setDate(temp);
                    updatedMilestones.add(milestoneResource);
                } else {
                    milestoneRestService
                            .resetMilestone(milestoneResource)
                            .getSuccess();
                }
            }
        });

        return milestoneRestService.updateMilestones(updatedMilestones).toServiceResult();
    }

    @Override
    public List<Error> validateMilestoneDates(Map<String, GenericMilestoneRowForm> milestonesFormEntries) {
        List<Error> errors =  new ArrayList<>();
        milestonesFormEntries.values().forEach(milestone -> {

            Integer day = milestone.getDay();
            Integer month = milestone.getMonth();
            Integer year = milestone.getYear();
            String fieldName = "milestone-" + milestone.getMilestoneNameType().toUpperCase();
            String fieldValidationError = milestone.getMilestoneType().getMilestoneDescription();
            if(!validTimeOfMiddayMilestone(milestone)) {
                errors.add(fieldError(fieldName, "", "error.milestone.invalid", fieldValidationError));
            }

            boolean dateFieldsIncludeNull = (day == null || month == null || year == null);
            if((dateFieldsIncludeNull || !isMilestoneDateValid(day, month, year))) {
                errors.add(fieldError(fieldName, "", "error.milestone.invalid", fieldValidationError));
            }
        });
        return errors;
    }

    private boolean validTimeOfMiddayMilestone(GenericMilestoneRowForm milestone) {
        if(milestone.isMiddayTime()) {
           return MilestoneTime.TWELVE_PM.equals(milestone.getTime());
        }
        return true;
    }

    @Override
    public boolean isMilestoneDateValid(Integer day, Integer month, Integer year) {
        try{
            TimeZoneUtil.fromUkTimeZone(year, month, day);
            return year <= 9999;
        }
        catch(DateTimeException dte){
            LOG.trace("invalid milestone date", dte);
            return false;
        }
    }

    public void sortMilestones(MilestonesForm milestoneForm) {
        LinkedMap<String, GenericMilestoneRowForm> milestoneEntries = milestoneForm.getMilestoneEntries();
        milestoneForm.setMilestoneEntries(sortMilestoneEntries(milestoneEntries.values()));
    }

    private LinkedMap<String, GenericMilestoneRowForm> sortMilestoneEntries(Collection<GenericMilestoneRowForm> milestones) {
        List<GenericMilestoneRowForm> sortedMilestones =
                sort(milestones,
                     Comparator.comparingInt(o -> o.getMilestoneType().ordinal()));

        LinkedMap<String, GenericMilestoneRowForm> milestoneFormEntries = new LinkedMap<>();
        sortedMilestones.stream().forEachOrdered(milestone ->
                milestoneFormEntries.put(milestone.getMilestoneType().name(), milestone)
        );

        return milestoneFormEntries;
    }
}
