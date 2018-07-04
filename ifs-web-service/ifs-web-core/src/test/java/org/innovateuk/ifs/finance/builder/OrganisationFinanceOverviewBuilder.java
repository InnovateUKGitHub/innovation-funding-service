package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.finance.view.OrganisationFinanceOverview;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class OrganisationFinanceOverviewBuilder extends BaseBuilder<OrganisationFinanceOverview, OrganisationFinanceOverviewBuilder> {

    private OrganisationFinanceOverviewBuilder(List<BiConsumer<Integer, OrganisationFinanceOverview>> multiActions) {
        super(multiActions);
    }

    public static OrganisationFinanceOverviewBuilder newOrganisationFinanceOverviewBuilder() {
        return new OrganisationFinanceOverviewBuilder(emptyList());
    }

    @Override
    protected OrganisationFinanceOverviewBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationFinanceOverview>> actions) {
        return new OrganisationFinanceOverviewBuilder(actions);
    }

    @Override
    protected OrganisationFinanceOverview createInitial() {
        return new OrganisationApplicationFinanceOverviewImpl();
    }

    public OrganisationFinanceOverviewBuilder withApplicationId(Long... applicationIds) {
        return withArray((id, organisationFinanceOverview) -> setField("applicationId", id, organisationFinanceOverview), applicationIds);
    }

    public OrganisationFinanceOverviewBuilder withOrganisationFinances(ApplicationFinanceResource... applicationFinances) {
        return withOrganisationFinances(asList(applicationFinances));
    }

    public OrganisationFinanceOverviewBuilder withOrganisationFinances(List<ApplicationFinanceResource> applicationFinances) {
        return with(organisationFinanceOverview -> setField("applicationFinances", applicationFinances, organisationFinanceOverview));
    }
}
