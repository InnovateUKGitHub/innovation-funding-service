package com.worth.ifs.application.finance.cost;

import java.math.BigDecimal;

/**
 * {@code TravelCost} implements {@link CostItem}
 */
public class TravelCost implements CostItem {
    private Long id;
    private BigDecimal costPerItem;
    private String item;
    private Integer quantity;

    public TravelCost() {

    }

    public TravelCost(Long id, BigDecimal costPerItem, String item, Integer quantity) {
        this.id = id;
        this.costPerItem = costPerItem;
        this.item = item;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getCostPerItem() {
        return costPerItem;
    }

    public String getItem() {
        return item;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public BigDecimal getTotal() {
        return costPerItem.multiply(new BigDecimal(quantity));
    }

}
