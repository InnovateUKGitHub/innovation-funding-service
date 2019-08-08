package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.category.VATCategory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class VATCategoryBuilder extends AbstractFinanceRowCostCategoryBuilder<VATCategory, VATCategoryBuilder> {

    public static VATCategoryBuilder newVATCategory() {
        return new VATCategoryBuilder(emptyList());
    }

    private VATCategoryBuilder(List<BiConsumer<Integer, VATCategory>> multiActions) {
        super(multiActions);
    }

    @Override
    protected VATCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, VATCategory>> actions) {
        return new VATCategoryBuilder(actions);
    }

    @Override
    protected VATCategory createInitial() {
        return new VATCategory();
    }

}