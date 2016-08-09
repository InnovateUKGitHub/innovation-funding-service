package com.worth.ifs.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class FinanceRowMetaValueId implements Serializable {
    private Long financeRow;
    private Long financeRowMetaField;

    public FinanceRowMetaValueId() {
    	// no-arg constructor
    }

    public FinanceRowMetaValueId(Long financeRow, Long financeRowMetaField) {
        this.financeRow = financeRow;
        this.financeRowMetaField = financeRowMetaField;
    }

    public Long getFinanceRow() {
        return financeRow;
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
            .append(this.financeRow, rhs.financeRow)
            .append(this.financeRowMetaField, rhs.financeRowMetaField)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(financeRow)
            .append(financeRowMetaField)
            .toHashCode();
    }
}
