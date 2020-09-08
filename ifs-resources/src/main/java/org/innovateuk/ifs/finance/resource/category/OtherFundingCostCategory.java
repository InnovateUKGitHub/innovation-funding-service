package org.innovateuk.ifs.finance.resource.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.finance.resource.cost.BaseOtherFunding;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.finance.resource.cost.PreviousFunding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
