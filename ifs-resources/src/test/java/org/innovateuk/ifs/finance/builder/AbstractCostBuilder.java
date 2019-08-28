package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.AbstractFinanceRowItem;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class AbstractCostBuilder<S extends AbstractFinanceRowItem, T extends AbstractCostBuilder> extends BaseBuilder<S, T> {

    public T withTargetId(Long... targetIds) {
        return withArray((targetId, cost) -> cost.setTargetId(targetId), targetIds);
    }

    protected AbstractCostBuilder(List<BiConsumer<Integer, S>> multiActions) {
        super(multiActions);
    }

}

