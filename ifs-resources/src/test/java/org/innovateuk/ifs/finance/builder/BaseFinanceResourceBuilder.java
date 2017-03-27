package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.GrantClaimCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FINANCE;

/**
 * Base class Builder for building BaseFinanceResource entities.  This class holds build steps that are common to all
 * BaseFinanceResource subclasses.
 */
public abstract class BaseFinanceResourceBuilder<FinanceResourceType extends BaseFinanceResource, S extends BaseFinanceResourceBuilder<FinanceResourceType, S>>
        extends BaseBuilder<FinanceResourceType, S> {

    public S withOrganisation(Long... organisationIds) {
        return withArray((organisationId, applicationFinanceResource) -> setField("organisation", organisationId, applicationFinanceResource), organisationIds);
    }

    public S withOrganisationSize(Long... value) {
        return withArray((v, finance) -> finance.setOrganisationSize(v), value);
    }

    public S withFinanceOrganisationDetails(Map<FinanceRowType, FinanceRowCostCategory>... financeOrganisationDetails) {
        return withArray((financeOrganisationDetail, finance) -> setField("financeOrganisationDetails", financeOrganisationDetail, finance), financeOrganisationDetails);
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
