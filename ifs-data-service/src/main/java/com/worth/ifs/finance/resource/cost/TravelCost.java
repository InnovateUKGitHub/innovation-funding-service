package com.worth.ifs.finance.resource.cost;

import java.math.BigDecimal;

/**
 * {@code TravelCost} implements {@link CostItem}
 */
public class TravelCost implements CostItem {
    private Long id;
    private String item;
    private BigDecimal cost;
    private Integer quantity;

    public TravelCost() {

    }

    public TravelCost(Long id, String item, BigDecimal costPerItem, Integer quantity) {
        this.id = id;
        this.item = item;
        this.cost = costPerItem;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getItem() {
        return item;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public BigDecimal getTotal() {
        if(cost == null) {
            return BigDecimal.ZERO;
        }
        return cost.multiply(new BigDecimal(quantity));
    }

}
