package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssessmentPeriodService {

    private static final int ASSESSOR_BRIEFING_INDEX = 0;
    private static final int ASSESSOR_DEADLINE_INDEX = 2;

    @Autowired
    private AssessmentPeriodRestService assessmentPeriodRestService;

    @Autowired
    private MilestoneRestService milestoneRestService;

    public String assessmentPeriodName(long assessmentPeriodId, long competitionId) {

        List<AssessmentPeriodResource> assessmentPeriods = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId).getSuccess();

        if (!(assessmentPeriods.size() > 1)) {
            return null;
        }

        Map<Long, List<MilestoneResource>> milestonesGroupedByAssessmentPeriodId = milestonesGroupedByAssessmentPeriodId(competitionId);

        int index = 1;
        int assessmentPeriodNumber = -1;
        for (Map.Entry<Long, List<MilestoneResource>> assessmentPeriodEntry: milestonesGroupedByAssessmentPeriodId.entrySet()) {
            if (assessmentPeriodEntry.getKey().equals(assessmentPeriodId)) {
                assessmentPeriodNumber = index;
                break;
            }
            index++;
        }

        return String.format("Assessment period %d: ", assessmentPeriodNumber) + displayName(assessmentPeriodId, competitionId);
    }

    public String displayName(long assessmentPeriodId, long competitionId) {
        List<AssessmentPeriodResource> assessmentPeriods = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId).getSuccess();

        if (!(assessmentPeriods.size() > 1)) {
            return null;
        }

        Map<Long, List<MilestoneResource>> milestonesGroupedByAssessmentPeriodId = milestonesGroupedByAssessmentPeriodId(competitionId);

        List<MilestoneResource> milestonesForAssessmentPeriod = milestonesGroupedByAssessmentPeriodId.get(assessmentPeriodId);

        int milestonesSize = milestonesForAssessmentPeriod.size();
        MilestoneResource first = milestonesForAssessmentPeriod.get(ASSESSOR_BRIEFING_INDEX);
        MilestoneResource last;
        if (milestonesSize > ASSESSOR_DEADLINE_INDEX) {
            last = milestonesForAssessmentPeriod.get(ASSESSOR_DEADLINE_INDEX);
        } else {
            last = milestonesForAssessmentPeriod.get(milestonesSize - 1); //for unit test setup
        }

        ZonedDateTime start = first.getDate();
        ZonedDateTime end = last.getDate();

        if (start == null || end == null) {
            return "";
        }
        if (start.getYear() == end.getYear()) {
            return String.format("%s %s to %s %s %s",
                    start.getDayOfMonth(),
                    start.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    end.getDayOfMonth(),
                    end.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    end.getYear());
        }
        return String.format("%s %s %s to %s %s %s",
                start.getDayOfMonth(),
                start.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                start.getYear(),
                end.getDayOfMonth(),
                end.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                end.getYear());
    }

    private Map<Long, List<MilestoneResource>> milestonesGroupedByAssessmentPeriodId(long competitionId) {
        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess();
        return milestones.stream()
                .filter(m -> m.getAssessmentPeriodId() != null)
                .filter(m -> m.getDate() != null)
                .collect(Collectors.groupingBy(MilestoneResource::getAssessmentPeriodId));
    }
}
