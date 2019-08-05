package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;

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
}
