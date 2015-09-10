package com.worth.ifs.application.finance;

import com.worth.ifs.application.finance.cost.CostItem;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class DefaultCostCategory implements CostCategory {
    CostType costType;
    List<CostItem> costs = new ArrayList<>();
    Double total = 0.0d;

    @Override
    public List<CostItem> getCosts() {
        return costs;
    }

    @Override
    public Double getTotal() {
        total = costs.stream().mapToDouble(c -> c.getTotal()).sum();
        return total;
    }

    @Override
    public void addCost(CostItem costItem) {
        if(costItem!=null) {
            costs.add(costItem);
        }
    }
}
