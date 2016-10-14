package com.worth.ifs.project.finance.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    public CostGroup() {
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
        costs.forEach(this::addCost);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addCost(Cost cost) {
        this.costs.add(cost);
        cost.setCostGroup(this);
    }

    public boolean removeCost(Cost cost) {
        return this.costs.remove(cost);
    }

    public void removeCost(int index) {
        this.costs.remove(index);
    }

    @Transient
    public Optional<Cost> getCostById(Long id){
        return this.costs.stream().filter(c -> c.getId().equals(id)).findFirst();
    }
}
