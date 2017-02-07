package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionInAssessmentKeyStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionInAssessmentKeyStatisticsResourceBuilder extends BaseBuilder<CompetitionInAssessmentKeyStatisticsResource, CompetitionInAssessmentKeyStatisticsResourceBuilder> {

    public static CompetitionInAssessmentKeyStatisticsResourceBuilder newCompetitionInAssessmentKeyStatisticsResource() {
        return new CompetitionInAssessmentKeyStatisticsResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionInAssessmentKeyStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionInAssessmentKeyStatisticsResource>> actions) {
        return new CompetitionInAssessmentKeyStatisticsResourceBuilder(actions);
    }

    @Override
    protected CompetitionInAssessmentKeyStatisticsResource createInitial() {
        return new CompetitionInAssessmentKeyStatisticsResource();
    }

    private CompetitionInAssessmentKeyStatisticsResourceBuilder(List<BiConsumer<Integer, CompetitionInAssessmentKeyStatisticsResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionInAssessmentKeyStatisticsResourceBuilder withAssignmentCount(Long... assignmentCounts) {
        return withArraySetFieldByReflection("assignmentCount", assignmentCounts);
    }

    public CompetitionInAssessmentKeyStatisticsResourceBuilder withAssignmentsWaiting(Long... assignmentsWaitings) {
        return withArraySetFieldByReflection("assignmentsWaiting", assignmentsWaitings);
    }

    public CompetitionInAssessmentKeyStatisticsResourceBuilder withAssignmentsAccepted(Long... assignmentsAccepteds) {
        return withArraySetFieldByReflection("assignmentsAccepted", assignmentsAccepteds);
    }

    public CompetitionInAssessmentKeyStatisticsResourceBuilder withAssessmentsStarted(Long... assessmentsStarteds) {
        return withArraySetFieldByReflection("assessmentsStarted", assessmentsStarteds);
    }

    public CompetitionInAssessmentKeyStatisticsResourceBuilder withAssessmentsSubmitted(Long... assessmentsSubmitteds) {
        return withArraySetFieldByReflection("assessmentsSubmitted", assessmentsSubmitteds);
    }

}
