package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Resource builder for CompetitionFundedKeyStatisticsResources
 */
public class CompetitionFundedKeyStatisticsResourceBuilder extends BaseBuilder<CompetitionFundedKeyStatisticsResource, CompetitionFundedKeyStatisticsResourceBuilder> {

    public static CompetitionFundedKeyStatisticsResourceBuilder newCompetitionFundedKeyStatisticsResource() {
        return new CompetitionFundedKeyStatisticsResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionFundedKeyStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionFundedKeyStatisticsResource>> actions) {
        return new CompetitionFundedKeyStatisticsResourceBuilder(actions);
    }

    @Override
    protected CompetitionFundedKeyStatisticsResource createInitial() {
        return new CompetitionFundedKeyStatisticsResource();
    }

    private CompetitionFundedKeyStatisticsResourceBuilder(List<BiConsumer<Integer, CompetitionFundedKeyStatisticsResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionFundedKeyStatisticsResourceBuilder withApplicationsSubmitted(Integer... applicationsSubmitted) {
        return withArraySetFieldByReflection("applicationsSubmitted", applicationsSubmitted);
    }

    public CompetitionFundedKeyStatisticsResourceBuilder withApplicationsFunded(Integer... applicationsFunded) {
        return withArraySetFieldByReflection("applicationsFunded", applicationsFunded);
    }

    public CompetitionFundedKeyStatisticsResourceBuilder withApplicationsNotFunded(Integer... applicationsNotFunded) {
        return withArraySetFieldByReflection("applicationsNotFunded", applicationsNotFunded);
    }

    public CompetitionFundedKeyStatisticsResourceBuilder withApplicationsOnHold(Integer... applicationsOnHold) {
        return withArraySetFieldByReflection("applicationsOnHold", applicationsOnHold);
    }

    public CompetitionFundedKeyStatisticsResourceBuilder withApplicationsNotifiedOfDecision(Integer... applicationsNotifiedOfDecision) {
        return withArraySetFieldByReflection("applicationsNotifiedOfDecision", applicationsNotifiedOfDecision);
    }

    public CompetitionFundedKeyStatisticsResourceBuilder withApplicationsAwaitingDecision(Integer... applicationsAwaitingDecision) {
        return withArraySetFieldByReflection("applicationsAwaitingDecision", applicationsAwaitingDecision);
    }
}
