package com.worth.ifs.finance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.finance.resource.FinanceRowMetaValueId;

import javax.persistence.*;

/**
 * FinanceRowMetaValue defines database relations and a model to use client side and server side.
 * Holds the reference between the extra cost fields and the original cost.
 * The value is stored and the type determines how it is processed.
 */
@Entity
@IdClass(FinanceRowMetaValueId.class)
public class FinanceRowMetaValue {
    private String value;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="finance_row_id")
    private FinanceRow financeRow;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="finance_row_meta_field_id")
    private FinanceRowMetaField financeRowMetaField;

    public FinanceRowMetaValue() {
    	// no-arg constructor
    }

    public FinanceRowMetaValue(FinanceRowMetaField financeRowMetaField, String value) {
        this.financeRowMetaField = financeRowMetaField;
        this.value = value;
    }

    public FinanceRowMetaValue(FinanceRow financeRow, FinanceRowMetaField financeRowMetaField, String value) {
        this.financeRow = financeRow;
        this.financeRowMetaField = financeRowMetaField;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonIgnore
    public FinanceRow getFinanceRow() {
        return financeRow;
    }

    public FinanceRowMetaField getFinanceRowMetaField() {
        return financeRowMetaField;
    }


    public void setFinanceRow(FinanceRow financeRow) {
        this.financeRow = financeRow;
    }

    public void setFinanceRowMetaField(FinanceRowMetaField financeRowMetaField) {
        this.financeRowMetaField = financeRowMetaField;
    }

    public FinanceRowMetaValueId getId(){ return new FinanceRowMetaValueId(this.financeRow.getId(), this.financeRowMetaField.getId());}

    public void setValue(String value) {
        this.value = value;
    }
}
