package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.finance.domain.CostCategory;
import com.worth.ifs.project.finance.domain.CostCategoryGroup;
import com.worth.ifs.project.finance.domain.CostCategoryType;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CostCategoryTypeBuilder extends BaseBuilder<CostCategoryType, CostCategoryTypeBuilder> {

    private CostCategoryTypeBuilder(List<BiConsumer<Integer, CostCategoryType>> multiActions) {
        super(multiActions);
    }

    public static CostCategoryTypeBuilder newCostCategoryType() {
        return new CostCategoryTypeBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CostCategoryTypeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CostCategoryType>> actions) {
        return new CostCategoryTypeBuilder(actions);
    }

    @Override
    protected CostCategoryType createInitial() {
        return new CostCategoryType();
    }

    public CostCategoryTypeBuilder withName(String... names) {
        return withArray((name, costCategoryType) -> setField("name", name, costCategoryType), names);
    }

    public CostCategoryTypeBuilder withCostCategoryGroup(CostCategoryGroup... groups) {
        return withArray((name, group) -> setField("costCategoryGroup", name, group), groups);
    }


}
