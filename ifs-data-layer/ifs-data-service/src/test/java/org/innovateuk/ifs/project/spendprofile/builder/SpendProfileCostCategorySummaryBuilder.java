package org.innovateuk.ifs.project.spendprofile.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileCostCategorySummaries;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileCostCategorySummary;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class SpendProfileCostCategorySummaryBuilder extends BaseBuilder<SpendProfileCostCategorySummary, SpendProfileCostCategorySummaryBuilder> {

    private SpendProfileCostCategorySummaryBuilder(final List<BiConsumer<Integer, SpendProfileCostCategorySummary>> newActions) {
        super(newActions);
    }

    @Override
    protected SpendProfileCostCategorySummaryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SpendProfileCostCategorySummary>> actions) {
        return new SpendProfileCostCategorySummaryBuilder(actions);
    }


    public SpendProfileCostCategorySummaryBuilder withCategory(CostCategory... costCategories) {
        return withArraySetFieldByReflection("category", costCategories);
    }

    public SpendProfileCostCategorySummaryBuilder withTotal(BigDecimal... totals) {
        return withArraySetFieldByReflection("total", totals);
    }

    public static SpendProfileCostCategorySummaryBuilder newSpendProfileCostCategorySummary() {
        return new SpendProfileCostCategorySummaryBuilder(emptyList());
    }

    @Override
    protected SpendProfileCostCategorySummary createInitial() {
        return newInstance(SpendProfileCostCategorySummary.class);
    }

}
