package org.innovateuk.ifs.management.assessor.populator;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.assessment.populator.BaseManageAssessmentsModelPopulator;
import org.innovateuk.ifs.management.assessor.viewmodel.ManageAssessorsRowViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.ManageAssessorsViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Component
public class ManageAssessorsModelPopulator extends BaseManageAssessmentsModelPopulator {

    @Autowired
    private CategoryRestService categoryRestService;

    @Autowired
    private MilestoneRestService milestoneRestService;

    public ManageAssessorsViewModel populateModel(CompetitionResource competition, AssessorCountSummaryPageResource assessorCounts, Long assessmentPeriodId) {

        String assessmentPeriodName = assessmentPeriodName(assessmentPeriodId, competition.getId());

        return new ManageAssessorsViewModel(
                competition.getId(), competition.getName(),
                assessmentPeriodId,
                assessmentPeriodName,
                simpleMap(assessorCounts.getContent(), this::getRowViewModel),
                competition.getCompetitionStatus() == IN_ASSESSMENT,
                categoryRestService.getInnovationSectors().getSuccess(),
                new Pagination(assessorCounts));
    }

    private String assessmentPeriodName(Long assessmentPeriodId, long competitionId) {
        if (assessmentPeriodId == null) {
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
        if (first.getDate().getYear() == last.getDate().getYear()) {
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

    public ManageAssessorsViewModel populateModel(CompetitionResource competition, AssessorCountSummaryPageResource assessorCounts) {
        return populateModel(competition, assessorCounts, null);
    }

    private ManageAssessorsRowViewModel getRowViewModel(AssessorCountSummaryResource assessorCount) {
        return new ManageAssessorsRowViewModel(assessorCount);
    }
}