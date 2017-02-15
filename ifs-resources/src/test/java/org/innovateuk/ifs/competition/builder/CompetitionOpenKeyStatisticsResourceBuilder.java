package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionOpenKeyStatisticsResourceBuilder extends BaseBuilder<CompetitionOpenKeyStatisticsResource, CompetitionOpenKeyStatisticsResourceBuilder> {

    public static CompetitionOpenKeyStatisticsResourceBuilder newCompetitionOpenKeyStatisticsResource() {
        return new CompetitionOpenKeyStatisticsResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionOpenKeyStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionOpenKeyStatisticsResource>> actions) {
        return new CompetitionOpenKeyStatisticsResourceBuilder(actions);
    }

    @Override
    protected CompetitionOpenKeyStatisticsResource createInitial() {
        return new CompetitionOpenKeyStatisticsResource();
    }

    private CompetitionOpenKeyStatisticsResourceBuilder(List<BiConsumer<Integer, CompetitionOpenKeyStatisticsResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionOpenKeyStatisticsResourceBuilder withAssessorsInvited(Integer... assessorsInviteds) {
        return withArraySetFieldByReflection("assessorsInvited", assessorsInviteds);
    }

    public CompetitionOpenKeyStatisticsResourceBuilder withAssessorsAccepted(Integer... assessorsAccepteds) {
        return withArraySetFieldByReflection("assessorsAccepted", assessorsAccepteds);
    }

    public CompetitionOpenKeyStatisticsResourceBuilder withApplicationsPerAssessor(Integer... applicationsPerAssessors) {
        return withArraySetFieldByReflection("applicationsPerAssessor", applicationsPerAssessors);
    }

    public CompetitionOpenKeyStatisticsResourceBuilder withApplicationsStarted(Integer... applicationsStarteds) {
        return withArraySetFieldByReflection("applicationsStarted", applicationsStarteds);
    }

    public CompetitionOpenKeyStatisticsResourceBuilder withApplicationsPastHalf(Integer... applicationsPastHalfs) {
        return withArraySetFieldByReflection("applicationsPastHalf", applicationsPastHalfs);
    }

    public CompetitionOpenKeyStatisticsResourceBuilder withApplicationsSubmitted(Integer... applicationsSubmitteds) {
        return withArraySetFieldByReflection("applicationsSubmitted", applicationsSubmitteds);
    }

}
