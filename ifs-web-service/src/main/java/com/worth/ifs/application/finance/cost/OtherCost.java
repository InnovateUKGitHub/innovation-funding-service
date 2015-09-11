package com.worth.ifs.application.finance.cost;

import javax.servlet.http.HttpServletRequest;

public class OtherCost implements CostItem {
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

    public Double getTotal() {
        return cost;
    }
}
