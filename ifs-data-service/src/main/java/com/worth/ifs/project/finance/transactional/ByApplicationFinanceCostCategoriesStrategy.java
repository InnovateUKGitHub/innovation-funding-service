package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.finance.transactional.FinanceRowService;
import com.worth.ifs.project.finance.domain.CostCategory;
import com.worth.ifs.project.finance.domain.CostCategoryGroup;
import com.worth.ifs.project.finance.domain.CostCategoryType;
import com.worth.ifs.project.finance.repository.CostCategoryTypeRepository;
import com.worth.ifs.project.transactional.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.*;

/**
 * An implementation of CostCategoryTypeStrategy that uses the original Application Finances for the given Organisation
 * to search for an existing CostCategoryType that supports its Cost Categories
 */
@Component
public class ByApplicationFinanceCostCategoriesStrategy implements CostCategoryTypeStrategy {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private CostCategoryTypeRepository costCategoryTypeRepository;

    @Override
    public ServiceResult<CostCategoryType> getOrCreateCostCategoryTypeForSpendProfile(Long projectId, Long organisationId) {

        return projectService.getProjectById(projectId).andOnSuccess(project ->
               financeRowService.financeDetails(project.getApplication(), organisationId)).andOnSuccess(finances -> {

            Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = finances.getFinanceOrganisationDetails();
            return serviceSuccess(getOrCreateSupportingCostCategoryType(financeOrganisationDetails.keySet()));
        });
    }

    private CostCategoryType getOrCreateSupportingCostCategoryType(Set<FinanceRowType> summaryPerCategory) {

        List<FinanceRowType> spendRows = simpleFilter(summaryPerCategory, FinanceRowType::isSpendCostCategory);

        List<String> categoryNamesToSupport = simpleMap(spendRows, FinanceRowType::getName);

        List<CostCategoryType> existingCostCategoryTypes = costCategoryTypeRepository.findAll();

        Optional<CostCategoryType> existingCostCategoryTypeWithMatchingCategories = simpleFindFirst(existingCostCategoryTypes, costCategoryType -> {
            List<String> existingCostCategoryNames = simpleMap(costCategoryType.getCostCategories(), CostCategory::getName);
            return existingCostCategoryNames.size() == categoryNamesToSupport.size() &&
                    existingCostCategoryNames.containsAll(categoryNamesToSupport);
        });

        return existingCostCategoryTypeWithMatchingCategories.orElseGet(() -> {

            List<CostCategory> costCategories = simpleMap(categoryNamesToSupport, CostCategory::new);
            String costCategoryGroupDescription = "Cost Category Group for Categories " + simpleJoiner(categoryNamesToSupport, ", ");
            CostCategoryGroup costCategoryGroup = new CostCategoryGroup(costCategoryGroupDescription, costCategories);

            String costCategoryTypeName = "Cost Category Type for Categories " + simpleJoiner(categoryNamesToSupport, ", ");
            CostCategoryType costCategoryTypeToCreate = new CostCategoryType(costCategoryTypeName, costCategoryGroup);
            return costCategoryTypeRepository.save(costCategoryTypeToCreate);
        });
    }
}
