package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionEoiKeyApplicationStatisticsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionEoiKeyApplicationStatisticsResourceBuilder extends
        BaseBuilder<CompetitionEoiKeyApplicationStatisticsResource, CompetitionEoiKeyApplicationStatisticsResourceBuilder> {

    public static CompetitionEoiKeyApplicationStatisticsResourceBuilder newCompetitionEoiKeyApplicationStatisticsResource() {
        return new CompetitionEoiKeyApplicationStatisticsResourceBuilder(emptyList());
    }

    @Override
    protected CompetitionEoiKeyApplicationStatisticsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionEoiKeyApplicationStatisticsResource>> actions) {
        return new CompetitionEoiKeyApplicationStatisticsResourceBuilder(actions);
    }

    @Override
    protected CompetitionEoiKeyApplicationStatisticsResource createInitial() {
        return new CompetitionEoiKeyApplicationStatisticsResource();
    }

    private CompetitionEoiKeyApplicationStatisticsResourceBuilder(List<BiConsumer<Integer, CompetitionEoiKeyApplicationStatisticsResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionEoiKeyApplicationStatisticsResourceBuilder withApplicationsSubmitted(Integer... applicationsSubmitted) {
        return withArraySetFieldByReflection("applicationsSubmitted", applicationsSubmitted);
    }
}
