package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.finance.domain.CostCategory;
import com.worth.ifs.project.finance.resource.CostCategoryResource;
import com.worth.ifs.project.finance.resource.CostGroupResource;
import com.worth.ifs.project.finance.resource.CostResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CostResourceBuilder extends BaseBuilder<CostResource, CostResourceBuilder> {

    private CostResourceBuilder(List<BiConsumer<Integer, CostResource>> multiActions) {
        super(multiActions);
    }

    public static CostResourceBuilder newCostResource() {
        return new CostResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CostResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CostResource>> actions) {
        return new CostResourceBuilder(actions);
    }

    @Override
    protected CostResource createInitial() {
        return new CostResource();
    }


    public CostResourceBuilder withValue(BigDecimal... values) {
        return withArray((value, cost) -> setField("value", value, cost), values);
    }

    public CostResourceBuilder withValue(String... values) {
        return withArray((value, cost) -> setField("value",  new BigDecimal(value), cost), values);
    }

    public CostResourceBuilder withCostCategory(CostCategoryResource... costCategories) {
        return withArray((costCategory, cost) -> setField("costCategory", costCategory, cost), costCategories);
    }
}
