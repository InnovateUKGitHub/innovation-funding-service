package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Holds the reference between the extra cost fields and the original cost.
 * The value is stored and the type determines how it is processed.
 */
@Entity
@IdClass(CostValueId.class)
public class CostValue implements Serializable {
    String value;

    @Id
    @ManyToOne
    @PrimaryKeyJoinColumn(name="cost_id", referencedColumnName="id")
    private Cost cost;

    @Id
    @ManyToOne
    @PrimaryKeyJoinColumn(name="cost_field_id", referencedColumnName="id")
    private CostField costField;

    public CostValue() {
    }

    public CostValue(CostField costField, String value) {
        this.costField = costField;
        this.value = value;
    }

    public CostValue(Cost cost, CostField costField, String value) {
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
}
