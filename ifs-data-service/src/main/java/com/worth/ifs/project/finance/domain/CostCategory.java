package com.worth.ifs.project.finance.domain;

import javax.persistence.*;

/**
 * Entity representing a Category against which costs can be recorded e.g. Labour, Materials etc
 */
@Entity
public class CostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "cost_category_group_id", nullable = false)
    private CostCategoryGroup costCategoryGroup;

    public CostCategory() {
    }

    public CostCategory(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public CostCategoryGroup getCostCategoryGroup() {
        return costCategoryGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // for ORM back-ref setting
    void setCostCategoryGroup(CostCategoryGroup costCategoryGroup) {
        this.costCategoryGroup = costCategoryGroup;
    }
}