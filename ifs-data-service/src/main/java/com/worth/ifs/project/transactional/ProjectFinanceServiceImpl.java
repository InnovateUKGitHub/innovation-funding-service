package com.worth.ifs.project.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.finance.transactional.FinanceRowService;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.finance.domain.*;
import com.worth.ifs.project.finance.repository.CostCategoryTypeRepository;
import com.worth.ifs.project.finance.repository.SpendProfileRepository;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.processAnyFailuresOrSucceed;
import static com.worth.ifs.project.finance.domain.CostTimePeriod.TimeUnit.MONTH;
import static com.worth.ifs.util.CollectionFunctions.*;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.math.RoundingMode.HALF_EVEN;
import static java.util.stream.Collectors.toList;

/**
 * Service dealing with Project finance operations
 */
@Service
public class ProjectFinanceServiceImpl extends BaseTransactionalService implements ProjectFinanceService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private CostCategoryTypeRepository costCategoryTypeRepository;

    @Autowired
    private SpendProfileRepository spendProfileRepository;

    @Override
    public ServiceResult<Void> generateSpendProfile(Long projectId) {

        return projectService.getProjectById(projectId).andOnSuccess(project ->
               projectService.getProjectUsers(projectId).andOnSuccess(projectUsers -> {
                   List<Long> organisationIds = removeDuplicates(simpleMap(projectUsers, ProjectUserResource::getOrganisation));
                   return generateSpendProfileForPartnerOrganisations(project, organisationIds);
               })
        );
    }

    private ServiceResult<Void> generateSpendProfileForPartnerOrganisations(ProjectResource project, List<Long> organisationIds) {

        List<ServiceResult<Void>> generationResults = simpleMap(organisationIds, organisationId -> {

            return financeRowService.financeDetails(project.getApplication(), organisationId).andOnSuccess(finance -> {

                Map<FinanceRowType, FinanceRowCostCategory> financeDetails = finance.getFinanceOrganisationDetails();

                List<CostCategorySummary> summaryPerCategory = simpleMap(financeDetails, (category, costs) ->
                    new CostCategorySummary(category, costs.getTotal(), project.getDurationInMonths()));

                return generateSpendProfileForOrganisation(project.getId(), organisationId, summaryPerCategory);
            });
        });

        return processAnyFailuresOrSucceed(generationResults);
    }

    private ServiceResult<Void> generateSpendProfileForOrganisation(
            Long projectId,
            Long organisationId,
            List<CostCategorySummary> summaryPerCategory) {

        return find(project(projectId), organisation(organisationId)).andOnSuccessReturnVoid(
                (project, organisation) -> generateSpendProfileForOrganisation(summaryPerCategory, project, organisation));
    }

    private void generateSpendProfileForOrganisation(List<CostCategorySummary> summaryPerCategory, Project project, Organisation organisation) {

        CostCategoryType costCategoryType = getOrCreateSupportingCostCategoryType(summaryPerCategory);
        List<Cost> eligibleCosts = generateEligibleCosts(summaryPerCategory, costCategoryType);
        List<Cost> spendProfileCosts = generateSpendProfileFigures(summaryPerCategory, project, costCategoryType);

        SpendProfile spendProfile = new SpendProfile(organisation, project, costCategoryType, eligibleCosts, spendProfileCosts);
        spendProfileRepository.save(spendProfile);
    }

    private List<Cost> generateSpendProfileFigures(List<CostCategorySummary> summaryPerCategory, Project project, CostCategoryType costCategoryType) {

        List<List<Cost>> spendProfileCostsPerCategory = simpleMap(summaryPerCategory, summary -> {

            CostCategory matchingCategory = simpleFindFirst(costCategoryType.getCostCategories(),
                    cat -> cat.getName().equals(summary.getCategory().getType())).get();

            return IntStream.of(project.getDurationInMonths().intValue()).mapToObj(i -> {

                BigDecimal costValueForThisMonth = i == 0 ? summary.getFirstMonthSpend() : summary.getOtherMonthsSpend();

                return new Cost(costValueForThisMonth).
                           withCategory(matchingCategory).
                           withTimePeriod(i, MONTH, 1, MONTH);

            }).collect(toList());
        });

        return flattenLists(spendProfileCostsPerCategory);
    }

    private List<Cost> generateEligibleCosts(List<CostCategorySummary> summaryPerCategory, CostCategoryType costCategoryType) {
        return simpleMap(summaryPerCategory, summary -> {

            CostCategory matchingCategory = simpleFindFirst(costCategoryType.getCostCategories(),
                    cat -> cat.getName().equals(summary.getCategory().getType())).get();

            return new Cost(summary.getTotal()).withCategory(matchingCategory);
        });
    }

    private CostCategoryType getOrCreateSupportingCostCategoryType(List<CostCategorySummary> summaryPerCategory) {

        List<String> categoryNamesToSupport = simpleMap(summaryPerCategory, details -> details.getCategory().getType());

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

    private Supplier<ServiceResult<Project>> project(Long id) {
        return () -> getProject(id);
    }

    private ServiceResult<Project> getProject(Long id) {
        return find(projectRepository.findOne(id), notFoundError(Project.class, id));
    }

    private class CostCategorySummary {
        private FinanceRowType category;
        private BigDecimal total;
        private BigDecimal firstMonthSpend;
        private BigDecimal otherMonthsSpend;

        private CostCategorySummary(FinanceRowType category, BigDecimal total, long projectDurationInMonths) {
            this.category = category;
            this.total = total;

            BigDecimal durationInMonths = BigDecimal.valueOf(projectDurationInMonths);
            BigDecimal monthlyCost = total.divide(durationInMonths, 0, HALF_EVEN);
            BigDecimal remainder = total.subtract(monthlyCost.multiply(durationInMonths)).setScale(0, HALF_EVEN);

            this.firstMonthSpend = monthlyCost.add(remainder);
            this.otherMonthsSpend = monthlyCost;
        }

        public FinanceRowType getCategory() {
            return category;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public BigDecimal getFirstMonthSpend() {
            return firstMonthSpend;
        }

        public BigDecimal getOtherMonthsSpend() {
            return otherMonthsSpend;
        }
    }
}
