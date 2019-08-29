package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.category.ExcludedCostCategory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class GrantClaimCostCategoryBuilder extends AbstractFinanceRowCostCategoryBuilder<ExcludedCostCategory, GrantClaimCostCategoryBuilder> {

    public static GrantClaimCostCategoryBuilder newGrantClaimCostCategory() {
        return new GrantClaimCostCategoryBuilder(emptyList());
    }

    private GrantClaimCostCategoryBuilder(List<BiConsumer<Integer, ExcludedCostCategory>> multiActions) {
        super(multiActions);
    }

    @Override
    protected GrantClaimCostCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ExcludedCostCategory>> actions) {
        return new GrantClaimCostCategoryBuilder(actions);
    }

    @Override
    protected ExcludedCostCategory createInitial() {
        return new ExcludedCostCategory();
    }
}

