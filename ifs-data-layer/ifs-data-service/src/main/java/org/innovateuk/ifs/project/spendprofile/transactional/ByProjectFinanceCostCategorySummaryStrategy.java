package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

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
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private CostCategoryTypeStrategy costCategoryTypeStrategy;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private CompetitionService competitionService;

    @Override
    public ServiceResult<SpendProfileCostCategorySummaries> getCostCategorySummaries(Long projectId, Long organisationId) {

        return projectService.getProjectById(projectId).andOnSuccess(project ->
               organisationService.findById(organisationId).andOnSuccess(organisation ->
               projectFinanceService.financeChecksDetails(project.getId(), organisationId).andOnSuccess(finances ->
               createCostCategorySummariesWithCostCategoryType(projectId, organisationId, project, organisation, finances))));
    }

    private ServiceResult<SpendProfileCostCategorySummaries> createCostCategorySummariesWithCostCategoryType(
            Long projectId, Long organisationId, ProjectResource project, OrganisationResource organisation, ProjectFinanceResource finances) {
        CompetitionResource competition = competitionService.getCompetitionById(project.getCompetition()).getSuccess();

        return costCategoryTypeStrategy.getOrCreateCostCategoryTypeForSpendProfile(projectId, organisationId).andOnSuccessReturn(
                costCategoryType ->
                       createCostCategorySummariesWithCostCategoryType(project, finances, competition, organisation, costCategoryType));
    }

    private SpendProfileCostCategorySummaries createCostCategorySummariesWithCostCategoryType(
            ProjectResource project, ProjectFinanceResource finances, CompetitionResource competition, OrganisationResource organisation, CostCategoryType costCategoryType) {

        List<SpendProfileCostCategorySummary> costCategorySummaries = new ArrayList<>();
        Map<CostCategory, BigDecimal> totalsPerCostCategory = getTotalsPerCostCategory(finances, competition, organisation, costCategoryType);
        totalsPerCostCategory.forEach((cc, total) -> costCategorySummaries.add(new SpendProfileCostCategorySummary(cc, total, project.getDurationInMonths())));

        return new SpendProfileCostCategorySummaries(costCategorySummaries, costCategoryType);
    }

    private Map<CostCategory, BigDecimal> getTotalsPerCostCategory(ProjectFinanceResource finances, CompetitionResource competition, OrganisationResource organisation, CostCategoryType costCategoryType) {

        Map<FinanceRowType, FinanceRowCostCategory> spendRows = getSpendProfileCostCategories(finances);
        return getTotalsPerCostCategory(competition, organisation, costCategoryType, spendRows);
    }

    private Map<CostCategory, BigDecimal> getTotalsPerCostCategory(CompetitionResource competition, OrganisationResource organisation, CostCategoryType costCategoryType, Map<FinanceRowType, FinanceRowCostCategory> spendRows) {

        if (competition.isProcurement()) {
            return getProcurementTotalsPerCostCategory(costCategoryType, spendRows);
        } else if (competition.applicantShouldUseJesFinances(organisation.getOrganisationTypeEnum())) {
            return getAcademicTotalsPerCostCategory(costCategoryType, spendRows);
        } else {
            return  getIndustrialTotalsPerCostCategory(costCategoryType, spendRows);
        }
    }

    private Map<FinanceRowType, FinanceRowCostCategory> getSpendProfileCostCategories(ProjectFinanceResource finances) {

        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = finances.getFinanceOrganisationDetails();
        return simpleFilter(financeOrganisationDetails, (category, costs) -> category.isIncludedInSpendProfile());
    }

    private Map<CostCategory, BigDecimal> getIndustrialTotalsPerCostCategory(CostCategoryType costCategoryType, Map<FinanceRowType, FinanceRowCostCategory> spendRows) {

        Map<CostCategory, BigDecimal> valuesPerCostCategory = new HashMap<>();

        for (Map.Entry<FinanceRowType, FinanceRowCostCategory> costCategoryDetails : spendRows.entrySet()) {
            CostCategory costCategory = findIndustrialCostCategoryForName(costCategoryType, costCategoryDetails.getKey().getDisplayName());
            valuesPerCostCategory.put(costCategory, costCategoryDetails.getValue().getTotal());
        }

        return valuesPerCostCategory;
    }

    private Map<CostCategory, BigDecimal> getProcurementTotalsPerCostCategory(CostCategoryType costCategoryType, Map<FinanceRowType, FinanceRowCostCategory> spendRows) {

        Map<CostCategory, BigDecimal> valuesPerCostCategory = new HashMap<>();
        costCategoryType.getCostCategories().forEach(cc -> valuesPerCostCategory.put(cc, BigDecimal.ZERO));

        for (Entry<FinanceRowType, FinanceRowCostCategory> entry : spendRows.entrySet()) {
            ProcurementCostCategoryGenerator generator = ProcurementCostCategoryGenerator.fromFinanceRowType(entry.getKey());
            if (generator != null) {
                CostCategory costCategory = findCostCategoryFromGenerator(costCategoryType, generator);
                BigDecimal value = entry.getValue().getTotal();
                BigDecimal currentValue = valuesPerCostCategory.get(costCategory);
                valuesPerCostCategory.put(costCategory, currentValue.add(value));
            }
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
                AcademicCostCategoryGenerator academicCostCategoryMatch = AcademicCostCategoryGenerator.fromFinanceRowName(costCategoryName);
                if(academicCostCategoryMatch != null) {
                    CostCategory costCategory = findCostCategoryFromGenerator(costCategoryType, academicCostCategoryMatch);
                    BigDecimal value = cost.getTotal();
                    BigDecimal currentValue = valuesPerCostCategory.get(costCategory);
                    valuesPerCostCategory.put(costCategory, currentValue.add(value));
                }
            }
        }
        return valuesPerCostCategory;
    }

    private CostCategory findCostCategoryFromGenerator(CostCategoryType costCategoryType, CostCategoryGenerator costCategoryGenerator) {
        return simpleFindFirst(costCategoryType.getCostCategories(), cat ->
                        cat.getName().equals(costCategoryGenerator.getDisplayName()) &&
                        Objects.equals(cat.getLabel(), costCategoryGenerator.getLabel()))
                .orElse(null);
    }

    private CostCategory findIndustrialCostCategoryForName(CostCategoryType costCategoryType, String costCategoryName) {
        return simpleFindFirst(costCategoryType.getCostCategories(), cat -> cat.getName().equals(costCategoryName)).get();
    }
}
