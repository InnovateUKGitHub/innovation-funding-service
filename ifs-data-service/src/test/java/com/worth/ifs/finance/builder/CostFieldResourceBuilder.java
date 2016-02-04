package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.resource.CostFieldResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Builder for CostField entities.
 */
public class CostFieldResourceBuilder extends BaseBuilder<CostFieldResource, CostFieldResourceBuilder> {

    private CostFieldResourceBuilder(List<BiConsumer<Integer, CostFieldResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CostFieldResourceBuilder newCostFieldResource() {
        return new CostFieldResourceBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedTitles("Title ")).
                with(idBasedTypes("Type "));
    }

    @Override
    protected CostFieldResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CostFieldResource>> actions) {
        return new CostFieldResourceBuilder(actions);
    }

    @Override
    protected CostFieldResource createInitial() {
        return new CostFieldResource();
    }
}
