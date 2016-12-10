package org.innovateuk.ifs.project.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.finance.domain.CostCategory;
import org.innovateuk.ifs.project.finance.domain.CostCategoryGroup;
import org.innovateuk.ifs.project.finance.domain.CostCategoryType;
import org.innovateuk.ifs.project.finance.repository.CostCategoryRepository;
import org.innovateuk.ifs.project.finance.repository.CostCategoryTypeRepository;
import org.innovateuk.ifs.project.finance.resource.CostResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.CollectionFunctions.unique;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Implementation of {@link SpendProfileCostCategorySummaryStrategy} that looks to the {@link org.innovateuk.ifs.project.finance.domain.FinanceCheck} in order to generate
 * a summary of each {@link org.innovateuk.ifs.project.finance.domain.CostCategory} for a Partner Organisation for the purposes of generating a {@link org.innovateuk.ifs.project.finance.domain.SpendProfile}
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
