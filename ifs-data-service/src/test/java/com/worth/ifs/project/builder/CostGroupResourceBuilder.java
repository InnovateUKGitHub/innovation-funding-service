package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.finance.resource.CostGroupResource;
import com.worth.ifs.project.finance.resource.CostResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CostGroupResourceBuilder extends BaseBuilder<CostGroupResource, CostGroupResourceBuilder> {

    private CostGroupResourceBuilder(List<BiConsumer<Integer, CostGroupResource>> multiActions) {
        super(multiActions);
    }

    public static CostGroupResourceBuilder newCostGroupResource() {
        return new CostGroupResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CostGroupResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CostGroupResource>> actions) {
        return new CostGroupResourceBuilder(actions);
    }

    @Override
    protected CostGroupResource createInitial() {
        return new CostGroupResource();
    }


    @SafeVarargs
    public final CostGroupResourceBuilder withCosts(List<CostResource>... costs) {
        return withArray((cost, costGroupResource) -> setField("costs", cost, costGroupResource), costs);
    }

    public CostGroupResourceBuilder withDescription(String... descriptions) {
        return withArray((description, costGroupResource) -> setField("description", description, costGroupResource), descriptions);
    }
}
