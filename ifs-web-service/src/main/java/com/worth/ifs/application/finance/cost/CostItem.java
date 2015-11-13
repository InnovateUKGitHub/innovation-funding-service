package com.worth.ifs.application.finance.cost;

import java.math.BigDecimal;

/**
 * {@code CostItem} interface is used to handle the different type of costItems
 * for an application.
 */
public interface CostItem {
    public Long getId();
    public BigDecimal getTotal();
}
