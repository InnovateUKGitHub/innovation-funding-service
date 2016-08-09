package com.worth.ifs.project.finance.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Entity representing a simple Cost value
 */
@Entity
public class Cost {

    public static final byte COST_SCALE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(precision = 14, scale = COST_SCALE)
    private BigDecimal value;

    @ManyToOne
    @JoinColumn(name = "cost_group_id")
    private CostGroup costGroup;

    Cost() {
        // for ORM use
    }

    public Cost(BigDecimal value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Optional<CostGroup> getCostGroup() {
        return Optional.ofNullable(costGroup);
    }

    // for ORM backref setting
    public void setCostGroup(CostGroup costGroup) {
        this.costGroup = costGroup;
    }
}
