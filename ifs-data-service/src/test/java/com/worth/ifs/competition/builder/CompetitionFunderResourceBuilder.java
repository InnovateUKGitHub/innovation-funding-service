package com.worth.ifs.competition.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.competition.resource.CompetitionFunderResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

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

    public CompetitionFunderResourceBuilder withFunder(String funder) {
        return with(competitionFunder -> setField("funder", funder, competitionFunder));
    }

    public CompetitionFunderResourceBuilder withFunderBudget(BigDecimal funderBudget) {
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
