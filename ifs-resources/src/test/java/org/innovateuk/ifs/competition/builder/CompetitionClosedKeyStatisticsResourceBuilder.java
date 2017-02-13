package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionClosedKeyStatisticsResourceBuilder extends BaseBuilder<CompetitionClosedKeyStatisticsResource, CompetitionClosedKeyStatisticsResourceBuilder> {

    public static CompetitionClosedKeyStatisticsResourceBuilder newCompetitionClosedKeyStatisticsResource() {
        return new CompetitionClosedKeyStatisticsResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionClosedKeyStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionClosedKeyStatisticsResource>> actions) {
        return new CompetitionClosedKeyStatisticsResourceBuilder(actions);
    }

    @Override
    protected CompetitionClosedKeyStatisticsResource createInitial() {
        return new CompetitionClosedKeyStatisticsResource();
    }

    private CompetitionClosedKeyStatisticsResourceBuilder(List<BiConsumer<Integer, CompetitionClosedKeyStatisticsResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionClosedKeyStatisticsResourceBuilder withAssessorsInvited(Integer... assessorsInviteds) {
        return withArraySetFieldByReflection("assessorsInvited", assessorsInviteds);
    }

    public CompetitionClosedKeyStatisticsResourceBuilder withAssessorsAccepted(Integer... assessorsAccepteds) {
        return withArraySetFieldByReflection("assessorsAccepted", assessorsAccepteds);
    }

    public CompetitionClosedKeyStatisticsResourceBuilder withApplicationsPerAssessor(Integer... applicationsPerAssessors) {
        return withArraySetFieldByReflection("applicationsPerAssessor", applicationsPerAssessors);
    }

    public CompetitionClosedKeyStatisticsResourceBuilder withApplicationsRequiringAssessors(Integer... applicationsRequiringAssessorss) {
        return withArraySetFieldByReflection("applicationsRequiringAssessors", applicationsRequiringAssessorss);
    }

    public CompetitionClosedKeyStatisticsResourceBuilder withAssessorsWithoutApplications(Integer... assessorsWithoutApplicationss) {
        return withArraySetFieldByReflection("assessorsWithoutApplications", assessorsWithoutApplicationss);
    }

    public CompetitionClosedKeyStatisticsResourceBuilder withAssignmentCount(Integer... assignmentCounts) {
        return withArraySetFieldByReflection("assignmentCount", assignmentCounts);
    }

}
