package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.AssessorAssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Resource that aggregates an assessor's competition assessment data.
 */
public class AssessorCompetitionSummaryResourceBuilder extends BaseBuilder<AssessorCompetitionSummaryResource, AssessorCompetitionSummaryResourceBuilder> {

    public static AssessorCompetitionSummaryResourceBuilder newAssessorCompetitionSummaryResource() {
        return new AssessorCompetitionSummaryResourceBuilder(emptyList());
    }

    protected AssessorCompetitionSummaryResourceBuilder(List<BiConsumer<Integer, AssessorCompetitionSummaryResource>> newActions) {
        super(newActions);
    }

    @Override
    protected AssessorCompetitionSummaryResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorCompetitionSummaryResource>> actions) {
        return new AssessorCompetitionSummaryResourceBuilder(actions);
    }

    @Override
    protected AssessorCompetitionSummaryResource createInitial() {
        return new AssessorCompetitionSummaryResource();
    }

    public AssessorCompetitionSummaryResourceBuilder withAssessor(AssessorProfileResource ...assessors) {
        return withArraySetFieldByReflection("assessor", assessors);
    }

    public AssessorCompetitionSummaryResourceBuilder withCompetitionId(Long ...competitionIds) {
        return withArraySetFieldByReflection("competitionId", competitionIds);
    }

    public AssessorCompetitionSummaryResourceBuilder withCompetitionName(String ...competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public AssessorCompetitionSummaryResourceBuilder withCompetitionStatus(CompetitionStatus ...competitionStatuses) {
        return withArraySetFieldByReflection("competitionStatus", competitionStatuses);
    }

    public AssessorCompetitionSummaryResourceBuilder withTotalApplications(Long ...totalApplications) {
        return withArraySetFieldByReflection("totalApplications", totalApplications);
    }

    public AssessorCompetitionSummaryResourceBuilder withAssignedAssessments(List<AssessorAssessmentResource> ...assignedAssessments) {
        return withArraySetFieldByReflection("assignedAssessments", assignedAssessments);
    }
}
