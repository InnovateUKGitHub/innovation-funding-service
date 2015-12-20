package com.worth.ifs.application.finance.cost;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OtherFunding implements CostItem {
    private Long id;
    private String otherPublicFunding;
    private String fundingSource;
    private String securedDateMonth;
    private String securedDateYear;
    private BigDecimal fundingAmount;

    public OtherFunding() {
    }

    public OtherFunding(Long id, String otherPublicFunding, String fundingSource, String securedDateMonth, String securedDateYear, BigDecimal fundingAmount) {
        this.id = id;
        this.otherPublicFunding = otherPublicFunding;
        this.fundingSource = fundingSource;
        this.securedDateMonth = securedDateMonth;
        this.securedDateYear = securedDateYear;
        this.fundingAmount = fundingAmount;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public BigDecimal getTotal() {
        return BigDecimal.ZERO;
    }

    public String getOtherPublicFunding() {
        return otherPublicFunding;
    }

    public String getFundingSource() {
        return fundingSource;
    }

    public String getSecuredDateYear() {
        return securedDateYear;
    }

    public String getSecuredDateMonth() {
        return securedDateMonth;
    }

    public BigDecimal getFundingAmount() {
        return fundingAmount;
    }
}
