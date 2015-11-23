package com.worth.ifs.application.finance.cost;

import java.math.BigDecimal;

/**
 * {@code Materials} implements {@link CostItem}
 */
public class Materials implements CostItem {

    private Long id;
    private String item;
    private BigDecimal cost;
    private Integer quantity;
    private BigDecimal total = BigDecimal.ZERO;

    public Materials() {
    }

    public Materials(Long id, String item, BigDecimal cost, Integer quantity) {
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

    public BigDecimal getCost() {
        return cost;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getTotal() {
        if(quantity!=null && cost!=null) {
            total = cost.multiply(new BigDecimal(quantity));
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

}
