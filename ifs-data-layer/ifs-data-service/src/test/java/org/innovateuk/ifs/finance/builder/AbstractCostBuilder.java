package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class AbstractCostBuilder<S extends FinanceRowItem, T extends BaseBuilder<S, T>> extends BaseBuilder<S, T> {

    protected AbstractCostBuilder(List<BiConsumer<Integer, S>> multiActions) {
        super(multiActions);
    }

}