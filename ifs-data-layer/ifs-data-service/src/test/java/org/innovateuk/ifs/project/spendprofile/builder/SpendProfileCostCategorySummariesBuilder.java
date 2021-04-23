package org.innovateuk.ifs.project.spendprofile.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileCostCategorySummaries;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileCostCategorySummary;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class SpendProfileCostCategorySummariesBuilder extends BaseBuilder<SpendProfileCostCategorySummaries, SpendProfileCostCategorySummariesBuilder > {

    private SpendProfileCostCategorySummariesBuilder(final List<BiConsumer<Integer, SpendProfileCostCategorySummaries>> newActions) {
        super(newActions);
    }

    @Override
    protected SpendProfileCostCategorySummariesBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SpendProfileCostCategorySummaries>> actions) {
        return new SpendProfileCostCategorySummariesBuilder(actions);
    }


    public SpendProfileCostCategorySummariesBuilder withCosts(List<SpendProfileCostCategorySummary>... costs) {
        return withArraySetFieldByReflection("costs", costs);
    }

    public SpendProfileCostCategorySummariesBuilder withCostCategoryType(CostCategoryType... costCategoryType) {
        return withArraySetFieldByReflection("costCategoryType", costCategoryType);
    }


    public static SpendProfileCostCategorySummariesBuilder newSpendProfileCostCategorySummaries() {
        return new SpendProfileCostCategorySummariesBuilder(emptyList());
    }

    @Override
    protected SpendProfileCostCategorySummaries createInitial() {
        return newInstance(SpendProfileCostCategorySummaries.class);
    }

}
