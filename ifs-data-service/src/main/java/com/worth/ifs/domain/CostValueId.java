package com.worth.ifs.domain;

import java.io.Serializable;

public class CostValueId implements Serializable {
    private Cost cost;
    private CostField costField;

    public CostValueId() {

    }

    public CostValueId(Cost cost, CostField costField) {
        this.cost = cost;
        this.costField = costField;
    }

    public Cost getCost() {
        return cost;
    }

    public CostField getCostField() {
        return costField;
    }

    public boolean equals(Object object) {
        if (object instanceof CostValueId) {
            CostValueId cv = (CostValueId)object;
            return cost.equals(cv.cost) && costField == cv.costField;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return cost.hashCode() + costField.hashCode();
    }
}
