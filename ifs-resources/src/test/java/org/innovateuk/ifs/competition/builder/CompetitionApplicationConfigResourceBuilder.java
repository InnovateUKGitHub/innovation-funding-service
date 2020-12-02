package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionApplicationConfigResourceBuilder extends BaseBuilder<CompetitionApplicationConfigResource, CompetitionApplicationConfigResourceBuilder> {

    private CompetitionApplicationConfigResourceBuilder (List<BiConsumer<Integer,CompetitionApplicationConfigResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionApplicationConfigResourceBuilder newCompetitionApplicationConfigResource() {
        return new CompetitionApplicationConfigResourceBuilder(emptyList());
    }

    public CompetitionApplicationConfigResourceBuilder withMaximumFundingSought(BigDecimal... maximumFundingSought) {
        return withArraySetFieldByReflection("maximumFundingSought", maximumFundingSought);
    }

    @Override
    protected CompetitionApplicationConfigResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionApplicationConfigResource>> actions) {
        return new CompetitionApplicationConfigResourceBuilder(actions);
    }

    @Override
    protected CompetitionApplicationConfigResource createInitial() {
        return new CompetitionApplicationConfigResource();
    }
}
