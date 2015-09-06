package com.worth.ifs.resource;

import com.worth.ifs.domain.Cost;
import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class LabourCategoryResource extends CostCategoryResource {
    Integer workingDaysPerYear;
    List<LabourCost> labourCosts = new ArrayList<>();
    Double total;

    public LabourCategoryResource() {
    }

    public LabourCategoryResource(Long categoryId, Integer workingDaysPerYear, List<LabourCost> labourCosts, Long questionId, Long sectionId) {
        super(sectionId, questionId);
        this.workingDaysPerYear = workingDaysPerYear;
        this.labourCosts = labourCosts;
        this.total = 0.0;

        calculateTotal();
    }

    public Integer getWorkingDaysPerYear() {
        return workingDaysPerYear;
    }

    public void calculateTotal() {
        total = labourCosts.stream().mapToDouble(lc -> lc.getTotal(workingDaysPerYear)).sum();
    }

    public Double getTotal() {
        return total;
    }

    public List<LabourCost> getLabourCosts() {
        return labourCosts;
    }
}
