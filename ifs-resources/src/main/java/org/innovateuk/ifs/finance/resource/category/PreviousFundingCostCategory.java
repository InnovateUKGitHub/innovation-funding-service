package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.BaseOtherFunding;
import org.innovateuk.ifs.finance.resource.cost.PreviousFunding;

/**
 * {@code PreviousFundingCostCategory} implementation for {@link FinanceRowCostCategory}. Retrieving the previous funding
 * for an application.
 */
public class PreviousFundingCostCategory extends BaseOtherFundingCostCategory {
    @Override
    protected BaseOtherFunding getFunding() {
        return new PreviousFunding();
    }
}
