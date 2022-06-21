package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.category.HecpIndirectCostsCostCategory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class HecpIndirectCostsCategoryBuilder extends AbstractFinanceRowCostCategoryBuilder<HecpIndirectCostsCostCategory, HecpIndirectCostsCategoryBuilder> {

    public static HecpIndirectCostsCategoryBuilder newHecpIndirectCostsCostCategory() {
        return new HecpIndirectCostsCategoryBuilder(emptyList());
    }

    private HecpIndirectCostsCategoryBuilder(List<BiConsumer<Integer, HecpIndirectCostsCostCategory>> multiActions) {
        super(multiActions);
    }

    @Override
    protected HecpIndirectCostsCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, HecpIndirectCostsCostCategory>> actions) {
        return new HecpIndirectCostsCategoryBuilder(actions);
    }

    @Override
    protected HecpIndirectCostsCostCategory createInitial() {
        return new HecpIndirectCostsCostCategory();
    }
}

    

