package com.worth.ifs.project.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CostCategoryResource {

    private Long id;

    private String name;

    private CostCategoryGroupResource costCategoryGroup;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CostCategoryGroupResource getCostCategoryGroup() {
        return costCategoryGroup;
    }

    public void setCostCategoryGroup(CostCategoryGroupResource costCategoryGroup) {
        this.costCategoryGroup = costCategoryGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CostCategoryResource that = (CostCategoryResource) o;

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
