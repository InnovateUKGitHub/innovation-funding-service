package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryGroup;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CostCategoryGroupBuilder extends BaseBuilder<CostCategoryGroup, CostCategoryGroupBuilder> {

    private CostCategoryGroupBuilder(List<BiConsumer<Integer, CostCategoryGroup>> multiActions) {
        super(multiActions);
    }

    public static CostCategoryGroupBuilder newCostCategoryGroup() {
        return new CostCategoryGroupBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CostCategoryGroupBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CostCategoryGroup>> actions) {
        return new CostCategoryGroupBuilder(actions);
    }

    @Override
    protected CostCategoryGroup createInitial() {
        return new CostCategoryGroup();
    }

    public CostCategoryGroupBuilder withCostCategories(List<CostCategory>... costCategories) {
        return withArray((name, costCategory) -> setField("costCategories", name, costCategory), costCategories);
    }

    public CostCategoryGroupBuilder withDescription(String... descriptions) {
        return withArray((name, description) -> setField("description", name, description), descriptions);
    }


}
