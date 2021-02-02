package org.innovateuk.ifs.management.assessmentperiod.service;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;
import org.innovateuk.ifs.management.assessmentperiod.form.ManageAssessmentPeriodsForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssessmentPeriodServiceImpl implements AssessmentPeriodService {

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Override
    public List<AssessmentPeriodForm> getAssessmentPeriodMilestonesForms(long competitionId) {
        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess();
        Map<Long, List<MilestoneResource>> existingAssessmentPeriods = milestones
                .stream()
                .filter(milestone -> milestone.getAssessmentPeriodId() != null)
                .collect(Collectors.groupingBy(MilestoneResource::getAssessmentPeriodId));

        List<AssessmentPeriodForm> milestonesForms = assembleAssessmentPeriodForms(existingAssessmentPeriods);
        return milestonesForms;
    }

    private List<AssessmentPeriodForm> assembleAssessmentPeriodForms(Map<Long, List<MilestoneResource>> existingAssessmentPeriods) {
        List<AssessmentPeriodForm> milestonesForms = new ArrayList<>();
        existingAssessmentPeriods.forEach((key, value) -> {
            LinkedMap<String, MilestoneRowForm> milestoneFormEntries = new LinkedMap<>();
            value.forEach(milestone ->
                    milestoneFormEntries.put(milestone.getType().getMilestoneDescription(), populateMilestoneFormEntries(milestone))
            );
            AssessmentPeriodForm milestonesForm = new AssessmentPeriodForm();
            milestonesForm.setAssessmentPeriodId(key);
            milestonesForm.setMilestoneEntries(milestoneFormEntries);
            milestonesForms.add(milestonesForm);
        });
        return milestonesForms;
    }

    private MilestoneRowForm populateMilestoneFormEntries(MilestoneResource milestone) {
        return new MilestoneRowForm(milestone.getType(), milestone.getDate(), isEditable(milestone));
    }

    private boolean isEditable(MilestoneResource milestone) {
        return milestone.getDate() == null || milestone.getDate().isAfter(ZonedDateTime.now());
    }

    @Override
    public List<MilestoneResource> extractMilestoneResourcesFromForm(ManageAssessmentPeriodsForm form, long competitionId) {
        List<MilestoneResource> existingMilestones = getExistingAssessmentPeriodMilestoneResources(competitionId);

        Map<Long, Collection<MilestoneRowForm>> formMilestones = new HashMap<>();
        form.getFormList().forEach(assessmentPeriodForm -> {
            Long assessmentPeriodId = assessmentPeriodForm.getAssessmentPeriodId();
            formMilestones.put(assessmentPeriodId, assessmentPeriodForm.getMilestoneEntries().values());
        });

        List<MilestoneResource> updatedMilestones = new ArrayList<>();
        existingMilestones.stream()
                .filter(this::isEditable)
                .forEach(existingMilestone -> {

                    MilestoneRowForm milestoneWithUpdate = formMilestones.get(existingMilestone.getAssessmentPeriodId())
                            .stream()
                            .filter(e -> e.getMilestoneType().equals(existingMilestone.getType()))
                            .findFirst()
                            .orElse(null);

                    if (milestoneWithUpdate != null) {
                        ZonedDateTime temp = milestoneWithUpdate.getMilestoneAsZonedDateTime();
                        if (temp != null) {
                            existingMilestone.setDate(temp);
                            updatedMilestones.add(existingMilestone);
                        } else {
                            milestoneRestService
                                    .resetMilestone(existingMilestone)
                                    .getSuccess();
                        }
                    }
                });
        return updatedMilestones;
    }

    private List<MilestoneResource> getExistingAssessmentPeriodMilestoneResources(long competitionId) {
        return milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess()
                .stream()
                .filter(e -> MilestoneType.assessmentPeriodValues().contains(e.getType()))
                .collect(Collectors.toList());
    }

}
