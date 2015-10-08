package com.worth.ifs.application.finance.cost;

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
    public Double getTotal() {
        return 0D;
    }

    public Integer getGrantClaimPercentage() {
        return grantClaimPercentage;
    }
}
