package com.worth.ifs.application.finance.cost;

import java.math.BigDecimal;

/**
 * {@code Overhead} implements {@link CostItem}
 */
public class Overhead implements CostItem {
    String acceptRate;
    Integer customRate;

    public Overhead() {
    }

    public Overhead(String acceptRate, Integer customRate) {
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
        return null;
    }

}
