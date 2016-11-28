package com.worth.ifs.project.finance.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.function.Function;

import static com.worth.ifs.util.CollectionFunctions.toSortedMap;

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

    public SortedMap<String, List<CostCategoryResource>> orderedLabelledCostCategories(){
        return toSortedMap(getCostCategories(), cc -> cc.getLabel() != null ? cc.getLabel() : "", Function.identity());
    }

    public void setCostCategories(List<CostCategoryResource> costCategories) {
        this.costCategories = costCategories;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CostCategoryGroupResource that = (CostCategoryGroupResource) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        return costCategories != null ? costCategories.equals(that.costCategories) : that.costCategories == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (costCategories != null ? costCategories.hashCode() : 0);
        return result;
    }
}
