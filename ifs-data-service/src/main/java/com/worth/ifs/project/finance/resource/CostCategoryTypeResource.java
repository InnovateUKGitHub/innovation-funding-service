package com.worth.ifs.project.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CostCategoryTypeResource {

    private Long id;

    private String name;

    private Long costCategoryGroup;

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

    public Long getCostCategoryGroup() {
        return costCategoryGroup;
    }

    public void setCostCategoryGroup(Long costCategoryGroup) {
        this.costCategoryGroup = costCategoryGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CostCategoryTypeResource that = (CostCategoryTypeResource) o;

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
