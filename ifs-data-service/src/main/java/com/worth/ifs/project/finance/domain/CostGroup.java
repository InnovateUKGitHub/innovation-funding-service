package com.worth.ifs.project.finance.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

/**
 * Entity representing a generic grouping of Costs
 */
@Entity
public class CostGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = ALL, mappedBy = "costGroup", orphanRemoval = true)
    private List<Cost> costs = new ArrayList<>();

    @Column(nullable = false)
    private String description;

    CostGroup() {
        // for ORM use
    }

    public CostGroup(String description, Collection<Cost> costs) {
        this.description = description;
        this.setCosts(costs);
    }

    public Long getId() {
        return id;
    }

    public List<Cost> getCosts() {
        return new ArrayList<>(costs);
    }

    public void setCosts(Collection<Cost> costs) {
        this.costs.clear();
        this.costs.addAll(costs);
        this.costs.forEach(c -> c.setCostGroup(this));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
