package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CostBuilder extends BaseBuilder<Cost, CostBuilder> {

    private CostBuilder(List<BiConsumer<Integer, Cost>> multiActions) {
        super(multiActions);
    }

    public static CostBuilder newCost() {
        return new CostBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CostBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Cost>> actions) {
        return new CostBuilder(actions);
    }

    @Override
    protected Cost createInitial() {
        return new Cost();
    }


    public CostBuilder withValue(BigDecimal... values) {
        return withArray((value, cost) -> setField("value", value, cost), values);
    }

    public CostBuilder withValue(String... values) {
        return withArray((value, cost) -> setField("value",  new BigDecimal(value), cost), values);
    }

    public CostBuilder withCostCategory(CostCategory... costCategories) {
        return withArray((costCategory, cost) -> setField("costCategory", costCategory, cost), costCategories);
    }
}
