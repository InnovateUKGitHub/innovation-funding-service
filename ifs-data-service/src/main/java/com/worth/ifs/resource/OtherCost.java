package com.worth.ifs.resource;

public class OtherCost {
    private Long id;
    private Double cost;
    private String description;

    public OtherCost() {
    }

    public OtherCost(Long id, Double cost, String description) {
        this.id = id;
        this.cost = cost;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Double getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }
}
