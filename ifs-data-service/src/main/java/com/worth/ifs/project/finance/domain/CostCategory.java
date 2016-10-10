package com.worth.ifs.project.finance.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @ManyToOne(fetch = FetchType.LAZY)
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
    public void setCostCategoryGroup(CostCategoryGroup costCategoryGroup) {
        this.costCategoryGroup = costCategoryGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CostCategory that = (CostCategory) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(costCategoryGroup, that.costCategoryGroup)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .toHashCode();
    }
}