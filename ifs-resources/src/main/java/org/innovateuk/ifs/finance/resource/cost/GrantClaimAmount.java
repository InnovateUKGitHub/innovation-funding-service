package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GrantClaimAmount extends AbstractFinanceRowItem implements GrantClaim {
    private Long id;
    private BigDecimal amount;

    private GrantClaimAmount() {
        this(null);
    }

    public GrantClaimAmount(Long targetId) {
        super(targetId);
    }

    public GrantClaimAmount(Long id, BigDecimal amount, Long targetId) {
        this(targetId);
        this.id = id;
        this.amount = amount;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public BigDecimal getTotal() {
        if (amount == null) {
            return null;
        }
        return amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String getName() {
        return getCostType().getType();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getMinRows() {
        return 0;
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.GRANT_CLAIM_AMOUNT;
    }

    @Override
    public void reset() {
        amount = null;
    }

    @Override
    public Integer calculateClaimPercentage(BigDecimal total) {
        if (amount == null) {
            return null;
        }
        if (total.equals(BigDecimal.ZERO)) {
            return 0;
        }
        return amount.multiply(new BigDecimal(100))
                .divide(total, RoundingMode.HALF_UP)
                .intValue();
    }

    @Override
    public boolean isRequestingFunding() {
        return amount != null && !amount.equals(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal calculateGrantClaimAmount(BigDecimal total) {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
