package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.ApplicationFinanceType;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CompetitionSetupFinanceResourceBuilder extends BaseBuilder<CompetitionSetupFinanceResource, CompetitionSetupFinanceResourceBuilder> {

    public static CompetitionSetupFinanceResourceBuilder  newCompetitionSetupFinanceResource() {
        return new CompetitionSetupFinanceResourceBuilder(emptyList());
    }

    private CompetitionSetupFinanceResourceBuilder(List<BiConsumer<Integer, CompetitionSetupFinanceResource>> newMultiActions) {
        super(newMultiActions);
    }

    public CompetitionSetupFinanceResourceBuilder withCompetitionId(Long... competitionIds) {
        return withArray((competitionId, competitionSetupFinance) -> competitionSetupFinance.setCompetitionId(competitionId), competitionIds);
    }

    public CompetitionSetupFinanceResourceBuilder withApplicationFinanceType(ApplicationFinanceType... applicationFinanceTypes) {
        return withArray((applicationFinanceType, competitionSetupFinance) -> competitionSetupFinance.setApplicationFinanceType(applicationFinanceType), applicationFinanceTypes);
    }

    public CompetitionSetupFinanceResourceBuilder withIncludeGrowthTable(Boolean... includeGrowthTables) {
        return withArray((includeGrowthTable, competitionSetupFinance) -> competitionSetupFinance.setIncludeGrowthTable(includeGrowthTable), includeGrowthTables);
    }

    public CompetitionSetupFinanceResourceBuilder withIncludeJesForm(Boolean... includeJesForms) {
        return withArray((includeJesForm, competitionSetupFinance) -> competitionSetupFinance.setIncludeJesForm(includeJesForm), includeJesForms);
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
