package com.worth.ifs.assembler.mapping;

import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.resource.Overhead;
import com.worth.ifs.resource.OverheadCategoryResource;

import java.util.ArrayList;
import java.util.List;

public class OverheadsMapper implements ResourceMapper<Overhead> {
    public OverheadCategoryResource getCostResource(CostCategory costCategory) {
        List<Overhead> overheads = mapCosts(costCategory.getCosts());
        Overhead overhead = null;
        if(overheads != null && overheads.size() > 0) {
            overhead = overheads.get(0);
        }
        return new OverheadCategoryResource(
                costCategory.getQuestion().getSection().getId(), costCategory.getQuestion().getId(),
                costCategory.getId(), overhead);
    }

    public List<Overhead> mapCosts(List<Cost> costs) {
        List<Overhead> overheads = new ArrayList<>();
        costs.stream().forEach(c -> overheads.add(new Overhead(c.getItem(), c.getQuantity())));
        return overheads;
    }
}
