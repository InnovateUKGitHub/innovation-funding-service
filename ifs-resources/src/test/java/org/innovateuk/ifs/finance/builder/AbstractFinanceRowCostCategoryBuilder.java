package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class AbstractFinanceRowCostCategoryBuilder<T extends FinanceRowCostCategory, S extends AbstractFinanceRowCostCategoryBuilder> extends BaseBuilder<T, S> {

    protected AbstractFinanceRowCostCategoryBuilder(List<BiConsumer<Integer, T>> newMultiActions) {
        super(newMultiActions);
    }

    public S withCosts(List<? extends FinanceRowItem>... costs) {
        return withArray((cost, financeRow) -> cost.forEach(financeRow::addCost), costs);
    }
}
