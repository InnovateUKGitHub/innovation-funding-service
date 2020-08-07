package org.innovateuk.ifs.finance.resource.cost;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;

//can i merge this and other funding?
public class PreviousFunding extends AbstractFinanceRowItem {
    private Long id;

//    is a string for some stupid reason, i imagine reusing a column or some rubbish
    private String receivedOtherFunding;
    private String fundingSource;
    private String securedDate;

    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal fundingAmount;

    private PreviousFunding() {
        this(null);
    }

    public PreviousFunding(Long targetId) {
        super(targetId);
    }

    public PreviousFunding(Long id, String receivedOtherFunding, String fundingSource, String securedDate, BigDecimal fundingAmount, Long targetId) {
        this(targetId);
        this.id = id;
        this.receivedOtherFunding = receivedOtherFunding;
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
    public boolean isEmpty() {
        return (StringUtils.isBlank(fundingSource) && StringUtils.isBlank(securedDate) && (fundingAmount == null || fundingAmount.compareTo(BigDecimal.ZERO) == 0));
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getReceivedOtherFunding() {
        return receivedOtherFunding;
    }

    public void setReceivedOtherFunding(String receivedOtherFunding) {
        this.receivedOtherFunding = receivedOtherFunding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PreviousFunding that = (PreviousFunding) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(receivedOtherFunding, that.receivedOtherFunding)
                .append(fundingSource, that.fundingSource)
                .append(securedDate, that.securedDate)
                .append(fundingAmount, that.fundingAmount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(receivedOtherFunding)
                .append(fundingSource)
                .append(securedDate)
                .append(fundingAmount)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("receivedOtherFunding", receivedOtherFunding)
                .append("fundingSource", fundingSource)
                .append("securedDate", securedDate)
                .append("fundingAmount", fundingAmount)
                .toString();
    }
}
