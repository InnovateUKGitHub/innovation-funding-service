package org.innovateuk.ifs.finance.resource.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.finance.resource.cost.BaseOtherFunding;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.finance.resource.cost.PreviousFunding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.PREVIOUS_FUNDING;

/**
 * {@code PreviousFundingCostCategory} implementation for {@link FinanceRowCostCategory}. Retrieving the previous funding
 * for an application.
 */
public class PreviousFundingCostCategory  extends BaseOtherFundingCostCategory {
//    do i need
    @Override
    protected BaseOtherFunding getFunding() {
        return new PreviousFunding();
    }
}
