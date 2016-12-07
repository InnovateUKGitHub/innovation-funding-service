package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.domain.CostCategory;
import com.worth.ifs.project.finance.domain.CostCategoryGroup;
import com.worth.ifs.project.finance.domain.CostCategoryType;
import com.worth.ifs.project.finance.repository.CostCategoryRepository;
import com.worth.ifs.project.finance.repository.CostCategoryTypeRepository;
import com.worth.ifs.project.finance.resource.CostResource;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.transactional.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.CollectionFunctions.unique;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

/**
 * Implementation of {@link SpendProfileCostCategorySummaryStrategy} that looks to the {@link com.worth.ifs.project.finance.domain.FinanceCheck} in order to generate
 * a summary of each {@link com.worth.ifs.project.finance.domain.CostCategory} for a Partner Organisation for the purposes of generating a {@link com.worth.ifs.project.finance.domain.SpendProfile}
 */
@Component
@ConditionalOnProperty(name = "ifs.spend.profile.generation.strategy", havingValue = "ByFinanceCheckCostCategorySummaryStrategy")
public class ByFinanceCheckCostCategorySummaryStrategy implements SpendProfileCostCategorySummaryStrategy {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private CostCategoryTypeRepository costCategoryTypeRepository;

    @Autowired
    private CostCategoryRepository costCategoryRepository;

    @Override
    public ServiceResult<SpendProfileCostCategorySummaries> getCostCategorySummaries(Long projectId, Long organisationId) {
        return find(financeCheck(projectId, organisationId), project(projectId)).
                andOnSuccess((financeCheck, project) -> {
                    List<CostResource> costs = financeCheck.getCostGroup().getCosts();
                    List<SpendProfileCostCategorySummary> spendProfileCostCategorySummaries = simpleMap(costs, cost -> {
                        CostCategory costCategory = costCategoryRepository.findOne(cost.getCostCategory().getId());
                        return new SpendProfileCostCategorySummary(costCategory, cost.getValue(), project.getDurationInMonths());
                    });
                    ServiceResult<CostCategoryType> costCategoryType = from(spendProfileCostCategorySummaries);
                    return costCategoryType.andOnSuccessReturn(cct -> new SpendProfileCostCategorySummaries(spendProfileCostCategorySummaries, cct));
                }
        );
    }

    private ServiceResult<CostCategoryType> from(List<SpendProfileCostCategorySummary> summaries){
        CostCategoryGroup costCategoryGroup = unique(summaries, item -> item.getCategory().getCostCategoryGroup());
        return find(costCategoryTypeRepository.findByCostCategoryGroupId(costCategoryGroup.getId()),
                notFoundError(CostCategoryType.class, costCategoryGroup.getId()));
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