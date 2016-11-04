package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.category.GrantClaimCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.finance.resource.cost.GrantClaim;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static com.worth.ifs.finance.resource.cost.FinanceRowType.FINANCE;
import static java.util.Collections.emptyList;

/**
 * Builder for ApplicationFinance entities.
 */
public class ApplicationFinanceResourceBuilder extends BaseBuilder<ApplicationFinanceResource, ApplicationFinanceResourceBuilder> {

    private ApplicationFinanceResourceBuilder(List<BiConsumer<Integer, ApplicationFinanceResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static ApplicationFinanceResourceBuilder newApplicationFinanceResource() {
        return new ApplicationFinanceResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ApplicationFinanceResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationFinanceResource>> actions) {
        return new ApplicationFinanceResourceBuilder(actions);
    }

    @Override
    protected ApplicationFinanceResource createInitial() {
        return new ApplicationFinanceResource();
    }

    public ApplicationFinanceResourceBuilder withApplication(Long... applicationIds) {
        return withArray((applicationId, applicationFinanceResource) -> setField("application", applicationId, applicationFinanceResource), applicationIds);
    }

    public ApplicationFinanceResourceBuilder withOrganisation(Long... organisationIds) {
        return withArray((organisationId, applicationFinanceResource) -> setField("organisation", organisationId, applicationFinanceResource), organisationIds);
    }

    public ApplicationFinanceResourceBuilder withFinanceOrganisationDetails(Map<FinanceRowType, FinanceRowCostCategory>... financeOrganisationDetails) {
        return withArray((financeOrganisationDetail, applicationFinanceResource) -> setField("financeOrganisationDetails", financeOrganisationDetail, applicationFinanceResource), financeOrganisationDetails);
    }

    public ApplicationFinanceResourceBuilder withFinanceFileEntry(Long financeFileEntry) {
        return with(finance -> finance.setFinanceFileEntry(financeFileEntry));
    }

    public ApplicationFinanceResourceBuilder withGrantClaimPercentage(Integer percentage) {
        return with(finance -> {
            GrantClaimCategory costCategory = new GrantClaimCategory();
            costCategory.addCost(new GrantClaim(null, percentage));
            costCategory.calculateTotal();
            finance.getFinanceOrganisationDetails().put(FINANCE, costCategory);
        });
    }
}
