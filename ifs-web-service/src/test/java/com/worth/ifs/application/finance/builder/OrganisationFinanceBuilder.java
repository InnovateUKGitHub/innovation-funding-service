package com.worth.ifs.application.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.finance.CostCategory;
import com.worth.ifs.application.finance.CostType;
import com.worth.ifs.application.finance.model.OrganisationFinance;
import com.worth.ifs.application.finance.view.OrganisationFinanceOverview;

import java.util.EnumMap;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class OrganisationFinanceBuilder extends BaseBuilder<OrganisationFinance, OrganisationFinanceBuilder> {

    private OrganisationFinanceBuilder(List<BiConsumer<Integer, OrganisationFinance>> multiActions) {
        super(multiActions);
    }

    public static OrganisationFinanceBuilder newOrganisationFinance() {
        return new OrganisationFinanceBuilder(emptyList());
    }

    @Override
    protected OrganisationFinanceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationFinance>> actions) {
        return new OrganisationFinanceBuilder(actions);
    }

    @Override
    protected OrganisationFinance createInitial() {
        return new OrganisationFinance();
    }

    public OrganisationFinanceBuilder withCostCategories(EnumMap<CostType, CostCategory> costCategories) {
        return with(organisationFinance -> setField("costCategories", costCategories, organisationFinance));
    }

    public OrganisationFinanceBuilder withGrantClaimPercentage(Integer... grantClaimPercentages) {
        return withArray((grantClaimPercentage, organisationFinance) -> setField("grantClaimPercentage", grantClaimPercentage, organisationFinance), grantClaimPercentages);
    }
}
