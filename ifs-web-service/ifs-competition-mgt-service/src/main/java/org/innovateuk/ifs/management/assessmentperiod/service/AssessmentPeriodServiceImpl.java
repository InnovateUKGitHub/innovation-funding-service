package org.innovateuk.ifs.management.assessmentperiod.service;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;
import org.innovateuk.ifs.management.assessmentperiod.form.ManageAssessmentPeriodsForm;
import org.innovateuk.ifs.management.competition.setup.core.form.GenericMilestoneRowForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestonesForm;
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
    public List<MilestonesForm> getAssessmentPeriodsForOverview(long competitionId) {
        Map<Long, List<MilestoneResource>> assessmentPeriodMap =
                milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess()
                        .stream()
                        .filter(milestone -> milestone.getAssessmentPeriodId() != null)
                        .collect(Collectors.groupingBy(MilestoneResource::getAssessmentPeriodId));

        List<MilestonesForm> milestonesForms = new ArrayList<>();
        assessmentPeriodMap.forEach((key, value) -> {
            LinkedMap<String, GenericMilestoneRowForm> milestoneFormEntries = new LinkedMap<>();
            value.stream().forEachOrdered(milestone ->
                    milestoneFormEntries.put(milestone.getType().getMilestoneDescription(), populateMilestoneFormEntries(milestone))
            );
            MilestonesForm milestonesForm = new MilestonesForm();
            milestonesForm.setMilestoneEntries(milestoneFormEntries);
            milestonesForms.add(milestonesForm);
        });
        return milestonesForms;
    }

    private String getName(MilestoneResource milestone) {
        switch (milestone.getType()) {
            case ASSESSOR_BRIEFING:
                return "1. Assessor briefing";
            case ASSESSOR_ACCEPTS:
                return "2. Acceptance deadline";
            case ASSESSOR_DEADLINE:
                return "3. Assessment deadline";
        }
        return milestone.getType().name();
    }


    @Override
    public List<AssessmentPeriodForm> getAssessmentPeriodMilestonesForms(long competitionId) {
        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess();
        Map<Long, List<MilestoneResource>> existingAssessmentPeriods = milestones
                .stream()
                .filter(milestone -> milestone.getAssessmentPeriodId() != null)
                .collect(Collectors.groupingBy(MilestoneResource::getAssessmentPeriodId));

        List<AssessmentPeriodForm> milestonesForms = new ArrayList<>();
        existingAssessmentPeriods.forEach((key, value) -> {
            LinkedMap<String, MilestoneRowForm> milestoneFormEntries = new LinkedMap<>();
            value.stream().forEachOrdered(milestone ->
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
        List<MilestoneResource> existingMilestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess()
                .stream()
                .filter(this::isOfAssessmentPeriodMilestoneType)
                .collect(Collectors.toList());

        Map<Long, Collection<MilestoneRowForm>> formMilestones = new HashMap<>();
        form.getFormList().forEach(assessmentPeriodForm -> {
            Long assessmentPeriodId = assessmentPeriodForm.getAssessmentPeriodId();
            formMilestones.put(assessmentPeriodId, assessmentPeriodForm.getMilestoneEntries().values());
        });

        // group the existing by id and match? Or just push straight?
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

    private boolean isOfAssessmentPeriodMilestoneType(MilestoneResource existingMilestone) {
        return existingMilestone.getType().equals(MilestoneType.ASSESSOR_BRIEFING) ||
                existingMilestone.getType().equals(MilestoneType.ASSESSOR_ACCEPTS) ||
                existingMilestone.getType().equals(MilestoneType.ASSESSOR_DEADLINE);
    }


}
