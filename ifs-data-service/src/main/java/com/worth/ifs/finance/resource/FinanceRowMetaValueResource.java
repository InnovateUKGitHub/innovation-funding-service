package com.worth.ifs.finance.resource;

public class FinanceRowMetaValueResource {
    String value;
    private Long cost;
    private Long costField;

    public FinanceRowMetaValueResource() {
    	// no-arg constructor
    }

    public FinanceRowMetaValueResource(CostFieldResource costField, String value) {
        this.costField = costField.getId();
        this.value = value;
    }

    public FinanceRowMetaValueResource(CostResource cost, CostFieldResource costField, String value) {
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


    public void setCost(CostResource cost) {
        this.cost = cost.getId();
    }

    public void setCostField(CostFieldResource costField) {
        this.costField = costField.getId();
    }

    public FinanceRowMetaValueId getId(){ return new FinanceRowMetaValueId(this.cost, this.costField);}

    public void setValue(String value) {
        this.value = value;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public void setCostField(Long costField) {
        this.costField = costField;
    }
}
