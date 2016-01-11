package com.worth.ifs.application.finance;

import com.worth.ifs.application.finance.cost.CostItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * {@code CostCategory} interface is for defined for retrieving updating and calculating costs
 * of which a cost category consists.
 */
public interface CostCategory {
    public List<CostItem> getCosts();

    public BigDecimal getTotal();
    public void addCost(CostItem costItem);
    public boolean excludeFromTotalCost();
}
