package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.finance.view.OrganisationFinanceOverview;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

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
        return new OrganisationFinanceOverview();
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
