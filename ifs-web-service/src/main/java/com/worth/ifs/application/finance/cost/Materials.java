package com.worth.ifs.application.finance.cost;

import javax.servlet.http.HttpServletRequest;

public class Materials implements CostItem {

    private Long id;
    private String item;
    private Double cost;
    private Integer quantity;
    private Double total = 0D;

    public Materials() {
    }

    public Materials(Long id, String item, Double cost, Integer quantity) {
        this.id = id;
        this.item = item;
        this.cost = cost;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public Double getCost() {
        return cost;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getTotal() {
        if(quantity!=null && cost!=null) {
            total = quantity * cost;
        } else {
            total = 0D;
        }
        return total;
    }

}
