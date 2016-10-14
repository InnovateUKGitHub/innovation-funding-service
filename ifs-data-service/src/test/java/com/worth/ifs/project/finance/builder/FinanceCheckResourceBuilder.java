package com.worth.ifs.project.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.finance.resource.CostGroupResource;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class FinanceCheckResourceBuilder extends BaseBuilder<FinanceCheckResource, FinanceCheckResourceBuilder> {

    private FinanceCheckResourceBuilder(List<BiConsumer<Integer, FinanceCheckResource>> multiActions) {
        super(multiActions);
    }

    public static FinanceCheckResourceBuilder newFinanceCheckResource() {
        return new FinanceCheckResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected FinanceCheckResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceCheckResource>> actions) {
        return new FinanceCheckResourceBuilder(actions);
    }

    @Override
    protected FinanceCheckResource createInitial() {
        return new FinanceCheckResource();
    }

    public FinanceCheckResourceBuilder withOrganisation(Long... organisations) {
        return withArray((organisation, financeCheckResource) -> setField("organisation", organisation, financeCheckResource), organisations);
    }

    public FinanceCheckResourceBuilder withProject(Long... projects) {
        return withArray((project, financeCheckResource) -> setField("project", project, financeCheckResource), projects);
    }

    public FinanceCheckResourceBuilder withCostGroup(CostGroupResource... costGroups) {
        return withArray((costGroup, financeCheckResource) -> setField("costGroup", costGroup, financeCheckResource), costGroups);
    }
}
