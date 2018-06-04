package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyApplicationStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionClosedKeyApplicationStatisticsResourceBuilder extends
        BaseBuilder<CompetitionClosedKeyApplicationStatisticsResource,
                CompetitionClosedKeyApplicationStatisticsResourceBuilder> {

    public static CompetitionClosedKeyApplicationStatisticsResourceBuilder
    newCompetitionClosedKeyApplicationStatisticsResource() {
        return new CompetitionClosedKeyApplicationStatisticsResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionClosedKeyApplicationStatisticsResourceBuilder createNewBuilderWithActions
            (List<BiConsumer<Integer, CompetitionClosedKeyApplicationStatisticsResource>> actions) {
        return new CompetitionClosedKeyApplicationStatisticsResourceBuilder(actions);
    }

    @Override
    protected CompetitionClosedKeyApplicationStatisticsResource createInitial() {
        return new CompetitionClosedKeyApplicationStatisticsResource();
    }

    private CompetitionClosedKeyApplicationStatisticsResourceBuilder(List<BiConsumer<Integer,
            CompetitionClosedKeyApplicationStatisticsResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionClosedKeyApplicationStatisticsResourceBuilder withApplicationsPerAssessor(Integer... applicationsPerAssessors) {
        return withArraySetFieldByReflection("applicationsPerAssessor", applicationsPerAssessors);
    }

    public CompetitionClosedKeyApplicationStatisticsResourceBuilder withApplicationsRequiringAssessors(Integer... applicationsRequiringAssessorss) {
        return withArraySetFieldByReflection("applicationsRequiringAssessors", applicationsRequiringAssessorss);
    }

    public CompetitionClosedKeyApplicationStatisticsResourceBuilder withAssignmentCount(Integer... assignmentCounts) {
        return withArraySetFieldByReflection("assignmentCount", assignmentCounts);
    }
}
