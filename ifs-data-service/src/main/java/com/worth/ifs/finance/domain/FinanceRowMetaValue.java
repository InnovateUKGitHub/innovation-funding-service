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
    @ManyToOne
    @JoinColumn(name="cost_id")
    private Cost cost;

    @Id
    @ManyToOne
    @JoinColumn(name="finance_row_meta_field_id")
    private FinanceRowMetaField financeRowMetaField;

    public FinanceRowMetaValue() {
    	// no-arg constructor
    }

    public FinanceRowMetaValue(FinanceRowMetaField financeRowMetaField, String value) {
        this.financeRowMetaField = financeRowMetaField;
        this.value = value;
    }

    public FinanceRowMetaValue(Cost cost, FinanceRowMetaField financeRowMetaField, String value) {
        this.cost = cost;
        this.financeRowMetaField = financeRowMetaField;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonIgnore
    public Cost getCost() {
        return cost;
    }

    public FinanceRowMetaField getFinanceRowMetaField() {
        return financeRowMetaField;
    }


    public void setCost(Cost cost) {
        this.cost = cost;
    }

    public void setFinanceRowMetaField(FinanceRowMetaField financeRowMetaField) {
        this.financeRowMetaField = financeRowMetaField;
    }

    public FinanceRowMetaValueId getId(){ return new FinanceRowMetaValueId(this.cost.getId(), this.financeRowMetaField.getId());}

    public void setValue(String value) {
        this.value = value;
    }
}
