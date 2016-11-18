package com.worth.ifs.finance.resource.cost;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;

public class GrantClaim implements FinanceRowItem {
    private Long id;

    @Digits(integer = MAX_DIGITS, fraction = 0, message = MAX_DIGITS_MESSAGE)
    private Integer grantClaimPercentage;

    private String name;

    public GrantClaim() {
        this.name = getCostType().getType();
    }

    public GrantClaim(Long id, Integer grantClaimPercentage) {
        this();
        this.id = id;
        this.grantClaimPercentage = grantClaimPercentage;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public BigDecimal getTotal() {
        if (grantClaimPercentage == null) {
            return null;
        }
        return new BigDecimal(grantClaimPercentage);
    }

    public Integer getGrantClaimPercentage() {
        return grantClaimPercentage;
    }

    public void setGrantClaimPercentage(Integer grantClaimPercentage) {
        this.grantClaimPercentage = grantClaimPercentage;
    }

    @Override
    public String getName() {
        return this.name;
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
        return FinanceRowType.FINANCE;
    }
}
