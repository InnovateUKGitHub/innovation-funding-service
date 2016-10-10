package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.finance.domain.CostGroup;
import com.worth.ifs.project.finance.domain.FinanceCheck;
import com.worth.ifs.project.finance.resource.CostGroupResource;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.user.domain.Organisation;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class FinanceCheckBuilder extends BaseBuilder<FinanceCheck, FinanceCheckBuilder> {

    private FinanceCheckBuilder(List<BiConsumer<Integer, FinanceCheck>> multiActions) {
        super(multiActions);
    }

    public static FinanceCheckBuilder newFinanceCheck() {
        return new FinanceCheckBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected FinanceCheckBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceCheck>> actions) {
        return new FinanceCheckBuilder(actions);
    }

    @Override
    protected FinanceCheck createInitial() {
        return new FinanceCheck();
    }

    public FinanceCheckBuilder withOrganisation(Organisation... organisations) {
        return withArray((organisation, financeCheckResource) -> setField("organisation", organisation, financeCheckResource), organisations);
    }

    public FinanceCheckBuilder withProject(Project... projects) {
        return withArray((project, financeCheckResource) -> setField("project", project, financeCheckResource), projects);
    }

    public FinanceCheckBuilder withCostGroup(CostGroup... costGroups) {
        return withArray((costGroup, financeCheckResource) -> setField("costGroup", costGroup, financeCheckResource), costGroups);
    }
}
