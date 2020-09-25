package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.BaseOtherFunding;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;

/**
 * {@code OtherFundingCostCategory} implementation for {@link FinanceRowCostCategory}. Retrieving the other funding
 * for an application.
 */
public class OtherFundingCostCategory extends BaseOtherFundingCostCategory {

    @Override
    protected BaseOtherFunding getFunding() {
        return new OtherFunding();
    }
}
