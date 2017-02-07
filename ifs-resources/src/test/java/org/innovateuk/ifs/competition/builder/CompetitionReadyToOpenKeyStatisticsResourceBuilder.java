package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionReadyToOpenKeyStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionReadyToOpenKeyStatisticsResourceBuilder extends BaseBuilder<CompetitionReadyToOpenKeyStatisticsResource, CompetitionReadyToOpenKeyStatisticsResourceBuilder> {

    public static CompetitionReadyToOpenKeyStatisticsResourceBuilder newCompetitionReadyToOpenKeyStatisticsResource() {
        return new CompetitionReadyToOpenKeyStatisticsResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionReadyToOpenKeyStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionReadyToOpenKeyStatisticsResource>> actions) {
        return new CompetitionReadyToOpenKeyStatisticsResourceBuilder(actions);
    }

    @Override
    protected CompetitionReadyToOpenKeyStatisticsResource createInitial() {
        return new CompetitionReadyToOpenKeyStatisticsResource();
    }

    private CompetitionReadyToOpenKeyStatisticsResourceBuilder(List<BiConsumer<Integer, CompetitionReadyToOpenKeyStatisticsResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionReadyToOpenKeyStatisticsResourceBuilder withAssessorsInvited(Long... assessorsInviteds) {
        return withArraySetFieldByReflection("assessorsInvited", assessorsInviteds);
    }

    public CompetitionReadyToOpenKeyStatisticsResourceBuilder withAssessorsAccepted(Long... assessorsAccepteds) {
        return withArraySetFieldByReflection("assessorsAccepted", assessorsAccepteds);
    }

}
