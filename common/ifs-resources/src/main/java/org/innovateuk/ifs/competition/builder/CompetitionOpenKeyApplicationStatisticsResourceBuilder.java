package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyApplicationStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionOpenKeyApplicationStatisticsResourceBuilder extends
        BaseBuilder<CompetitionOpenKeyApplicationStatisticsResource,
                CompetitionOpenKeyApplicationStatisticsResourceBuilder> {

    public static CompetitionOpenKeyApplicationStatisticsResourceBuilder
    newCompetitionOpenKeyApplicationStatisticsResource() {
        return new CompetitionOpenKeyApplicationStatisticsResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionOpenKeyApplicationStatisticsResourceBuilder createNewBuilderWithActions(
            List<BiConsumer<Integer, CompetitionOpenKeyApplicationStatisticsResource>> actions) {
        return new CompetitionOpenKeyApplicationStatisticsResourceBuilder(actions);
    }

    @Override
    protected CompetitionOpenKeyApplicationStatisticsResource createInitial() {
        return new CompetitionOpenKeyApplicationStatisticsResource();
    }

    private CompetitionOpenKeyApplicationStatisticsResourceBuilder(List<BiConsumer<Integer,
            CompetitionOpenKeyApplicationStatisticsResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionOpenKeyApplicationStatisticsResourceBuilder withApplicationsPerAssessor(Integer... applicationsPerAssessors) {
        return withArraySetFieldByReflection("applicationsPerAssessor", applicationsPerAssessors);
    }

    public CompetitionOpenKeyApplicationStatisticsResourceBuilder withApplicationsStarted(Integer... applicationsStarteds) {
        return withArraySetFieldByReflection("applicationsStarted", applicationsStarteds);
    }

    public CompetitionOpenKeyApplicationStatisticsResourceBuilder withApplicationsPastHalf(Integer... applicationsPastHalfs) {
        return withArraySetFieldByReflection("applicationsPastHalf", applicationsPastHalfs);
    }

    public CompetitionOpenKeyApplicationStatisticsResourceBuilder withApplicationsSubmitted(Integer... applicationsSubmitteds) {
        return withArraySetFieldByReflection("applicationsSubmitted", applicationsSubmitteds);
    }
}
