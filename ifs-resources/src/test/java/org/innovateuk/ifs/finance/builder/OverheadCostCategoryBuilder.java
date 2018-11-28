package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.category.OverheadCostCategory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class OverheadCostCategoryBuilder extends AbstractFinanceRowCostCategoryBuilder<OverheadCostCategory, OverheadCostCategoryBuilder> {

    public static OverheadCostCategoryBuilder newOverheadCostCategory() {
        return new OverheadCostCategoryBuilder(emptyList());
    }

    private OverheadCostCategoryBuilder(List<BiConsumer<Integer, OverheadCostCategory>> multiActions) {
        super(multiActions);
    }

    @Override
    protected OverheadCostCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OverheadCostCategory>> actions) {
        return new OverheadCostCategoryBuilder(actions);
    }

    @Override
    protected OverheadCostCategory createInitial() {
        return new OverheadCostCategory();
    }
}

    

