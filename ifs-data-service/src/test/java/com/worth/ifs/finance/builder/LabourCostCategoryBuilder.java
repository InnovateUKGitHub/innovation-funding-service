package com.worth.ifs.finance.builder;

import com.worth.ifs.finance.resource.category.LabourCostCategory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class LabourCostCategoryBuilder extends AbstractFinanceRowCostCategoryBuilder<LabourCostCategory, LabourCostCategoryBuilder> {

    public static LabourCostCategoryBuilder newLabourCostCategory() {
        return new LabourCostCategoryBuilder(emptyList());
    }

    private LabourCostCategoryBuilder(List<BiConsumer<Integer, LabourCostCategory>> multiActions) {
        super(multiActions);
    }

    @Override
    protected LabourCostCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, LabourCostCategory>> actions) {
        return new LabourCostCategoryBuilder(actions);
    }

    @Override
    protected LabourCostCategory createInitial() {
        return new LabourCostCategory();
    }
}

    

