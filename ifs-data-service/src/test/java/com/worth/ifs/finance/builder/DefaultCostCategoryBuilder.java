package com.worth.ifs.finance.builder;

import com.worth.ifs.finance.resource.category.DefaultCostCategory;
import com.worth.ifs.finance.resource.category.LabourCostCategory;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class DefaultCostCategoryBuilder extends AbstractFinanceRowCostCategoryBuilder<DefaultCostCategory, DefaultCostCategoryBuilder> {

    public static DefaultCostCategoryBuilder newDefaultCostCategory() {
        return new DefaultCostCategoryBuilder(emptyList());
    }

    private DefaultCostCategoryBuilder(List<BiConsumer<Integer, DefaultCostCategory>> multiActions) {
        super(multiActions);
    }

    @Override
    protected DefaultCostCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, DefaultCostCategory>> actions) {
        return new DefaultCostCategoryBuilder(actions);
    }

    @Override
    protected DefaultCostCategory createInitial() {
        return new DefaultCostCategory();
    }
}

    

