package org.innovateuk.ifs.finance.resource.cost;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

/**
 * Abstract class for holding helper methods shared by all finance row item types.
 */
public abstract class AbstractFinanceRowItem implements FinanceRowItem {

    private Long targetId;

    public AbstractFinanceRowItem(Long targetId) {
        this.targetId = targetId;
    }

    @Override
    public BigDecimal totalDiff(FinanceRowItem other){
        BigDecimal thisRate = getTotal() == null ? BigDecimal.ZERO : getTotal();
        BigDecimal otherRate = other.getTotal() == null ? BigDecimal.ZERO : other.getTotal();
        return thisRate.subtract(otherRate);
    }

    @Override
    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AbstractFinanceRowItem that = (AbstractFinanceRowItem) o;

        return new EqualsBuilder()
                .append(targetId, that.targetId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(targetId)
                .toHashCode();
    }
}
