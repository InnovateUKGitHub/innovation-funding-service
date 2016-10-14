package com.worth.ifs.project.finance.domain;

import com.worth.ifs.util.CollectionFunctions;

import javax.persistence.*;
import java.util.*;
import java.util.function.Function;

import static com.worth.ifs.util.CollectionFunctions.toSortedMap;
import static javax.persistence.CascadeType.ALL;

/**
 * Entity representing a generic grouping of FinanceRow Categories
 */
@Entity
public class CostCategoryGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = ALL, mappedBy = "costCategoryGroup", orphanRemoval = true)
    @OrderBy("id ASC")
    private List<CostCategory> costCategories = new ArrayList<>();

    @Column(nullable = false)
    private String description;

    public CostCategoryGroup() {
        // for ORM use
    }

    public SortedMap<String, List<CostCategory>> orderedLabelledCostCategories(){
        return toSortedMap(getCostCategories(), cc -> cc.getLabel() != null ? cc.getLabel() : "", Function.identity());
    }

    public CostCategoryGroup(String description, Collection<CostCategory> costCategories) {
        this.description = description;
        this.setCostCategories(costCategories);
    }

    public Long getId() {
        return id;
    }

    public List<CostCategory> getCostCategories() {
        return new ArrayList<>(costCategories);
    }

    public void setCostCategories(Collection<CostCategory> costCategories) {
        this.costCategories.clear();
        this.costCategories.addAll(costCategories);
        this.costCategories.forEach(c -> c.setCostCategoryGroup(this));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
