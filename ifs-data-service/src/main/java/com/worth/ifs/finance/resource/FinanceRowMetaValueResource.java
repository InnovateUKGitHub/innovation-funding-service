package com.worth.ifs.finance.resource;

public class FinanceRowMetaValueResource {
    String value;
    private Long cost;
    private Long financeRowMetaField;

    public FinanceRowMetaValueResource() {
    	// no-arg constructor
    }

    public FinanceRowMetaValueResource(FinanceRowMetaFieldResource financeRowMetaField, String value) {
        this.financeRowMetaField = financeRowMetaField.getId();
        this.value = value;
    }

    public FinanceRowMetaValueResource(CostResource cost, FinanceRowMetaFieldResource financeRowMetaField, String value) {
        this.cost = cost.getId();
        this.financeRowMetaField = financeRowMetaField.getId();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Long getCost() {
        return cost;
    }

    public Long getFinanceRowMetaField() {
        return financeRowMetaField;
    }


    public void setCost(CostResource cost) {
        this.cost = cost.getId();
    }

    public void setFinanceRowMetaField(FinanceRowMetaFieldResource financeRowMetaField) {
        this.financeRowMetaField = financeRowMetaField.getId();
    }

    public FinanceRowMetaValueId getId(){ return new FinanceRowMetaValueId(this.cost, this.financeRowMetaField);}

    public void setValue(String value) {
        this.value = value;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public void setFinanceRowMetaField(Long financeRowMetaField) {
        this.financeRowMetaField = financeRowMetaField;
    }
}
