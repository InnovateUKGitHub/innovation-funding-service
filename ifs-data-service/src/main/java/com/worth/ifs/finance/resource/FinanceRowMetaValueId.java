package com.worth.ifs.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class FinanceRowMetaValueId implements Serializable {
    private Long cost;
    private Long financeRowMetaField;

    public FinanceRowMetaValueId() {
    	// no-arg constructor
    }

    public FinanceRowMetaValueId(Long cost, Long financeRowMetaField) {
        this.cost = cost;
        this.financeRowMetaField = financeRowMetaField;
    }

    public Long getCost() {
        return cost;
    }

    public Long getFinanceRowMetaField() {
        return financeRowMetaField;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        FinanceRowMetaValueId rhs = (FinanceRowMetaValueId) obj;
        return new EqualsBuilder()
            .append(this.cost, rhs.cost)
            .append(this.financeRowMetaField, rhs.financeRowMetaField)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(cost)
            .append(financeRowMetaField)
            .toHashCode();
    }
}
