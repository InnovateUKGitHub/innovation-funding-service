package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.finance.domain.Cost;
import com.worth.ifs.project.finance.domain.CostGroup;
import com.worth.ifs.project.finance.resource.CostGroupResource;
import com.worth.ifs.project.finance.resource.CostResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CostGroupBuilder extends BaseBuilder<CostGroup, CostGroupBuilder> {

    private CostGroupBuilder(List<BiConsumer<Integer, CostGroup>> multiActions) {
        super(multiActions);
    }

    public static CostGroupBuilder newCostGroup() {
        return new CostGroupBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CostGroupBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CostGroup>> actions) {
        return new CostGroupBuilder(actions);
    }

    @Override
    protected CostGroup createInitial() {
        return new CostGroup();
    }


    public CostGroupBuilder withCosts(List<Cost>... costs) {
        return withArray((cost, costGroup) -> setField("costs", cost, costGroup), costs);
    }

    public CostGroupBuilder withDescription(String... descriptions) {
        return withArray((description, costGroupResource) -> setField("description", description, costGroupResource), descriptions);
    }
}
