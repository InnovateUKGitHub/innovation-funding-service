package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyApplicationStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionFundedKeyApplicationStatisticsResourceBuilder extends
        BaseBuilder<CompetitionFundedKeyApplicationStatisticsResource, CompetitionFundedKeyApplicationStatisticsResourceBuilder> {

    public static CompetitionFundedKeyApplicationStatisticsResourceBuilder newCompetitionFundedKeyApplicationStatisticsResource() {
        return new CompetitionFundedKeyApplicationStatisticsResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionFundedKeyApplicationStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
            CompetitionFundedKeyApplicationStatisticsResource>> actions) {
        return new CompetitionFundedKeyApplicationStatisticsResourceBuilder(actions);
    }

    @Override
    protected CompetitionFundedKeyApplicationStatisticsResource createInitial() {
        return new CompetitionFundedKeyApplicationStatisticsResource();
    }

    private CompetitionFundedKeyApplicationStatisticsResourceBuilder(List<BiConsumer<Integer,
            CompetitionFundedKeyApplicationStatisticsResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionFundedKeyApplicationStatisticsResourceBuilder withApplicationsSubmitted(Integer... applicationsSubmitted) {
        return withArraySetFieldByReflection("applicationsSubmitted", applicationsSubmitted);
    }

    public CompetitionFundedKeyApplicationStatisticsResourceBuilder withApplicationsFunded(Integer... applicationsFunded) {
        return withArraySetFieldByReflection("applicationsFunded", applicationsFunded);
    }

    public CompetitionFundedKeyApplicationStatisticsResourceBuilder withApplicationsNotFunded(Integer... applicationsNotFunded) {
        return withArraySetFieldByReflection("applicationsNotFunded", applicationsNotFunded);
    }

    public CompetitionFundedKeyApplicationStatisticsResourceBuilder withApplicationsOnHold(Integer... applicationsOnHold) {
        return withArraySetFieldByReflection("applicationsOnHold", applicationsOnHold);
    }

    public CompetitionFundedKeyApplicationStatisticsResourceBuilder withApplicationsNotifiedOfDecision(Integer... applicationsNotifiedOfDecision) {
        return withArraySetFieldByReflection("applicationsNotifiedOfDecision", applicationsNotifiedOfDecision);
    }

    public CompetitionFundedKeyApplicationStatisticsResourceBuilder withApplicationsAwaitingDecision(Integer... applicationsAwaitingDecision) {
        return withArraySetFieldByReflection("applicationsAwaitingDecision", applicationsAwaitingDecision);
    }
}
