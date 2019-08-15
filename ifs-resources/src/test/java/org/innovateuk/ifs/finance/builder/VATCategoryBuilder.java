package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.category.VatCategory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class VATCategoryBuilder extends AbstractFinanceRowCostCategoryBuilder<VatCategory, VATCategoryBuilder> {

    public static VATCategoryBuilder newVATCategory() {
        return new VATCategoryBuilder(emptyList());
    }

    private VATCategoryBuilder(List<BiConsumer<Integer, VatCategory>> multiActions) {
        super(multiActions);
    }

    @Override
    protected VATCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, VatCategory>> actions) {
        return new VATCategoryBuilder(actions);
    }

    @Override
    protected VatCategory createInitial() {
        return new VatCategory();
    }

}