package com.worth.ifs.domain;

import javax.persistence.*;

@Entity
public class OtherCost {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;
    private Double cost;

    @ManyToOne
    @JoinColumn(name="applicationFinanceId", referencedColumnName="id")
    private ApplicationFinance applicationFinance;

    public OtherCost(Long id, String description, Double cost) {
        this.id = id;
        this.description = description;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Double getCost() {
        return cost;
    }
}
