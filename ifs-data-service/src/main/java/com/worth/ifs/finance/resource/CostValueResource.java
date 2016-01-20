package com.worth.ifs.finance.resource;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.domain.CostValueId;


public class CostValueResource {
    String value;
    private Long cost;
    private Long costField;

    public CostValueResource() {
    }

    public CostValueResource(CostField costField, String value) {
        this.costField = costField.getId();
        this.value = value;
    }

    public CostValueResource(Cost cost, CostField costField, String value) {
        this.cost = cost.getId();
        this.costField = costField.getId();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Long getCost() {
        return cost;
    }

    public Long getCostField() {
        return costField;
    }


    public void setCost(Cost cost) {
        this.cost = cost.getId();
    }

    public void setCostField(CostField costField) {
        this.costField = costField.getId();
    }

    public CostValueId getId(){ return new CostValueId(this.cost, this.costField);}
}
