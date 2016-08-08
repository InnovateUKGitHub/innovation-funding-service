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
    @JoinColumn(name="cost_field_id")
    private CostField costField;

    public FinanceRowMetaValue() {
    	// no-arg constructor
    }

    public FinanceRowMetaValue(CostField costField, String value) {
        this.costField = costField;
        this.value = value;
    }

    public FinanceRowMetaValue(Cost cost, CostField costField, String value) {
        this.cost = cost;
        this.costField = costField;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonIgnore
    public Cost getCost() {
        return cost;
    }

    public CostField getCostField() {
        return costField;
    }


    public void setCost(Cost cost) {
        this.cost = cost;
    }

    public void setCostField(CostField costField) {
        this.costField = costField;
    }

    public FinanceRowMetaValueId getId(){ return new FinanceRowMetaValueId(this.cost.getId(), this.costField.getId());}

    public void setValue(String value) {
        this.value = value;
    }
}
