package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.finance.transactional.FinanceRowService;
import com.worth.ifs.project.transactional.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

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

    @Override
    public ServiceResult<List<SpendProfileCostCategorySummary>> getCostCategorySummaries(Long projectId, Long organisationId) {

        return projectService.getProjectById(projectId).andOnSuccess(project ->
                financeRowService.financeDetails(project.getApplication(), organisationId).andOnSuccessReturn(finances -> {

            Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = finances.getFinanceOrganisationDetails();

            Map<FinanceRowType, FinanceRowCostCategory> spendRows =
                    simpleFilter(financeOrganisationDetails, (category, costs) -> category.isSpendCostCategory());

            return simpleMap(spendRows, (category, costs) ->
                new SpendProfileCostCategorySummary(category, costs.getTotal(), project.getDurationInMonths()));

        }));
    }
}