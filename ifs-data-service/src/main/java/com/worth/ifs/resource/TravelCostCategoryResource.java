package com.worth.ifs.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class TravelCostCategoryResource  extends CostCategoryResource {
    List<TravelCost> travelCosts = new ArrayList<>();
    Double total;

    public TravelCostCategoryResource() {
    }

    public TravelCostCategoryResource(Long sectionId, Long questionId, Long categoryId, List<TravelCost> travelCosts) {
        super(sectionId, questionId, categoryId);
        this.total = 0.0;
        this.travelCosts = travelCosts;

        calculateTotal();
    }

    public List<TravelCost> getTravelCosts() {
        return travelCosts;
    }

    @Override
    public Double getTotal() {
        return total;
    }

    private void calculateTotal() {
        total = travelCosts.stream().mapToDouble(tc -> tc.getTotal()).sum();
    }
}
