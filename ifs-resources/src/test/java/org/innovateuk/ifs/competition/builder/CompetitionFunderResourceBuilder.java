package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competition.resource.Funder;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CompetitionFunderResourceBuilder extends BaseBuilder<CompetitionFunderResource, CompetitionFunderResourceBuilder> {

    private CompetitionFunderResourceBuilder(List<BiConsumer<Integer, CompetitionFunderResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionFunderResourceBuilder newCompetitionFunderResource() {
        return new CompetitionFunderResourceBuilder(emptyList()).with(uniqueIds());
    }

    public CompetitionFunderResourceBuilder withId(Long id) {
        return with(competitionFunder -> setField("id", id, competitionFunder));
    }

    public CompetitionFunderResourceBuilder withFunder(Funder funder) {
        return with(competitionFunder -> setField("funder", funder, competitionFunder));
    }

    public CompetitionFunderResourceBuilder withFunderBudget(BigInteger funderBudget) {
        return with(competitionFunder -> setField("funderBudget", funderBudget, competitionFunder));
    }

    public CompetitionFunderResourceBuilder withCoFunder(Boolean coFunder) {
        return with(competitionFunder -> setField("coFunder", coFunder, competitionFunder));
    }

    public CompetitionFunderResourceBuilder withCompetitionId(Long competitionId) {
        return with(competitionFunder -> setField("competitionId", competitionId, competitionFunder));
    }

    @Override
    protected CompetitionFunderResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionFunderResource>> actions) {
        return new CompetitionFunderResourceBuilder(actions);
    }

    @Override
    protected CompetitionFunderResource createInitial() {
        return new CompetitionFunderResource();
    }
}
