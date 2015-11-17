package com.worth.ifs.application.finance.cost;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

public class OtherCost implements CostItem {
    private Long id;
    private BigDecimal cost;
    private String description;

    public OtherCost() {
    }

    public OtherCost(Long id, BigDecimal cost, String description) {
        this.id = id;
        this.cost = cost;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getTotal() {
        return cost;
    }
}
