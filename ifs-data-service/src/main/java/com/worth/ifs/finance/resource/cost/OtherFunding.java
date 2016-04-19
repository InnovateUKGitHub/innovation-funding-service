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
}
