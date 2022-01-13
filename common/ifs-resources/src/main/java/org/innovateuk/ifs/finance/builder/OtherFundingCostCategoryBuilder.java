package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class OtherFundingCostCategoryBuilder extends AbstractFinanceRowCostCategoryBuilder<OtherFundingCostCategory, OtherFundingCostCategoryBuilder> {

    public static OtherFundingCostCategoryBuilder newOtherFundingCostCategory() {
        return new OtherFundingCostCategoryBuilder(emptyList());
    }

    private OtherFundingCostCategoryBuilder(List<BiConsumer<Integer, OtherFundingCostCategory>> multiActions) {
        super(multiActions);
    }

    @Override
    protected OtherFundingCostCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OtherFundingCostCategory>> actions) {
        return new OtherFundingCostCategoryBuilder(actions);
    }

    @Override
    protected OtherFundingCostCategory createInitial() {
        return new OtherFundingCostCategory();
    }
}
