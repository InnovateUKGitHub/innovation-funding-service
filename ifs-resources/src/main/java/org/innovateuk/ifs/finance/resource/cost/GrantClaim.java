package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;

public interface GrantClaim extends FinanceRowItem {

    void reset();

    boolean isRequestingFunding();

    int calculateClaimPercentage(BigDecimal total, BigDecimal totalOtherFunding);

    BigDecimal calculateFundingSought(BigDecimal total, BigDecimal totalOtherFunding);
}
