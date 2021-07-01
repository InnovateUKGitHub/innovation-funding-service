package org.innovateuk.ifs.management.assessmentperiod.populator;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.commons.resource.PageResource;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
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
import java.util.stream.IntStream;

@Component
public class AssessmentPeriodFormPopulator {

    @Autowired
    private MilestoneRestService milestoneRestService;

    public ManageAssessmentPeriodsForm populate(long competitionId, PageResource<AssessmentPeriodResource> assessmentPeriodResources, boolean addAssessment) {
        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess();
        Map<Long, List<MilestoneResource>> periodIdToMilestoneMap = milestones
                .stream()
                .filter(milestone -> milestone.getAssessmentPeriodId() != null)
                .collect(Collectors.groupingBy(MilestoneResource::getAssessmentPeriodId));
        List<AssessmentPeriodForm> assessmentPeriods = IntStream.range(0, assessmentPeriodResources.getContent().size())
                .mapToObj(index -> {
                    AssessmentPeriodResource assessmentPeriod = assessmentPeriodResources.getContent().get(index);
                    LinkedMap<String, MilestoneRowForm> milestoneFormEntries = new LinkedMap<>();
                    periodIdToMilestoneMap.get(assessmentPeriod.getId()).forEach(milestone ->
                            milestoneFormEntries.put(milestone.getType().name(), populateMilestoneFormEntries(milestone))
                    );
                    AssessmentPeriodForm assessmentPeriodForm = new AssessmentPeriodForm();
                    assessmentPeriodForm.setAssessmentPeriodId(assessmentPeriod.getId());
                    assessmentPeriodForm.setMilestoneEntries(milestoneFormEntries);
                    int pageStart = (assessmentPeriodResources.getNumber() * assessmentPeriodResources.getSize());
                    assessmentPeriodForm.setIndex(index + 1 + pageStart);
                    return assessmentPeriodForm;
                })
                .collect(Collectors.toList());
        ManageAssessmentPeriodsForm form = new ManageAssessmentPeriodsForm();
        form.setAssessmentPeriods(assessmentPeriods);
        return form;
    }

    private MilestoneRowForm populateMilestoneFormEntries(MilestoneResource milestone) {
        return new MilestoneRowForm(milestone.getType(), milestone.getDate(), isEditable(milestone));
    }

    private boolean isEditable(MilestoneResource milestone) {
        return milestone.getDate() == null || milestone.getDate().isAfter(ZonedDateTime.now());
    }
}
