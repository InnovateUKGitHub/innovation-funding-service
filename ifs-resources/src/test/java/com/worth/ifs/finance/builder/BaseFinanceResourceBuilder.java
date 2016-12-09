package com.worth.ifs.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.finance.resource.BaseFinanceResource;
import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.category.GrantClaimCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.finance.resource.cost.GrantClaim;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.finance.resource.cost.FinanceRowType.FINANCE;

/**
 * Base class Builder for building BaseFinanceResource entities.  This class holds build steps that are common to all
 * BaseFinanceResource subclasses.
 */
public abstract class BaseFinanceResourceBuilder<FinanceResourceType extends BaseFinanceResource, S extends BaseFinanceResourceBuilder<FinanceResourceType, S>>
        extends BaseBuilder<FinanceResourceType, S> {

    public S withApplication(Long... applicationIds) {
        return withArray((applicationId, applicationFinanceResource) -> setField("application", applicationId, applicationFinanceResource), applicationIds);
    }

    public S withOrganisation(Long... organisationIds) {
        return withArray((organisationId, applicationFinanceResource) -> setField("organisation", organisationId, applicationFinanceResource), organisationIds);
    }

    public S withFinanceOrganisationDetails(Map<FinanceRowType, FinanceRowCostCategory>... financeOrganisationDetails) {
        return withArray((financeOrganisationDetail, applicationFinanceResource) -> setField("financeOrganisationDetails", financeOrganisationDetail, applicationFinanceResource), financeOrganisationDetails);
    }

    public S withGrantClaimPercentage(Integer percentage) {
        return with(finance -> {
            GrantClaimCategory costCategory = new GrantClaimCategory();
            costCategory.addCost(new GrantClaim(null, percentage));
            costCategory.calculateTotal();
            finance.getFinanceOrganisationDetails().put(FINANCE, costCategory);
        });
    }

    protected BaseFinanceResourceBuilder(List<BiConsumer<Integer, FinanceResourceType>> newActions) {
        super(newActions);
    }
}
