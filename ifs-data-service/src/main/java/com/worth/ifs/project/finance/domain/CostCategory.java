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

    @Column(nullable = true)
    private String label;

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

    public String getIdString() {
        return String.valueOf(id);
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    // for ORM back-ref setting
    public void setCostCategoryGroup(CostCategoryGroup costCategoryGroup) {
        this.costCategoryGroup = costCategoryGroup;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CostCategory that = (CostCategory) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        return costCategoryGroup != null ? costCategoryGroup.equals(that.costCategoryGroup) : that.costCategoryGroup == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (costCategoryGroup != null ? costCategoryGroup.hashCode() : 0);
        return result;
    }
}