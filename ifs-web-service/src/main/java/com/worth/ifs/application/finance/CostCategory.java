package com.worth.ifs.application.finance;

import com.worth.ifs.application.finance.cost.CostItem;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * {@code CostCategory} interface is for defined for retrieving updating and calculating costs
 * of which a cost category consists.
 */
public interface CostCategory {
    public List<CostItem> getCosts();

    public Double getTotal();
    public void addCost(CostItem costItem);
}
