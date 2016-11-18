package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.resource.cost.LabourCost;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class LabourCostBuilder extends BaseBuilder<LabourCost, LabourCostBuilder> {

    public static LabourCostBuilder newLabourCost() {
        return new LabourCostBuilder(emptyList()).with(uniqueIds()).with(idBasedNames("LabourCost "));
    }

    private LabourCostBuilder(List<BiConsumer<Integer, LabourCost>> multiActions) {
        super(multiActions);
    }

    @Override
    protected LabourCostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, LabourCost>> actions) {
        return new LabourCostBuilder(actions);
    }

    @Override
    protected LabourCost createInitial() {
        return new LabourCost();
    }
}
