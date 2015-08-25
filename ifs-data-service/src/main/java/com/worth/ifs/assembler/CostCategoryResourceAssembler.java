package com.worth.ifs.assembler;

import com.worth.ifs.assembler.mapping.*;
import com.worth.ifs.domain.CostCategory;
import com.worth.ifs.resource.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CostCategoryResourceAssembler {
    public final String QUESTION_TYPE_LABOUR = "labour";
    public final String QUESTION_TYPE_MATERIALS = "materials";
    public final String QUESTION_TYPE_CAPITAL_USAGE = "capital_usage";
    public final String QUESTION_TYPE_OVERHEADS = "overheads";
    public final String QUESTION_TYPE_SUBCONTRACTING_COSTS = "subcontracting_costs";
    public final String QUESTION_TYPE_TRAVEL = "travel";
    public final String QUESTION_TYPE_OTHER_COSTS = "other_costs";

    private final Log log = LogFactory.getLog(getClass());

    public List<CostCategoryResource> getCostCategories(List<CostCategory> costCategories) {
        List<CostCategoryResource> costCategoryResources = new ArrayList<>();

        ResourceMapper resourceMapper = null;
        for(CostCategory costCategory : costCategories) {
            if(costCategory.getQuestion().getQuestionType().getTitle().equals(QUESTION_TYPE_LABOUR)) {
                resourceMapper = new LabourCostMapper();
            } else if(costCategory.getQuestion().getQuestionType().getTitle().equals(QUESTION_TYPE_MATERIALS)) {
                resourceMapper = new MaterialsMapper();
            } else if(costCategory.getQuestion().getQuestionType().getTitle().equals(QUESTION_TYPE_CAPITAL_USAGE)) {
                resourceMapper = new CapitalUsageMapper();
            } else if(costCategory.getQuestion().getQuestionType().getTitle().equals(QUESTION_TYPE_OVERHEADS)) {
                resourceMapper = new OverheadsMapper();
            } else if(costCategory.getQuestion().getQuestionType().getTitle().equals(QUESTION_TYPE_SUBCONTRACTING_COSTS)) {
                resourceMapper = new SubContractingCostMapper();
            } else if(costCategory.getQuestion().getQuestionType().getTitle().equals(QUESTION_TYPE_TRAVEL)) {
                resourceMapper = new TravelCostMapper();
            } else if(costCategory.getQuestion().getQuestionType().getTitle().equals(QUESTION_TYPE_OTHER_COSTS)) {
                resourceMapper = new OtherCostMapper();
            }
            costCategoryResources.add(resourceMapper.getCostResource(costCategory));
        }
        return costCategoryResources;
    }
}
