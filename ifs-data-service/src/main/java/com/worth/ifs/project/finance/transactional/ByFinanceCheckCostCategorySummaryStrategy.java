package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.resource.*;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.transactional.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.CollectionFunctions.unique;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

/**
 * Implementation of {@link SpendProfileCostCategorySummaryStrategy} that looks to the {@link com.worth.ifs.project.finance.domain.FinanceCheck} in order to generate
 * a summary of each {@link com.worth.ifs.project.finance.domain.CostCategory} for a Partner Organisation for the purposes of generating a {@link com.worth.ifs.project.finance.domain.SpendProfile}
 */
@Component
public class ByFinanceCheckCostCategorySummaryStrategy implements SpendProfileCostCategorySummaryStrategy {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private ProjectFinanceService projectFinanceService;


    @Override
    public ServiceResult<SpendProfileCostCategorySummaries> getCostCategorySummaries(Long projectId, Long organisationId) {
        return find(financeCheck(projectId, organisationId), project(projectId)).
                andOnSuccess((financeCheck, project) -> {
                    List<CostResource> costs = financeCheck.getCostGroup().getCosts();
                    List<SpendProfileCostCategorySummary> spendProfileCostCategorySummaries = simpleMap(costs, cost -> new SpendProfileCostCategorySummary(cost.getCostCategory(), cost.getValue(), project.getDurationInMonths()));
                    ServiceResult<CostCategoryTypeResource> costCategoryType = from(spendProfileCostCategorySummaries);
                    return costCategoryType.andOnSuccessReturn(cct -> new SpendProfileCostCategorySummaries(spendProfileCostCategorySummaries, cct));
                }
        );
    }

    private ServiceResult<CostCategoryTypeResource> from(List<SpendProfileCostCategorySummary> summaries){
        CostCategoryGroupResource costCategoryGroup = unique(summaries, item -> item.getCategory().getCostCategoryGroup());
        return projectFinanceService.findByCostCategoryGroupId(costCategoryGroup.getId());
    }

    private Supplier<ServiceResult<ProjectResource>> project(Long projectId) {
        return () -> projectService.getProjectById(projectId);
    }

    private Supplier<ServiceResult<FinanceCheckResource>> financeCheck(Long projectId, Long organisationId) {
        return () -> {
            ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
            return financeCheckService.getByProjectAndOrganisation(projectOrganisationCompositeId);
        };
    }
}