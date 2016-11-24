package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.finance.transactional.FinanceRowService;
import com.worth.ifs.project.finance.domain.CostCategory;
import com.worth.ifs.project.finance.domain.CostCategoryType;
import com.worth.ifs.project.finance.mapper.CostCategoryMapper;
import com.worth.ifs.project.finance.mapper.CostCategoryTypeMapper;
import com.worth.ifs.project.finance.resource.CostCategoryResource;
import com.worth.ifs.project.transactional.ProjectService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.worth.ifs.util.CollectionFunctions.*;
import static com.worth.ifs.util.MapFunctions.toMap;

/**
 * Implementation of SpendProfileCostCategorySummaryStrategy that looks to the Application Finances in order to generate
 * a summary of each Cost Category for a Partner Organisation for the purposes of generating a Spend Profile
 */
@Component
public class ByApplicationFinanceCostCategorySummaryStrategy implements SpendProfileCostCategorySummaryStrategy {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private CostCategoryTypeStrategy costCategoryTypeStrategy;

    @Autowired
    private CostCategoryMapper costCategoryMapper;

    @Autowired
    private CostCategoryTypeMapper costCategoryTypeMapper;

    @Override
    public ServiceResult<SpendProfileCostCategorySummaries> getCostCategorySummaries(Long projectId, Long organisationId) {

        return projectService.getProjectById(projectId).andOnSuccess(project ->
                financeRowService.financeDetails(project.getApplication(), organisationId).andOnSuccess(finances -> {

                    return costCategoryTypeStrategy.getOrCreateCostCategoryTypeForSpendProfile(projectId, organisationId).andOnSuccessReturn(costCategoryType -> {

                        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = finances.getFinanceOrganisationDetails();

                        Map<FinanceRowType, FinanceRowCostCategory> spendRows =
                                simpleFilter(financeOrganisationDetails, (category, costs) -> category.isSpendCostCategory());

                        List<Pair<CostCategoryResource, FinanceRowCostCategory>> spendRowsAgainstCategories =
                                simpleMap(spendRows, (category, costs) -> Pair.of(findCategoryForFinanceRowType(category, costCategoryType), costs));

                        List<SpendProfileCostCategorySummary> costCategorySummaries = simpleMap(toMap(spendRowsAgainstCategories), (category, costs) ->
                                new SpendProfileCostCategorySummary(category, costs.getTotal(), project.getDurationInMonths()));

                        return new SpendProfileCostCategorySummaries(costCategorySummaries, costCategoryTypeMapper.mapToResource(costCategoryType));
                    });
                }));
    }

    private CostCategoryResource findCategoryForFinanceRowType(FinanceRowType category, CostCategoryType costCategoryType) {
        CostCategory costCategory = simpleFindFirst(costCategoryType.getCostCategories(), cat -> cat.getName().equals(category.getName())).get();
        return costCategoryMapper.mapToResource(costCategory);
    }
}