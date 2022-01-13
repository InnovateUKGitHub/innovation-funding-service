package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.finance.resource.CostCategoryGroupResource;
import org.innovateuk.ifs.project.finance.resource.CostCategoryResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CostCategoryResourceBuilder extends BaseBuilder<CostCategoryResource, CostCategoryResourceBuilder> {

    public CostCategoryResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public CostCategoryResourceBuilder withName(String... names) {
        return withArray((name, costCategory) -> setField("name", name, costCategory), names);
    }

    public CostCategoryResourceBuilder withCostCategoryGroup(CostCategoryGroupResource... costCategoryGroupResources) {
        return withArray((costCategoryGroupResource, costCategory) -> setField("costCategoryGroup", costCategoryGroupResource, costCategory), costCategoryGroupResources);
    }

    private CostCategoryResourceBuilder(List<BiConsumer<Integer, CostCategoryResource>> multiActions) {
        super(multiActions);
    }

    public static CostCategoryResourceBuilder newCostCategoryResource() {
        return new CostCategoryResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CostCategoryResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CostCategoryResource>> actions) {
        return new CostCategoryResourceBuilder(actions);
    }

    @Override
    protected CostCategoryResource createInitial() {
        return new CostCategoryResource();
    }
}
