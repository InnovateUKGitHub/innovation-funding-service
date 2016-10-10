package com.worth.ifs.project.finance.domain;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.CascadeType.ALL;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cost_group_id")
    private CostGroup costGroup;

    @OneToOne(cascade = ALL, mappedBy = "cost", orphanRemoval = true)
    private CostTimePeriod costTimePeriod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cost_categorization",
            joinColumns = @JoinColumn(name = "cost_id"),
            inverseJoinColumns = @JoinColumn(name = "cost_category_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"cost_id", "cost_category_id"})
    )
    @OrderColumn(name = "priority")
    private CostCategory costCategory;

    public Cost() {
        // for ORM use
    }

    // copy constructor
    private Cost(Cost cost) {
        this(cost.id, cost.value, cost.costGroup, cost.costTimePeriod, cost.costCategory);
    }

    // for use with copy constructor
    private Cost(Long id, BigDecimal value, CostGroup costGroup, CostTimePeriod costTimePeriod, CostCategory costCategory) {
        this.id = id;
        this.value = value;
        this.costGroup = costGroup;
        this.costTimePeriod = costTimePeriod;
        this.costCategory = costCategory;
    }

    public Cost(String value) {
        this(new BigDecimal(value));
    }

    public Cost(BigDecimal value) {
        this.value = value;
    }

    public Cost withCategory(CostCategory category) {
        Cost copy = new Cost(this);
        copy.setCostCategory(category);
        return copy;
    }

    public Cost withTimePeriod(Integer offsetAmount, TimeUnit offsetUnit, Integer durationAmount, TimeUnit durationUnit) {
        Cost copy = new Cost(this);
        copy.setCostTimePeriod(new CostTimePeriod(offsetAmount, offsetUnit, durationAmount, durationUnit));
        return copy;
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

    public void setValue(String value) {
        setValue(new BigDecimal(value));
    }

    public CostGroup getCostGroup() {
        return costGroup;
    }

    // for ORM backref setting
    public void setCostGroup(CostGroup costGroup) {
        this.costGroup = costGroup;
    }

    // for the mapper
    public CostTimePeriod getCostTimePeriod() {
        return costTimePeriod;
    }

    // for ORM backref setting
    public void setCostTimePeriod(CostTimePeriod costTimePeriod) {
        this.costTimePeriod = costTimePeriod;
        this.costTimePeriod.setCost(this);
    }

    public CostCategory getCostCategory() {
        return costCategory;
    }

    // for ORM backref setting
    public void setCostCategory(CostCategory costCategory) {
        this.costCategory = costCategory;
    }
}
