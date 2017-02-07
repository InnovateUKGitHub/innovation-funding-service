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

    public CompetitionClosedKeyStatisticsResourceBuilder withAssessorsInvited(Long... assessorsInviteds) {
        return withArraySetFieldByReflection("assessorsInvited", assessorsInviteds);
    }

    public CompetitionClosedKeyStatisticsResourceBuilder withAssessorsAccepted(Long... assessorsAccepteds) {
        return withArraySetFieldByReflection("assessorsAccepted", assessorsAccepteds);
    }

    public CompetitionClosedKeyStatisticsResourceBuilder withApplicationsPerAssessor(Long... applicationsPerAssessors) {
        return withArraySetFieldByReflection("applicationsPerAssessor", applicationsPerAssessors);
    }

    public CompetitionClosedKeyStatisticsResourceBuilder withApplicationsRequiringAssessors(Long... applicationsRequiringAssessorss) {
        return withArraySetFieldByReflection("applicationsRequiringAssessors", applicationsRequiringAssessorss);
    }

    public CompetitionClosedKeyStatisticsResourceBuilder withAssessorsWithoutApplications(Long... assessorsWithoutApplicationss) {
        return withArraySetFieldByReflection("assessorsWithoutApplications", assessorsWithoutApplicationss);
    }

    public CompetitionClosedKeyStatisticsResourceBuilder withAssignmentCount(Long... assignmentCounts) {
        return withArraySetFieldByReflection("assignmentCount", assignmentCounts);
    }

}
