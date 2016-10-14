package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.LabourCost;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.idBasedNames;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public abstract class AbstractCostBuilder<S extends FinanceRowItem, T extends AbstractCostBuilder> extends BaseBuilder<S, T> {

    protected AbstractCostBuilder(List<BiConsumer<Integer, S>> multiActions) {
        super(multiActions);
    }

}
