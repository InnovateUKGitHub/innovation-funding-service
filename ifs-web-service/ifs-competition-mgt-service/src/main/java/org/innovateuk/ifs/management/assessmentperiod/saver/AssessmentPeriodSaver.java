package org.innovateuk.ifs.management.assessmentperiod.saver;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;
import org.innovateuk.ifs.management.assessmentperiod.form.ManageAssessmentPeriodsForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.competition.resource.MilestoneType.assessmentPeriodValues;

@Component
public class AssessmentPeriodSaver {

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Autowired
    private AssessmentPeriodRestService assessmentPeriodRestService;

    public ServiceResult<Void> save(long competitionId, ManageAssessmentPeriodsForm form) {
        List<MilestoneResource> existingMilestones = getExistingAssessmentPeriodMilestoneResources(competitionId);

        return aggregate(IntStream.range(0, form.getAssessmentPeriods().size()).mapToObj((index) -> {
            AssessmentPeriodForm assessmentPeriodForm = form.getAssessmentPeriods().get(index);
            Long assessmentPeriodId = assessmentPeriodForm.getAssessmentPeriodId();
            return validate(index, assessmentPeriodForm, existingMilestones)
                    .andOnSuccess(() -> createAssessmentPeriodIfRequired(assessmentPeriodForm, competitionId))
                    .andOnSuccessReturn(() ->
                            aggregate(assessmentPeriodForm.getMilestoneEntries().values().stream()
                                    .map(milestoneRowForm -> {
                                                Optional<MilestoneResource> matchingMilestoneResource = findMatchingResource(existingMilestones, milestoneRowForm, assessmentPeriodId);
                                                ZonedDateTime date = milestoneRowForm.getMilestoneAsZonedDateTime();
                                                if (matchingMilestoneResource.isPresent()) {
                                                    if (isEditable(matchingMilestoneResource.get())) {
                                                        matchingMilestoneResource.get().setDate(date);
                                                        return milestoneRestService.updateMilestone(matchingMilestoneResource.get()).toServiceResult();
                                                    }
                                                    return serviceSuccess();
                                                } else {
                                                    MilestoneResource milestone = new MilestoneResource();
                                                    milestone.setDate(date);
                                                    milestone.setType(milestoneRowForm.getMilestoneType());
                                                    milestone.setCompetitionId(competitionId);
                                                    milestone.setAssessmentPeriodId(assessmentPeriodForm.getAssessmentPeriodId());
                                                    return milestoneRestService.create(milestone).toServiceResult().andOnSuccessReturnVoid();
                                                }
                                    })
                                    .collect(Collectors.toList())).andOnSuccessReturnVoid()
            );
        }).collect(Collectors.toList()))
                .andOnSuccessReturnVoid();
    }

    private boolean isEditable(MilestoneResource milestone) {
        return milestone.getDate() == null || milestone.getDate().isAfter(ZonedDateTime.now());
    }

    private List<MilestoneResource> getExistingAssessmentPeriodMilestoneResources(long competitionId) {
        return milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess()
                .stream()
                .filter(e -> MilestoneType.assessmentPeriodValues().contains(e.getType()))
                .collect(Collectors.toList());
    }

    private ServiceResult<AssessmentPeriodForm> createAssessmentPeriodIfRequired(AssessmentPeriodForm assessmentPeriodForm, long competitionId){
        if (assessmentPeriodForm.getAssessmentPeriodId() == null){
            AssessmentPeriodResource period = new AssessmentPeriodResource();
            period.setCompetitionId(competitionId);
            AssessmentPeriodResource savedPeriod = assessmentPeriodRestService.create(period).getSuccess();
            assessmentPeriodForm.setAssessmentPeriodId(savedPeriod.getId());
        }
        return ServiceResult.serviceSuccess(assessmentPeriodForm);
    }

    private Optional<MilestoneResource> findMatchingResource(List<MilestoneResource> existingMilestones, MilestoneRowForm milestoneRowForm, Long assessmentPeriodId) {
        return existingMilestones.stream()
                .filter(m ->
                        m.getAssessmentPeriodId().equals(assessmentPeriodId)
                                && m.getType() == milestoneRowForm.getMilestoneType())
                .findAny();
    }

    private ServiceResult<Void> validate(int index, AssessmentPeriodForm period, List<MilestoneResource> existingMilestones) {
        List<Error> errors = new ArrayList<>();
        for (MilestoneRowForm milestone : period.getMilestoneEntries().values()) {
            Integer day = milestone.getDay();
            Integer month = milestone.getMonth();
            Integer year = milestone.getYear();
            boolean dateFieldsIncludeNull = (day == null || month == null || year == null);
            if ((dateFieldsIncludeNull || !isMilestoneDateValid(day, month, year))) {
                String fieldName = String.format("assessmentPeriods[%d].milestoneEntries[%s]", index, milestone.getMilestoneType());
                String fieldValidationError = milestone.getMilestoneType().getAlwaysOpenDescription();
                errors.add(fieldError(fieldName, "", "error.assessment-period.invalid", fieldValidationError));
            }
        }
        if (errors.isEmpty()) {
            List<MilestoneRowForm> milestones = period.getMilestoneEntries().values()
                    .stream()
                    .sorted(Comparator.comparing(m -> m.getMilestoneType().getPriority()))
                    .collect(Collectors.toList());
            for (int i = 0; i < milestones.size(); i++) {
                MilestoneRowForm current = milestones.get(i);
                if (i == 0) {
                    Optional<MilestoneResource> matchingMilestoneResource = findMatchingResource(existingMilestones, current, period.getAssessmentPeriodId());
                    if (!matchingMilestoneResource.isPresent() || isEditable(matchingMilestoneResource.get())) {
                        if (ZonedDateTime.now().isAfter(current.getMilestoneAsZonedDateTime())) {
                            String fieldName = String.format("assessmentPeriods[%d].milestoneEntries[%s]", index, current.getMilestoneType());
                            String fieldValidationError = current.getMilestoneType().getAlwaysOpenDescription();
                            errors.add(fieldError(fieldName, "", "error.assessment-period.in-past", fieldValidationError));
                        }
                    }
                } else {
                    MilestoneRowForm previous = milestones.get(i - 1);
                    if (previous.getMilestoneAsZonedDateTime().isAfter(current.getMilestoneAsZonedDateTime())) {
                        String fieldName = String.format("assessmentPeriods[%d].milestoneEntries[%s]", index, current.getMilestoneType());
                        String fieldValidationError = current.getMilestoneType().getAlwaysOpenDescription();
                        errors.add(fieldError(fieldName, "", "error.assessment-period.non-in-order", fieldValidationError));
                    }
                }
            }
        }
        if (errors.isEmpty()) {
            return serviceSuccess();
        } else {
            return serviceFailure(errors);
        }
    }

    private boolean isMilestoneDateValid(Integer day, Integer month, Integer year) {
        try{
            TimeZoneUtil.fromUkTimeZone(year, month, day);
            return year <= 9999;
        }
        catch(DateTimeException dte){
            return false;
        }
    }
}
