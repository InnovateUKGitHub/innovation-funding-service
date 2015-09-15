package com.worth.ifs.finance.domain;

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

    public boolean equals(Object object) {
        if (object instanceof CostValueId) {
            CostValueId cv = (CostValueId)object;
            return cost.equals(cv.cost) && costField.equals(cv.costField);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return cost.hashCode() + costField.hashCode();
    }
}
