package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.handler.OrganisationFinanceDelegate;
import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.cost.AcademicCostCategoryGenerator;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.finance.transactional.FinanceRowService;
import com.worth.ifs.organisation.transactional.OrganisationService;
import com.worth.ifs.project.finance.domain.CostCategory;
import com.worth.ifs.project.finance.domain.CostCategoryType;
import com.worth.ifs.project.transactional.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Implementation of SpendProfileCostCategorySummaryStrategy that looks to the Application Finances in order to generate
 * a summary of each Cost Category for a Partner Organisation for the purposes of generating a Spend Profile
 */
@Component
@ConditionalOnProperty(value = "ifs.spend.profile.generation.strategy", havingValue = "ByProjectFinanceCostCategorySummaryStrategy")
public class ByProjectFinanceCostCategorySummaryStrategy implements SpendProfileCostCategorySummaryStrategy {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private CostCategoryTypeStrategy costCategoryTypeStrategy;

    @Autowired
    private OrganisationFinanceDelegate organisationFinanceDelegate;

    @Autowired
    private OrganisationService organisationService;

    @Override
    public ServiceResult<SpendProfileCostCategorySummaries> getCostCategorySummaries(Long projectId, Long organisationId) {

        return projectService.getProjectById(projectId).andOnSuccess(project ->
               organisationService.findById(organisationId).andOnSuccess(organisation ->
               financeRowService.financeChecksDetails(project.getId(), organisationId).andOnSuccess(finances -> {

                   boolean academicFinances = organisationFinanceDelegate.isUsingJesFinances(organisation.getOrganisationTypeName());

                   return costCategoryTypeStrategy.getOrCreateCostCategoryTypeForSpendProfile(projectId, organisationId).andOnSuccessReturn(costCategoryType -> {

                        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = finances.getFinanceOrganisationDetails();

                        Map<FinanceRowType, FinanceRowCostCategory> spendRows =
                                simpleFilter(financeOrganisationDetails, (category, costs) -> category.isSpendCostCategory());

                       Map<CostCategory, BigDecimal> valuesPerCostCategory = academicFinances ?
                               getAcademicValuesPerCostCategory(costCategoryType, spendRows) :
                               getIndustrialValuesPerCostCategory(costCategoryType, spendRows);

                        List<SpendProfileCostCategorySummary> costCategorySummaries = new ArrayList<>();
                        valuesPerCostCategory.forEach((cc, total) -> costCategorySummaries.add(new SpendProfileCostCategorySummary(cc, total, project.getDurationInMonths())));

                        return new SpendProfileCostCategorySummaries(costCategorySummaries, costCategoryType);
                    });
                })));
    }

    private Map<CostCategory, BigDecimal> getIndustrialValuesPerCostCategory(CostCategoryType costCategoryType, Map<FinanceRowType, FinanceRowCostCategory> spendRows) {

        Map<CostCategory, BigDecimal> valuesPerCostCategory = new HashMap<>();

        for (Map.Entry<FinanceRowType, FinanceRowCostCategory> costCategoryDetails : spendRows.entrySet()) {
            CostCategory costCategory = findIndustrialCostCategoryForName(costCategoryType, costCategoryDetails.getKey().getName());
            valuesPerCostCategory.put(costCategory, costCategoryDetails.getValue().getTotal());
        }

        return valuesPerCostCategory;
    }

    private Map<CostCategory, BigDecimal> getAcademicValuesPerCostCategory(CostCategoryType costCategoryType, Map<FinanceRowType, FinanceRowCostCategory> spendRows) {

        Map<CostCategory, BigDecimal> valuesPerCostCategory = new HashMap<>();
        costCategoryType.getCostCategories().forEach(cc -> valuesPerCostCategory.put(cc, BigDecimal.ZERO));

        for (FinanceRowCostCategory costCategoryDetails : spendRows.values()) {

            List<FinanceRowItem> costs = costCategoryDetails.getCosts();

            for (FinanceRowItem cost : costs) {

                String costCategoryName = cost.getName();
                CostCategory costCategory = findAcademicCostCategoryForName(costCategoryType, costCategoryName);
                BigDecimal value = cost.getTotal();

                BigDecimal currentValue = valuesPerCostCategory.get(costCategory);
                valuesPerCostCategory.put(costCategory, currentValue.add(value));
            }
        }
        return valuesPerCostCategory;
    }

    private CostCategory findAcademicCostCategoryForName(CostCategoryType costCategoryType, String costCategoryName) {
        AcademicCostCategoryGenerator academicCostCategoryMatch = AcademicCostCategoryGenerator.fromFinanceRowName(costCategoryName);
        return simpleFindFirst(costCategoryType.getCostCategories(), cat -> cat.getName().equals(academicCostCategoryMatch.getName())).get();
    }

    private CostCategory findIndustrialCostCategoryForName(CostCategoryType costCategoryType, String costCategoryName) {
        return simpleFindFirst(costCategoryType.getCostCategories(), cat -> cat.getName().equals(costCategoryName)).get();
    }
}