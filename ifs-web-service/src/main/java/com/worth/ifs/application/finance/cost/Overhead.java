package com.worth.ifs.application.finance.cost;

import javax.servlet.http.HttpServletRequest;

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
    public Double getTotal() {
        return 0D;
    }

    @Override
    public Long getId() {
        return null;
    }

}
