package com.worth.ifs.finance.resource.cost;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;

/**
 * {@code Overhead} implements {@link CostItem}
 */
public class Overhead implements CostItem {
    private Long id;
    private String acceptRate;
    private Integer customRate;
    private CostType costType;

    public Overhead() {
        this.costType = CostType.OVERHEADS;
    }

    public Overhead(Long id, String acceptRate, Integer customRate) {
        this();
        this.id = id;
        this.acceptRate = acceptRate;
        this.customRate = customRate;
    }

    public String getAcceptRate() {
        return acceptRate;
    }

    public Integer getCustomRate() {
        return customRate;
    }

    @Override
    public BigDecimal getTotal() {
        return BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public CostType getCostType() {
        return costType;
    }
}
