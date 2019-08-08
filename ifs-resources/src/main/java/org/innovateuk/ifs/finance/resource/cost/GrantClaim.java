package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;

public interface GrantClaim extends FinanceRowItem {

    void reset();

    Integer calculateClaimPercentage(BigDecimal total);

    BigDecimal calculateGrantClaimAmount(BigDecimal total);

    boolean isRequestingFunding();
}
