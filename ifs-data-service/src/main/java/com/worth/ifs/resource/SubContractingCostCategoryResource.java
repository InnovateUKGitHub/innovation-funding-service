package com.worth.ifs.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class SubContractingCostCategoryResource  extends CostCategoryResource {
    List<SubContractingCost> subContractingCosts = new ArrayList<>();
    Double total;

    public SubContractingCostCategoryResource() {
    }

    public SubContractingCostCategoryResource(Long sectionId, Long questionId, Long categoryId, List<SubContractingCost> subContractingCosts) {
        super(sectionId, questionId, categoryId);
        this.total = 0.0;
        this.subContractingCosts = subContractingCosts;

        calculateTotal();
    }

    public List<SubContractingCost> getSubContractingCosts() {
        return subContractingCosts;
    }

    @Override
    public Double getTotal() {
        return total;
    }

    private void calculateTotal() {
        total = subContractingCosts.stream().mapToDouble(s -> s.getCost()).sum();
    }
}
