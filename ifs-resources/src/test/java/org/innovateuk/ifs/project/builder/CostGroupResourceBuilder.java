package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.finance.resource.CostGroupResource;
import org.innovateuk.ifs.project.finance.resource.CostResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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
