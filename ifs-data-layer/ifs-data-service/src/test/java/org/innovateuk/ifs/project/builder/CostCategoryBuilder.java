package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryGroup;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CostCategoryBuilder extends BaseBuilder<CostCategory, CostCategoryBuilder> {

    private CostCategoryBuilder(List<BiConsumer<Integer, CostCategory>> multiActions) {
        super(multiActions);
    }

    public static CostCategoryBuilder newCostCategory() {
        return new CostCategoryBuilder(emptyList()).with(uniqueIds());
    }

    public CostCategoryBuilder withName(String... names) {
        return withArray((name, costCategory) -> setField("name", name, costCategory), names);
    }

    public CostCategoryBuilder withLabel(String... labels) {
        return withArray((label, costCategory) -> setField("label", label, costCategory), labels);
    }

    public CostCategoryBuilder withCostCategoryGroup(CostCategoryGroup... value) {
        return withArray((v, costCategory) -> costCategory.setCostCategoryGroup(v), value);
    }

    @Override
    protected CostCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CostCategory>> actions) {
        return new CostCategoryBuilder(actions);
    }

    @Override
    protected CostCategory createInitial() {
        return new CostCategory();
    }

}
