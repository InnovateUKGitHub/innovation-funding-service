package org.innovateuk.ifs.project.financechecks.domain;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

/**
 * Entity representing a grouping of FinanceRow Categories with additional metadata relating
 * to the meaning of that grouping (e.g. for Academic costs, for Industrial costs etc)
 */
@Entity
public class CostCategoryType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = ALL, optional = false)
    private CostCategoryGroup costCategoryGroup;

    public CostCategoryType() {
        // for ORM use
    }

    public CostCategoryType(String name, CostCategoryGroup costCategoryGroup) {
        this.name = name;
        this.costCategoryGroup = costCategoryGroup;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CostCategoryGroup getCostCategoryGroup() {
        return costCategoryGroup;
    }

    public List<CostCategory> getCostCategories() {
        return getCostCategoryGroup().getCostCategories();
    }
}
