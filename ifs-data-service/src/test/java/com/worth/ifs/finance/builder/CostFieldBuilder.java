package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.domain.CostField;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Builder for CostField entities.
 */
public class CostFieldBuilder extends BaseBuilder<CostField, CostFieldBuilder> {

    private CostFieldBuilder(List<BiConsumer<Integer, CostField>> newMultiActions) {
        super(newMultiActions);
    }

    public static CostFieldBuilder newCostField() {
        return new CostFieldBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedTitles("Title ")).
                with(idBasedTypes("Type "));
    }

    @Override
    protected CostFieldBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CostField>> actions) {
        return new CostFieldBuilder(actions);
    }

    @Override
    protected CostField createInitial() {
        return new CostField();
    }
}
