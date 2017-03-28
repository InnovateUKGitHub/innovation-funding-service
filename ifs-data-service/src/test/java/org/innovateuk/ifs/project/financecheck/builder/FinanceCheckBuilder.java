package org.innovateuk.ifs.project.financecheck.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.financecheck.domain.CostGroup;
import org.innovateuk.ifs.project.financecheck.domain.FinanceCheck;
import org.innovateuk.ifs.user.domain.Organisation;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class FinanceCheckBuilder extends BaseBuilder<FinanceCheck, FinanceCheckBuilder> {

    private FinanceCheckBuilder(List<BiConsumer<Integer, FinanceCheck>> multiActions) {
        super(multiActions);
    }

    public static FinanceCheckBuilder newFinanceCheck() {
        return new FinanceCheckBuilder(emptyList()).with(uniqueIds());
    }

    public FinanceCheckBuilder withId(Long... id) {
        return withArraySetFieldByReflection("id", id);
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

    @Override
    protected FinanceCheckBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceCheck>> actions) {
        return new FinanceCheckBuilder(actions);
    }

    @Override
    protected FinanceCheck createInitial() {
        return new FinanceCheck();
    }

}
