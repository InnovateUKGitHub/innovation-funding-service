package com.worth.ifs.resource;

public class TravelCost {
    private Long id;
    private Double costPerItem;
    private String item;
    private Integer quantity;

    public TravelCost() {

    }

    public TravelCost(Long id, Double costPerItem, String item, Integer quantity) {
        this.id = id;
        this.costPerItem = costPerItem;
        this.item = item;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Double getCostPerItem() {
        return costPerItem;
    }

    public String getItem() {
        return item;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getTotal() {
        return quantity * costPerItem;
    }
}
