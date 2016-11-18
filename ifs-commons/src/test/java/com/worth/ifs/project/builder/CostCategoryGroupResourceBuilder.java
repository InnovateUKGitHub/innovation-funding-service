package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.finance.resource.CostCategoryGroupResource;
import com.worth.ifs.project.finance.resource.CostCategoryResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CostCategoryGroupResourceBuilder extends BaseBuilder<CostCategoryGroupResource, CostCategoryGroupResourceBuilder> {

    private CostCategoryGroupResourceBuilder(List<BiConsumer<Integer, CostCategoryGroupResource>> multiActions) {
        super(multiActions);
    }

    public static CostCategoryGroupResourceBuilder newCostCategoryGroupResource() {
        return new CostCategoryGroupResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CostCategoryGroupResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CostCategoryGroupResource>> actions) {
        return new CostCategoryGroupResourceBuilder(actions);
    }

    @Override
    protected CostCategoryGroupResource createInitial() {
        return new CostCategoryGroupResource();
    }

}
