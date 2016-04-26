package com.worth.ifs.finance.resource.cost;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class OtherFunding implements CostItem {
    private Long id;

    @NotBlank
    private String otherPublicFunding; // the date
    @NotBlank
    private String fundingSource;
    @NotBlank
    private String securedDate;

    @NotNull
    @DecimalMin(value = "0")
    @Digits(integer = MAX_DIGITS, fraction = 0)
    private BigDecimal fundingAmount;

    private CostType costType;
    private String name;

    public OtherFunding() {
        this.costType = CostType.OTHER_FUNDING;
        this.name = this.costType.getType();
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEmpty() {
        if((fundingSource.isEmpty() && securedDate.isEmpty() && fundingAmount.compareTo(BigDecimal.ZERO) == 0)){
            return true;
        }
        return false;
    }

    @Override
    public int getMinRows() {
        return 1;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOtherPublicFunding(String otherPublicFunding) {
        this.otherPublicFunding = otherPublicFunding;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource;
    }

    public void setSecuredDate(String securedDate) {
        this.securedDate = securedDate;
    }

    public void setFundingAmount(BigDecimal fundingAmount) {
        this.fundingAmount = fundingAmount;
    }

    public void setCostType(CostType costType) {
        this.costType = costType;
    }

    public void setName(String name) {
        this.name = name;
    }
}
