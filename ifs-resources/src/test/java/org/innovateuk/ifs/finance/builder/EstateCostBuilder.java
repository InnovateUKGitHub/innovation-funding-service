package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.cost.EstateCost;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class EstateCostBuilder extends BaseBuilder<EstateCost, EstateCostBuilder> {

    public EstateCostBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
    }

    public EstateCostBuilder withCost(BigInteger... value) {
        return withArraySetFieldByReflection("cost", value);
    }

    public EstateCostBuilder withDescription(String... value) {
        return withArraySetFieldByReflection("description", value);
    }

    public EstateCostBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public EstateCostBuilder withTargetId(Long... value) {
        return withArraySetFieldByReflection("targetId", value);
    }

    public static EstateCostBuilder newEstateCost() {
        return new EstateCostBuilder(emptyList()).with(uniqueIds());
    }

    private EstateCostBuilder(List<BiConsumer<Integer, EstateCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected EstateCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EstateCost>> actions) {
        return new EstateCostBuilder(actions);
    }

    @Override
    protected EstateCost createInitial() {
        return newInstance(EstateCost.class);
    }
}