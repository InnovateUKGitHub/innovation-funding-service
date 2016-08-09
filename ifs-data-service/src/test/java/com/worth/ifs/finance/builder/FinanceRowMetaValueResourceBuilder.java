package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.resource.FinanceRowMetaValueResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for {@link FinanceRowMetaValueResource} entities.
 */
public class FinanceRowMetaValueResourceBuilder extends BaseBuilder<FinanceRowMetaValueResource, FinanceRowMetaValueResourceBuilder> {

    private FinanceRowMetaValueResourceBuilder(List<BiConsumer<Integer, FinanceRowMetaValueResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static FinanceRowMetaValueResourceBuilder newFinanceRowMetaValue() {
        return new FinanceRowMetaValueResourceBuilder(emptyList());
    }

    @Override
    protected FinanceRowMetaValueResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceRowMetaValueResource>> actions) {
        return new FinanceRowMetaValueResourceBuilder(actions);
    }

    @Override
    protected FinanceRowMetaValueResource createInitial() {
        return new FinanceRowMetaValueResource();
    }

    public FinanceRowMetaValueResourceBuilder withCostField(final Long costFieldId){
        return with(costValue -> costValue.setFinanceRowMetaField(costFieldId));
    }

    public FinanceRowMetaValueResourceBuilder withCost(final Long costId) {
        return with(costValue -> costValue.setFinanceRow(costId));
    }
}
