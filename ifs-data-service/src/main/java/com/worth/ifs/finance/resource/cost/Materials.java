package com.worth.ifs.finance.resource.cost;

import java.math.BigDecimal;

/**
 * {@code Materials} implements {@link CostItem}
 */
public class Materials implements CostItem {
    private Long id;
    private String name;
    private String item;
    private BigDecimal cost;
    private Integer quantity;
    private BigDecimal total = BigDecimal.ZERO;
    private CostType costType;

    public Materials() {
        this.costType = CostType.MATERIALS;
        this.name = this.costType.getType();
    }

    public Materials(Long id, String item, BigDecimal cost, Integer quantity) {
        this();
        this.id = id;
        this.item = item;
        this.cost = cost;
        this.quantity = quantity;
    }

    @Override
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

    @Override
    public BigDecimal getTotal() {
        if(quantity!=null && cost!=null) {
            total = cost.multiply(new BigDecimal(quantity));
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    @Override
    public CostType getCostType() {
        return costType;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
