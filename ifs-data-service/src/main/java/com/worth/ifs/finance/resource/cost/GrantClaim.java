package com.worth.ifs.finance.resource.cost;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;

public class GrantClaim implements CostItem {
    private Long id;
    private Integer grantClaimPercentage;
    private CostType costType;

    public GrantClaim() {
        this.costType = CostType.FINANCE;
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
        return new BigDecimal(grantClaimPercentage);
    }

    public Integer getGrantClaimPercentage() {
        return grantClaimPercentage;
    }

    @Override
    public CostType getCostType() {
        return costType;
    }
}
