package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.resource.CostValueResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for Cost entities.
 */
public class CostValueResourceBuilder extends BaseBuilder<CostValueResource, CostValueResourceBuilder> {

    private CostValueResourceBuilder(List<BiConsumer<Integer, CostValueResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CostValueResourceBuilder newCostValue() {
        return new CostValueResourceBuilder(emptyList());
    }

    @Override
    protected CostValueResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CostValueResource>> actions) {
        return new CostValueResourceBuilder(actions);
    }

    @Override
    protected CostValueResource createInitial() {
        return new CostValueResource();
    }

    public CostValueResourceBuilder withCostField(final Long costFieldId){
        return with(costValue -> costValue.setCostField(costFieldId));
    }

    public CostValueResourceBuilder withCost(final Long costId) {
        return with(costValue -> costValue.setCost(costId));
    }
}
