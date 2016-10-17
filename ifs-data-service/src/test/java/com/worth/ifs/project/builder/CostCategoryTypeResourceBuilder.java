package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.finance.resource.CostCategoryResource;
import com.worth.ifs.project.finance.resource.CostCategoryTypeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CostCategoryTypeResourceBuilder extends BaseBuilder<CostCategoryTypeResource, CostCategoryTypeResourceBuilder> {

    private CostCategoryTypeResourceBuilder(List<BiConsumer<Integer, CostCategoryTypeResource>> multiActions) {
        super(multiActions);
    }

    public static CostCategoryTypeResourceBuilder newCostCategoryTypeResource() {
        return new CostCategoryTypeResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CostCategoryTypeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CostCategoryTypeResource>> actions) {
        return new CostCategoryTypeResourceBuilder(actions);
    }

    @Override
    protected CostCategoryTypeResource createInitial() {
        return new CostCategoryTypeResource();
    }

}
