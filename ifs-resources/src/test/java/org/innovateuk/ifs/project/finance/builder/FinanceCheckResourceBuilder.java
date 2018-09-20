package org.innovateuk.ifs.project.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.finance.resource.CostGroupResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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
