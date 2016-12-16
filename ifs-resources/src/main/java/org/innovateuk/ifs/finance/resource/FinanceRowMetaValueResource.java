package org.innovateuk.ifs.finance.resource;

public class FinanceRowMetaValueResource {

    private Long id;
    String value;
    private Long financeRowId;
    private Long financeRowMetaField;

    public FinanceRowMetaValueResource() {
    	// no-arg constructor
    }

    public FinanceRowMetaValueResource(FinanceRowMetaFieldResource financeRowMetaField, String value) {
        this.financeRowMetaField = financeRowMetaField.getId();
        this.value = value;
    }

    public FinanceRowMetaValueResource(FinanceRowResource financeRowId, FinanceRowMetaFieldResource financeRowMetaField, String value) {
        this.financeRowId = financeRowId.getId();
        this.financeRowMetaField = financeRowMetaField.getId();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Long getFinanceRowId() {
        return financeRowId;
    }

    public Long getFinanceRowMetaField() {
        return financeRowMetaField;
    }


    public void setFinanceRowId(FinanceRowResource financeRowId) {
        this.financeRowId = financeRowId.getId();
    }

    public void setFinanceRowMetaField(FinanceRowMetaFieldResource financeRowMetaField) {
        this.financeRowMetaField = financeRowMetaField.getId();
    }

    public Long getId(){ return id;}

    public void setId(Long id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setFinanceRowId(Long financeRowId) {
        this.financeRowId = financeRowId;
    }

    public void setFinanceRowMetaField(Long financeRowMetaField) {
        this.financeRowMetaField = financeRowMetaField;
    }
}
