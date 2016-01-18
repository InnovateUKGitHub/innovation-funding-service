package com.worth.ifs.application.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.finance.view.OrganisationFinanceOverview;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
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

    public OrganisationFinanceOverviewBuilder withOrganisationFinances(OrganisationFinance... organisationFinances) {
        return withOrganisationFinances(asList(organisationFinances));
    }

    public OrganisationFinanceOverviewBuilder withOrganisationFinances(List<OrganisationFinance> organisationFinances) {
        return with(organisationFinanceOverview -> setField("organisationFinances", organisationFinances, organisationFinanceOverview));
    }
}
