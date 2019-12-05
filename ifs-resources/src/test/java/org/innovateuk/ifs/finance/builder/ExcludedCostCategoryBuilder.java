package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.category.ExcludedCostCategory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ExcludedCostCategoryBuilder extends AbstractFinanceRowCostCategoryBuilder<ExcludedCostCategory, ExcludedCostCategoryBuilder> {

    public static ExcludedCostCategoryBuilder newExcludedCostCategory() {
        return new ExcludedCostCategoryBuilder(emptyList());
    }

    private ExcludedCostCategoryBuilder(List<BiConsumer<Integer, ExcludedCostCategory>> multiActions) {
        super(multiActions);
    }

    @Override
    protected ExcludedCostCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ExcludedCostCategory>> actions) {
        return new ExcludedCostCategoryBuilder(actions);
    }

    @Override
    protected ExcludedCostCategory createInitial() {
        return new ExcludedCostCategory();
    }
}

