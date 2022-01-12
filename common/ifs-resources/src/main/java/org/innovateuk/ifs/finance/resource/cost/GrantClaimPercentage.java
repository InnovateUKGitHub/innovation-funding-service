package org.innovateuk.ifs.finance.resource.cost;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

public class GrantClaimPercentage extends AbstractFinanceRowItem implements GrantClaim {
    private Long id;

    private BigDecimal percentage;

    private GrantClaimPercentage() {
        this(null);
    }

    public GrantClaimPercentage(Long targetId) {
        super(targetId);
    }

    public GrantClaimPercentage(Long id, BigDecimal percentage, Long targetId) {
        this(targetId);
        this.id = id;
        this.percentage = percentage;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    @JsonIgnore
    public BigDecimal getTotal() {
        return percentage;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    @JsonIgnore
    public Integer getIntegerPercentage() {
        if (percentage == null) {
            return null;
        }
        return percentage.intValue();
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    @Override
    @JsonIgnore
    public String getName() {
        return getCostType().getType();
    }

    @Override
    @JsonIgnore
    public boolean isEmpty() {
        return false;
    }

    @Override
    @JsonIgnore
    public FinanceRowType getCostType() {
        return FinanceRowType.FINANCE;
    }

    @Override
    public void reset() {
        percentage = null;
    }

    @Override
    public BigDecimal calculateClaimPercentage(BigDecimal total, BigDecimal totalOtherFunding) {
        return percentage == null ? BigDecimal.ZERO : percentage;
    }

    @Override
    @JsonIgnore
    public boolean isRequestingFunding() {
        return percentage != null && !percentage.equals(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal calculateFundingSought(BigDecimal total, BigDecimal otherFunding) {
        if (percentage == null) {
            return BigDecimal.ZERO;
        }
        return total.multiply(percentage)
                .divide(new BigDecimal(100))
                .subtract(otherFunding)
                .max(BigDecimal.ZERO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GrantClaimPercentage that = (GrantClaimPercentage) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(percentage, that.percentage)
                .append(getTargetId(), that.getTargetId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(percentage)
                .append(getTargetId())
                .toHashCode();
    }
}
