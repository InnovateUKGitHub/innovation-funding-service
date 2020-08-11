package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;

public class PreviousFunding extends BaseOtherFunding {

    public PreviousFunding() {
        this(null);
    }

    public PreviousFunding(Long targetId) {
        super(targetId);
    }

    public PreviousFunding(Long id, String otherPublicFunding, String fundingSource, String securedDate, BigDecimal fundingAmount, Long targetId) {
        super(id, otherPublicFunding, fundingSource, securedDate, fundingAmount, targetId);
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.PREVIOUS_FUNDING;
    }
}
