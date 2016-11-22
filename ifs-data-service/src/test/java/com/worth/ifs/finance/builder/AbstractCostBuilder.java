package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class AbstractCostBuilder<S extends FinanceRowItem, T extends AbstractCostBuilder> extends BaseBuilder<S, T> {

    protected AbstractCostBuilder(List<BiConsumer<Integer, S>> multiActions) {
        super(multiActions);
    }

}
