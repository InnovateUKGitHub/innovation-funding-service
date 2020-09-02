package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.category.AdditionalCompanyCostCategory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AdditionalCompanyCostCategoryBuilder extends AbstractFinanceRowCostCategoryBuilder<AdditionalCompanyCostCategory, AdditionalCompanyCostCategoryBuilder> {

    public static AdditionalCompanyCostCategoryBuilder newAdditionalCompanyCostCategory() {
        return new AdditionalCompanyCostCategoryBuilder(emptyList());
    }

    private AdditionalCompanyCostCategoryBuilder(List<BiConsumer<Integer, AdditionalCompanyCostCategory>> multiActions) {
        super(multiActions);
    }

    @Override
    protected AdditionalCompanyCostCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AdditionalCompanyCostCategory>> actions) {
        return new AdditionalCompanyCostCategoryBuilder(actions);
    }

    @Override
    protected AdditionalCompanyCostCategory createInitial() {
        return new AdditionalCompanyCostCategory();
    }
}

    

