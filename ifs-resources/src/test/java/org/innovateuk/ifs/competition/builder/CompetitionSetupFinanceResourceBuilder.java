package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class CompetitionSetupFinanceResourceBuilder extends BaseBuilder<CompetitionSetupFinanceResource, CompetitionSetupFinanceResourceBuilder> {

    public static CompetitionSetupFinanceResourceBuilder  newCompetitionSetupFinanceResource() {
        return new CompetitionSetupFinanceResourceBuilder(emptyList());
    }

    private CompetitionSetupFinanceResourceBuilder(List<BiConsumer<Integer, CompetitionSetupFinanceResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionSetupFinanceResourceBuilder withCompetitionId(Long... competitionIds) {
        return withArray((competitionId, competitionSetupFinance) -> setField("competitionId", competitionId, competitionSetupFinance), competitionIds);
    }

    public CompetitionSetupFinanceResourceBuilder withFullApplicationFinance(Boolean... fullApplicationFinances) {
        return withArray((fullApplicationFinance, competitionSetupFinance) -> setField("fullApplicationFinance", fullApplicationFinance, competitionSetupFinance), fullApplicationFinances);
    }

    public CompetitionSetupFinanceResourceBuilder withIncludeGrowthTable(Boolean... includeGrowthTables) {
        return withArray((includeGrowthTable, competitionSetupFinance) -> setField("includeGrowthTable", includeGrowthTable, competitionSetupFinance), includeGrowthTables);
    }

    @Override
    protected CompetitionSetupFinanceResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionSetupFinanceResource>> actions) {
        return new CompetitionSetupFinanceResourceBuilder(actions);
    }

    @Override
    protected CompetitionSetupFinanceResource createInitial() {
        return new CompetitionSetupFinanceResource();
    }
}
