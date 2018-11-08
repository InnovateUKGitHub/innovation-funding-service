package org.innovateuk.ifs.finance.resource.cost;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;

public class GrantClaim extends AbstractFinanceRowItem {
    private Long id;

    @Digits(integer = MAX_DIGITS, fraction = 0, message = MAX_DIGITS_MESSAGE)
    private Integer grantClaimPercentage;

    private String name;

    public GrantClaim() {
        this.name = getCostType().getType();
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
        if (grantClaimPercentage == null) {
            return null;
        }
        return new BigDecimal(grantClaimPercentage);
    }

    public Integer getGrantClaimPercentage() {
        return grantClaimPercentage;
    }

    public void setGrantClaimPercentage(Integer grantClaimPercentage) {
        this.grantClaimPercentage = grantClaimPercentage;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getMinRows() {
        return 0;
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.FINANCE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GrantClaim that = (GrantClaim) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(grantClaimPercentage, that.grantClaimPercentage)
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(grantClaimPercentage)
                .append(name)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("grantClaimPercentage", grantClaimPercentage)
                .append("name", name)
                .toString();
    }
}
