package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.finance.resource.CostCategoryGroupResource;
import org.innovateuk.ifs.project.finance.resource.CostCategoryResource;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CostCategoryGroupResourceBuilder extends BaseBuilder<CostCategoryGroupResource, CostCategoryGroupResourceBuilder> {

    private CostCategoryGroupResourceBuilder(List<BiConsumer<Integer, CostCategoryGroupResource>> multiActions) {
        super(multiActions);
    }

    public static CostCategoryGroupResourceBuilder newCostCategoryGroupResource() {
        return new CostCategoryGroupResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CostCategoryGroupResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CostCategoryGroupResource>> actions) {
        return new CostCategoryGroupResourceBuilder(actions);
    }

    @Override
    protected CostCategoryGroupResource createInitial() {
        return new CostCategoryGroupResource();
    }

}
