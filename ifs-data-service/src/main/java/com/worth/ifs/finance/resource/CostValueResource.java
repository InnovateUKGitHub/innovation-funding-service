package com.worth.ifs.finance.resource;

public class CostValueResource {
    String value;
    private Long cost;
    private Long costField;

    public CostValueResource() {
    	// no-arg constructor
    }

    public CostValueResource(CostFieldResource costField, String value) {
        this.costField = costField.getId();
        this.value = value;
    }

    public CostValueResource(CostResource cost, CostFieldResource costField, String value) {
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

    public CostValueId getId(){ return new CostValueId(this.cost, this.costField);}

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
