package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.category.VatCostCategory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class VATCategoryBuilder extends AbstractFinanceRowCostCategoryBuilder<VatCostCategory, VATCategoryBuilder> {

    public static VATCategoryBuilder newVATCategory() {
        return new VATCategoryBuilder(emptyList());
    }

    private VATCategoryBuilder(List<BiConsumer<Integer, VatCostCategory>> multiActions) {
        super(multiActions);
    }

    @Override
    protected VATCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, VatCostCategory>> actions) {
        return new VATCategoryBuilder(actions);
    }

    @Override
    protected VatCostCategory createInitial() {
        return new VatCostCategory();
    }

}