package com.worth.ifs.finance.resource.category;

import com.worth.ifs.finance.resource.cost.CostItem;
import java.math.BigDecimal;
import java.util.List;

/**
 * {@code CostCategory} interface is for defined for retrieving updating and calculating costs
 * of which a cost category consists.
 */
public interface CostCategory {
    public List<CostItem> getCosts();

    public BigDecimal getTotal();
    public void calculateTotal();
    public void addCost(CostItem costItem);
    public boolean excludeFromTotalCost();
    public void setCosts(List<CostItem> costItems);
}
