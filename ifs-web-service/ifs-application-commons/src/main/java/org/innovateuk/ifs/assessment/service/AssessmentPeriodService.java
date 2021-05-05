package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AssessmentPeriodService {

    @Autowired
    private AssessmentPeriodRestService assessmentPeriodRestService;

    @Autowired
    private MilestoneRestService milestoneRestService;

    public String assessmentPeriodName(long assessmentPeriodId, long competitionId) {

        List<AssessmentPeriodResource> assessmentPeriods = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId).getSuccess();

        if (!(assessmentPeriods.size() > 1)) {
            return null;
        }

        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess();
        Map<Long, List<MilestoneResource>> milestonesGroupedByAssessmentPeriodId = milestones.stream()
                .filter(m -> m.getAssessmentPeriodId() != null)
                .collect(Collectors.groupingBy(MilestoneResource::getAssessmentPeriodId));

        int index = 1;
        int assessmentPeriodNumber = -1;
        for (Map.Entry<Long, List<MilestoneResource>> assessmentPeriodEntry: milestonesGroupedByAssessmentPeriodId.entrySet()) {
            if (assessmentPeriodEntry.getKey().equals(assessmentPeriodId)) {
                assessmentPeriodNumber = index;
                break;
            }
            index++;
        }

        List<MilestoneResource> milestonesForAssessmentPeriod = milestonesGroupedByAssessmentPeriodId.get(assessmentPeriodId);

        MilestoneResource first = milestonesForAssessmentPeriod.get(0);
        MilestoneResource last = milestonesForAssessmentPeriod.get(milestonesForAssessmentPeriod.size() - 1);

        ZonedDateTime start = first.getDate();
        ZonedDateTime end = last.getDate();

        if (start == null || end == null) {
            return String.format("Assessment period %d", assessmentPeriodNumber);
        }
        if (start.getYear() == end.getYear()) {
            return String.format("Assessment period %d: %s %s to %s %s %s",
                    assessmentPeriodNumber,
                    start.getDayOfMonth(),
                    start.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    end.getDayOfMonth(),
                    end.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    end.getYear());
        }
        return String.format("Assessment period %d: %s %s %s to %s %s %s",
                assessmentPeriodNumber,
                start.getDayOfMonth(),
                start.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                start.getYear(),
                end.getDayOfMonth(),
                end.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                end.getYear());
    }
}
