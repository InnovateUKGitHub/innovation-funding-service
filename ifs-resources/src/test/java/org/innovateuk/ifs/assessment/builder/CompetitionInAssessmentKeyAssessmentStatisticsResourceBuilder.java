package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder extends
        BaseBuilder<CompetitionInAssessmentKeyAssessmentStatisticsResource,
                CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder> {

    public static CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder
    newCompetitionInAssessmentKeyAssessmentStatisticsResource() {
        return new CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder createNewBuilderWithActions
            (List<BiConsumer<Integer, CompetitionInAssessmentKeyAssessmentStatisticsResource>> actions) {
        return new CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder(actions);
    }

    @Override
    protected CompetitionInAssessmentKeyAssessmentStatisticsResource createInitial() {
        return new CompetitionInAssessmentKeyAssessmentStatisticsResource();
    }

    private CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder(List<BiConsumer<Integer,
            CompetitionInAssessmentKeyAssessmentStatisticsResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder withAssignmentCount(Integer...
                                                                                                     assignmentCounts) {
        return withArraySetFieldByReflection("assignmentCount", assignmentCounts);
    }

    public CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder withAssignmentsWaiting(Integer...
                                                                                                        assignmentsWaitings) {
        return withArraySetFieldByReflection("assignmentsWaiting", assignmentsWaitings);
    }

    public CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder withAssignmentsAccepted(Integer...
                                                                                                         assignmentsAccepteds) {
        return withArraySetFieldByReflection("assignmentsAccepted", assignmentsAccepteds);
    }

    public CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder withAssessmentsStarted(Integer...
                                                                                                        assessmentsStarteds) {
        return withArraySetFieldByReflection("assessmentsStarted", assessmentsStarteds);
    }

    public CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder withAssessmentsSubmitted(Integer...
                                                                                                          assessmentsSubmitteds) {
        return withArraySetFieldByReflection("assessmentsSubmitted", assessmentsSubmitteds);
    }

}
