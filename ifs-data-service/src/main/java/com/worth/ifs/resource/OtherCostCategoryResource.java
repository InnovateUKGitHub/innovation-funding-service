package com.worth.ifs.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class OtherCostCategoryResource  extends CostCategoryResource {
    List<OtherCost> otherCosts = new ArrayList<>();
    Double total;

    public OtherCostCategoryResource() {
    }

    public OtherCostCategoryResource(Long sectionId, Long questionId, Long categoryId, List<OtherCost> otherCosts) {
        super(sectionId,questionId, categoryId);
        this.otherCosts = otherCosts;
        this.total = 0.0;
        calculateTotal();
    }

    public List<OtherCost> getOtherCosts() {
        return otherCosts;
    }

    @Override
    public Double getTotal() {
        return total;
    }

    private void calculateTotal() {
        total = otherCosts.stream().mapToDouble(o -> o.getCost()).sum();
    }
}
