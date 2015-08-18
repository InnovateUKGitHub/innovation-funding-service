package com.worth.ifs.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Holds the reference between the extra cost fields and the original cost.
 * The value is stored and the type determines how it is processed.
 */
@Entity
public class CostValue implements Serializable {
    String value;

    @Id
    @Column(name="cost_id")
    private Long costId;

    @Id
    @Column(name="cost_field_id")
    private Long costFieldId;

    @ManyToOne
    @PrimaryKeyJoinColumn(name="costId", referencedColumnName="id")
    private Cost cost;

    @ManyToOne
    @PrimaryKeyJoinColumn(name="costFieldId", referencedColumnName="id")
    private CostField costField;

    public CostValue(String value) {
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

    public Cost getCost() {
        return cost;
    }

    public CostField getCostField() {
        return costField;
    }
}
