package org.innovateuk.ifs.project.financecheck.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.AcademicCostCategoryGenerator;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.project.financecheck.domain.CostCategory;
import org.innovateuk.ifs.project.financecheck.domain.CostCategoryType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileCostCategorySummaries;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileCostCategorySummary;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileCostCategorySummaryStrategy;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.project.util.FinanceUtil;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Implementation of SpendProfileCostCategorySummaryStrategy that looks to the Project Finances (i.e. the Project's
 * Finance Checks version of the original Application Finances) in order to generate a summary of each Cost Category
 * for a Partner Organisation for the purposes of generating a Spend Profile
 */
@Component
@ConditionalOnProperty(value = "ifs.spend.profile.generation.strategy", havingValue = "ByProjectFinanceCostCategorySummaryStrategy")
public class ByProjectFinanceCostCategorySummaryStrategy implements SpendProfileCostCategorySummaryStrategy {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectFinanceRowService financeRowService;

    @Autowired
    private CostCategoryTypeStrategy costCategoryTypeStrategy;

    @Autowired
    private FinanceUtil financeUtil;

    @Autowired
    private OrganisationService organisationService;

    @Override
    public ServiceResult<SpendProfileCostCategorySummaries> getCostCategorySummaries(Long projectId, Long organisationId) {

        return projectService.getProjectById(projectId).andOnSuccess(project ->
               organisationService.findById(organisationId).andOnSuccess(organisation ->
               financeRowService.financeChecksDetails(project.getId(), organisationId).andOnSuccess(finances ->
               createCostCategorySummariesWithCostCategoryType(projectId, organisationId, project, organisation, finances))));
    }

    private ServiceResult<SpendProfileCostCategorySummaries> createCostCategorySummariesWithCostCategoryType(
            Long projectId, Long organisationId, ProjectResource project, OrganisationResource organisation, ProjectFinanceResource finances) {

        boolean useAcademicFinances = financeUtil.isUsingJesFinances(organisation.getOrganisationType());

        return costCategoryTypeStrategy.getOrCreateCostCategoryTypeForSpendProfile(projectId, organisationId).andOnSuccessReturn(
                costCategoryType ->
                       createCostCategorySummariesWithCostCategoryType(project, finances, useAcademicFinances, costCategoryType));
    }

    private SpendProfileCostCategorySummaries createCostCategorySummariesWithCostCategoryType(
            ProjectResource project, ProjectFinanceResource finances, boolean useAcademicFinances, CostCategoryType costCategoryType) {

        List<SpendProfileCostCategorySummary> costCategorySummaries = new ArrayList<>();
        Map<CostCategory, BigDecimal> totalsPerCostCategory = getTotalsPerCostCategory(finances, useAcademicFinances, costCategoryType);
        totalsPerCostCategory.forEach((cc, total) -> costCategorySummaries.add(new SpendProfileCostCategorySummary(cc, total, project.getDurationInMonths())));

        return new SpendProfileCostCategorySummaries(costCategorySummaries, costCategoryType);
    }

    private Map<CostCategory, BigDecimal> getTotalsPerCostCategory(ProjectFinanceResource finances, boolean useAcademicFinances, CostCategoryType costCategoryType) {

        Map<FinanceRowType, FinanceRowCostCategory> spendRows = getSpendProfileCostCategories(finances);
        return getTotalsPerCostCategory(useAcademicFinances, costCategoryType, spendRows);
    }

    private Map<CostCategory, BigDecimal> getTotalsPerCostCategory(boolean useAcademicFinances, CostCategoryType costCategoryType, Map<FinanceRowType, FinanceRowCostCategory> spendRows) {

        return useAcademicFinances ?
                getAcademicTotalsPerCostCategory(costCategoryType, spendRows) :
                getIndustrialTotalsPerCostCategory(costCategoryType, spendRows);
    }

    private Map<FinanceRowType, FinanceRowCostCategory> getSpendProfileCostCategories(ProjectFinanceResource finances) {

        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = finances.getFinanceOrganisationDetails();
        return simpleFilter(financeOrganisationDetails, (category, costs) -> category.isSpendCostCategory());
    }

    private Map<CostCategory, BigDecimal> getIndustrialTotalsPerCostCategory(CostCategoryType costCategoryType, Map<FinanceRowType, FinanceRowCostCategory> spendRows) {

        Map<CostCategory, BigDecimal> valuesPerCostCategory = new HashMap<>();

        for (Map.Entry<FinanceRowType, FinanceRowCostCategory> costCategoryDetails : spendRows.entrySet()) {
            CostCategory costCategory = findIndustrialCostCategoryForName(costCategoryType, costCategoryDetails.getKey().getName());
            valuesPerCostCategory.put(costCategory, costCategoryDetails.getValue().getTotal());
        }

        return valuesPerCostCategory;
    }

    private Map<CostCategory, BigDecimal> getAcademicTotalsPerCostCategory(CostCategoryType costCategoryType, Map<FinanceRowType, FinanceRowCostCategory> spendRows) {

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
        return simpleFindFirst(costCategoryType.getCostCategories(), cat ->
                        cat.getName().equals(academicCostCategoryMatch.getName()) &&
                        cat.getLabel().equals(academicCostCategoryMatch.getLabel()))
                .orElse(null);
    }

    private CostCategory findIndustrialCostCategoryForName(CostCategoryType costCategoryType, String costCategoryName) {
        return simpleFindFirst(costCategoryType.getCostCategories(), cat -> cat.getName().equals(costCategoryName)).get();
    }
}
