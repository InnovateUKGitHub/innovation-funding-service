package com.worth.ifs.finance.resource.cost;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;

public class OtherFunding implements CostItem {
    private Long id;
    private String otherPublicFunding;
    private String fundingSource;
    private String securedDate;
    private BigDecimal fundingAmount;
    private CostType costType;

    public OtherFunding() {
        this.costType = CostType.OTHER_FUNDING;
    }

    public OtherFunding(Long id, String otherPublicFunding, String fundingSource, String securedDate, BigDecimal fundingAmount) {
        this();
        this.id = id;
        this.otherPublicFunding = otherPublicFunding;
        this.fundingSource = fundingSource;
        this.securedDate = securedDate;
        this.fundingAmount = fundingAmount;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public BigDecimal getTotal() {
        return this.fundingAmount;
    }

    public String getOtherPublicFunding() {
        return otherPublicFunding;
    }

    public String getFundingSource() {
        return fundingSource;
    }

    public String getSecuredDate() {
        return securedDate;
    }

    public BigDecimal getFundingAmount() {
        return fundingAmount;
    }

    @Override
    public CostType getCostType() {
        return costType;
    }
}
