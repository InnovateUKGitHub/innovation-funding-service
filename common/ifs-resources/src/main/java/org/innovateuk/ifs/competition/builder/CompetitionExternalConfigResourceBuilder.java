package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionExternalConfigResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CompetitionExternalConfigResourceBuilder extends BaseBuilder<CompetitionExternalConfigResource, CompetitionExternalConfigResourceBuilder> {

    private CompetitionExternalConfigResourceBuilder(List<BiConsumer<Integer, CompetitionExternalConfigResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected CompetitionExternalConfigResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionExternalConfigResource>> actions) {
        return new CompetitionExternalConfigResourceBuilder(actions);
    }

    @Override
    protected CompetitionExternalConfigResource createInitial() {
        return new CompetitionExternalConfigResource();
    }

    public static CompetitionExternalConfigResourceBuilder newCompetitionExternalConfigResource() {
        return new CompetitionExternalConfigResourceBuilder(emptyList()).with(uniqueIds());
    }

    public CompetitionExternalConfigResourceBuilder withExternalCompetitionId(String... externalCompetitionId) {
        return withArraySetFieldByReflection("externalCompetitionId", externalCompetitionId);
    }
}