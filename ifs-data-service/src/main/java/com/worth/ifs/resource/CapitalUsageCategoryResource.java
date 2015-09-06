package com.worth.ifs.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class CapitalUsageCategoryResource  extends CostCategoryResource {
    List<CapitalUsage> capitalUsages = new ArrayList<>();
    Double total;

    public CapitalUsageCategoryResource() {
    }

    public CapitalUsageCategoryResource(List<CapitalUsage> capitalUsages, Long questionId, Long sectionId) {
        super(sectionId, questionId);
        this.total = 0.0;
        this.capitalUsages = capitalUsages;
    }

    public void calculateTotal() {
        total = capitalUsages.stream().mapToDouble(c -> c.getNetCost()).sum();
    }

    public Double getTotal() {
        calculateTotal();
        return total;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public List<CapitalUsage> getCapitalUsages() {
        return capitalUsages;
    }

}
