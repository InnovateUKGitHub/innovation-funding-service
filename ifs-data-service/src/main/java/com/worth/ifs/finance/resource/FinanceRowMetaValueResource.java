package com.worth.ifs.finance.resource;

public class FinanceRowMetaValueResource {
    String value;
    private Long financeRow;
    private Long financeRowMetaField;

    public FinanceRowMetaValueResource() {
    	// no-arg constructor
    }

    public FinanceRowMetaValueResource(FinanceRowMetaFieldResource financeRowMetaField, String value) {
        this.financeRowMetaField = financeRowMetaField.getId();
        this.value = value;
    }

    public FinanceRowMetaValueResource(FinanceRowResource financeRow, FinanceRowMetaFieldResource financeRowMetaField, String value) {
        this.financeRow = financeRow.getId();
        this.financeRowMetaField = financeRowMetaField.getId();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Long getFinanceRow() {
        return financeRow;
    }

    public Long getFinanceRowMetaField() {
        return financeRowMetaField;
    }


    public void setFinanceRow(FinanceRowResource financeRow) {
        this.financeRow = financeRow.getId();
    }

    public void setFinanceRowMetaField(FinanceRowMetaFieldResource financeRowMetaField) {
        this.financeRowMetaField = financeRowMetaField.getId();
    }

    public FinanceRowMetaValueId getId(){ return new FinanceRowMetaValueId(this.financeRow, this.financeRowMetaField);}

    public void setValue(String value) {
        this.value = value;
    }

    public void setFinanceRow(Long financeRow) {
        this.financeRow = financeRow;
    }

    public void setFinanceRowMetaField(Long financeRowMetaField) {
        this.financeRowMetaField = financeRowMetaField;
    }
}
