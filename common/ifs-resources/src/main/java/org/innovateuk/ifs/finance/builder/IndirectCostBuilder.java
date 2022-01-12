package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.IndirectCost;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class IndirectCostBuilder extends BaseBuilder<IndirectCost, IndirectCostBuilder> {
    public IndirectCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public IndirectCostBuilder withCost(BigInteger... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public IndirectCostBuilder withTargetId(Long... value) {
        return withArraySetFieldByReflection("targetId", value);
    }

    public static IndirectCostBuilder newIndirectCost() {
        return new IndirectCostBuilder(emptyList()).with(uniqueIds());
    }

    private IndirectCostBuilder(List<BiConsumer<Integer, IndirectCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected IndirectCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, IndirectCost>> actions) {
        return new IndirectCostBuilder(actions);
    }

    @Override
    protected IndirectCost createInitial() {
        return newInstance(IndirectCost.class);
    }
}
