package com.worth.ifs.assembler;

import com.worth.ifs.domain.Cost;
import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.resource.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CostCategoryResourceAssembler {
    public final String WORKING_DAYS_PER_YEAR = "Working days per year";
    public final String QUESTION_TYPE_LABOUR = "labour";
    public final String QUESTION_TYPE_MATERIALS = "materials";
    private final Log log = LogFactory.getLog(getClass());

    public List<CostCategoryResource> getCostCategories(List<CostCategory> costCategories) {
        List<CostCategoryResource> costCategoryResources = new ArrayList<>();

        for(CostCategory costCategory : costCategories) {
            if(costCategory.getQuestion().getQuestionType().getTitle().equals(QUESTION_TYPE_LABOUR)) {
                Integer workingDaysPerYear = getWorkingDaysPerYear(costCategory);
                List<LabourCost> labourCost = mapToLabourCosts(costCategory.getCosts());
                LabourCategoryResource labourCategoryResource = new LabourCategoryResource(costCategory.getId(), workingDaysPerYear, labourCost, costCategory.getQuestion().getId(), costCategory.getQuestion().getSection().getId());
                costCategoryResources.add(labourCategoryResource);
            }
            else if(costCategory.getQuestion().getQuestionType().getTitle().equals(QUESTION_TYPE_MATERIALS)) {
                List<Materials> materials = mapToMaterials(costCategory.getCosts());
                MaterialsCategoryResource materialsCategoryResource = new MaterialsCategoryResource(costCategory.getId(), materials, costCategory.getQuestion().getId(), costCategory.getQuestion().getSection().getId());
                costCategoryResources.add(materialsCategoryResource);
            }
        }
        return costCategoryResources;
    }

    private Integer getWorkingDaysPerYear(CostCategory labourCostCategory) {
        Optional<Cost> cost = labourCostCategory.getCosts().stream().filter(c -> c.getDescription().equals(WORKING_DAYS_PER_YEAR)).findFirst();
        return cost.isPresent() ? cost.get().getQuantity() : 0;
    }

    private List<LabourCost> mapToLabourCosts(List<Cost> costs) {
        List<LabourCost> labourCosts = new ArrayList<>();
        costs.stream().filter(c -> !c.getDescription().equals(WORKING_DAYS_PER_YEAR)).forEach(l -> labourCosts.add(new LabourCost(l.getId(), l.getItem(), l.getCost(), l.getQuantity())));
        return labourCosts;
    }

    private List<Materials> mapToMaterials(List<Cost> costs) {
        List<Materials> materials = new ArrayList<>();
        costs.stream().forEach(c -> materials.add(new Materials(c.getId(),c.getItem(),c.getCost(),c.getQuantity())));
        return materials;
    }
}
