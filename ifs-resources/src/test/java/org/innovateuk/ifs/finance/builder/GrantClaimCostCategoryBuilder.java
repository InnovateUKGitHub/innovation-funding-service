package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.category.GrantClaimCategory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class GrantClaimCostCategoryBuilder extends AbstractFinanceRowCostCategoryBuilder<GrantClaimCategory, GrantClaimCostCategoryBuilder> {

    public static GrantClaimCostCategoryBuilder newGrantClaimCostCategory() {
        return new GrantClaimCostCategoryBuilder(emptyList());
    }

    private GrantClaimCostCategoryBuilder(List<BiConsumer<Integer, GrantClaimCategory>> multiActions) {
        super(multiActions);
    }

    @Override
    protected GrantClaimCostCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, GrantClaimCategory>> actions) {
        return new GrantClaimCostCategoryBuilder(actions);
    }

    @Override
    protected GrantClaimCategory createInitial() {
        return new GrantClaimCategory();
    }
}

