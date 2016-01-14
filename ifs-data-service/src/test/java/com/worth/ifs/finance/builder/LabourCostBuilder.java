package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.resource.LabourCost;

import java.util.List;
import java.util.function.BiConsumer;

public class LabourCostBuilder extends BaseBuilder<LabourCost, LabourCostBuilder> {

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
