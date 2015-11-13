package com.worth.ifs.application.finance.cost;

import java.math.BigDecimal;

public class GrantClaim implements CostItem {
    private Long id;
    private Integer grantClaimPercentage;

    public GrantClaim(Long id, Integer grantClaimPercentage) {
        this.id = id;
        this.grantClaimPercentage = grantClaimPercentage;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public BigDecimal getTotal() {
        return new BigDecimal(0);
    }

    public Integer getGrantClaimPercentage() {
        return grantClaimPercentage;
    }
}
