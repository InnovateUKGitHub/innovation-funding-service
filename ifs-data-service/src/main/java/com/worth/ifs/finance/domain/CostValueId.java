package com.worth.ifs.finance.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CostValueId implements Serializable {
    private Long cost;
    private Long costField;

    public CostValueId() {

    }

    public CostValueId(Long cost, Long costField) {
        this.cost = cost;
        this.costField = costField;
    }

    public Long getCost() {
        return cost;
    }

    public Long getCostField() {
        return costField;
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
        CostValueId rhs = (CostValueId) obj;
        return new EqualsBuilder()
            .append(this.cost, rhs.cost)
            .append(this.costField, rhs.costField)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(cost)
            .append(costField)
            .toHashCode();
    }
}
