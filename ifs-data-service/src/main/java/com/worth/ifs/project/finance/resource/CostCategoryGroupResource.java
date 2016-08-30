package com.worth.ifs.project.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class CostCategoryGroupResource {

    private Long id;

    private String description;

    private List<CostCategoryResource> costCategories = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CostCategoryResource> getCostCategories() {
        return costCategories;
    }

    public void setCostCategories(List<CostCategoryResource> costCategories) {
        this.costCategories = costCategories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CostCategoryGroupResource that = (CostCategoryGroupResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(description, that.description)
                .append(costCategories, that.costCategories)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(description)
                .toHashCode();
    }
}
