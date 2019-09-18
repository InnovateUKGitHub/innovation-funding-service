package org.innovateuk.ifs.finance.resource.cost;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;

public class OtherFunding extends AbstractFinanceRowItem {
    private Long id;

    private String otherPublicFunding;
    private String fundingSource;
    private String securedDate;

    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal fundingAmount;

    private OtherFunding() {
        this(null);
    }

    public OtherFunding(Long targetId) {
        super(targetId);
    }

    public OtherFunding(Long id, String otherPublicFunding, String fundingSource, String securedDate, BigDecimal fundingAmount, Long targetId) {
        this(targetId);
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
    public FinanceRowType getCostType() {
        return FinanceRowType.OTHER_FUNDING;
    }

    @Override
    public String getName() {
        return getCostType().getType();
    }

    @Override
    public boolean excludeInRowCount() {
        return (OtherFundingCostCategory.OTHER_FUNDING.equals(fundingSource) || isEmpty());
    }

    @Override
    public boolean isEmpty() {
        return (StringUtils.isBlank(fundingSource) && StringUtils.isBlank(securedDate) && (fundingAmount == null || fundingAmount.compareTo(BigDecimal.ZERO) == 0));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OtherFunding that = (OtherFunding) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(otherPublicFunding, that.otherPublicFunding)
                .append(fundingSource, that.fundingSource)
                .append(securedDate, that.securedDate)
                .append(fundingAmount, that.fundingAmount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(otherPublicFunding)
                .append(fundingSource)
                .append(securedDate)
                .append(fundingAmount)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("otherPublicFunding", otherPublicFunding)
                .append("fundingSource", fundingSource)
                .append("securedDate", securedDate)
                .append("fundingAmount", fundingAmount)
                .toString();
    }
}
