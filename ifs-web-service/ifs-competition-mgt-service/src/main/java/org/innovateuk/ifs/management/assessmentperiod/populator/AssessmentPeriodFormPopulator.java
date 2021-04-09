package org.innovateuk.ifs.management.assessmentperiod.populator;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;
import org.innovateuk.ifs.management.assessmentperiod.form.ManageAssessmentPeriodsForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AssessmentPeriodFormPopulator {

    @Autowired
    private MilestoneRestService milestoneRestService;

    public ManageAssessmentPeriodsForm populate(long competitionId) {
        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess();
        Map<Long, List<MilestoneResource>> periodIdToMilestoneMap = milestones
                .stream()
                .filter(milestone -> milestone.getAssessmentPeriodId() != null)
                .collect(Collectors.groupingBy(MilestoneResource::getAssessmentPeriodId));
        List<AssessmentPeriodForm> milestonesForms = periodIdToMilestoneMap.entrySet().stream()
                .map(e -> {
                    LinkedMap<String, MilestoneRowForm> milestoneFormEntries = new LinkedMap<>();
                    e.getValue().forEach(milestone ->
                            milestoneFormEntries.put(milestone.getType().getMilestoneDescription(), populateMilestoneFormEntries(milestone))
                    );
                    AssessmentPeriodForm milestonesForm = new AssessmentPeriodForm();
                    milestonesForm.setAssessmentPeriodId(e.getKey());
                    milestonesForm.setMilestoneEntries(milestoneFormEntries);
                    return milestonesForm;
                })
                .collect(Collectors.toList());
        ManageAssessmentPeriodsForm form = new ManageAssessmentPeriodsForm();
        form.setAssessmentPeriods(milestonesForms);
        return form;
    }

    private MilestoneRowForm populateMilestoneFormEntries(MilestoneResource milestone) {
        return new MilestoneRowForm(milestone.getType(), milestone.getDate(), isEditable(milestone));
    }

    private boolean isEditable(MilestoneResource milestone) {
        return milestone.getDate() == null || milestone.getDate().isAfter(ZonedDateTime.now());
    }
}
