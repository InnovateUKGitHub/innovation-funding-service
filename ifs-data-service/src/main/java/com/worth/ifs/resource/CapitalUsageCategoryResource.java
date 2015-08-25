package com.worth.ifs.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class CapitalUsageCategoryResource  extends CostCategoryResource {
    List<CapitalUsage> capitalUsages = new ArrayList<>();
    Double total;

    public CapitalUsageCategoryResource() {
    }

    public CapitalUsageCategoryResource(Long categoryId, List<CapitalUsage> capitalUsages, Long questionId, Long sectionId) {
        super(sectionId, questionId, categoryId);
        this.total = 0.0;
        this.capitalUsages = capitalUsages;

        calculateTotal();
    }

    public void calculateTotal() {
        total = capitalUsages.stream().mapToDouble(c -> c.getNetCost()).sum();
    }

    public Double getTotal() {
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

    public Long getCategoryId() {
        return categoryId;
    }
}
