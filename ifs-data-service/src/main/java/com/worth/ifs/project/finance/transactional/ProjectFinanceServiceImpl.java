package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.finance.domain.*;
import com.worth.ifs.project.finance.repository.SpendProfileRepository;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.project.transactional.ProjectService;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.processAnyFailuresOrSucceed;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.project.finance.domain.TimeUnit.MONTH;
import static com.worth.ifs.util.CollectionFunctions.*;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.util.stream.Collectors.groupingBy;
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
    private SpendProfileRepository spendProfileRepository;

    @Autowired
    private CostCategoryTypeStrategy costCategoryTypeStrategy;

    @Autowired
    private SpendProfileCostCategorySummaryStrategy spendProfileCostCategorySummaryStrategy;

    private static final String SPEND_PROFILE_TOTAL_FOR_ALL_MONTHS_INCORRECT_ERROR_KEY = "PROJECT_SETUP_SPEND_PROFILE_TOTAL_FOR_ALL_MONTHS_DOES_NOT_MATCH_ELIGIBLE_TOTAL_FOR_SPECIFIED_CATEGORY";

    private static final String SPEND_PROFILE_INCORRECT_COST_ERROR_KEY = "PROJECT_SETUP_SPEND_PROFILE_INCORRECT_COST_FOR_SPECIFIED_CATEGORY_AND_MONTH";

    @Override
    public ServiceResult<Void> generateSpendProfile(Long projectId) {

        return getProject(projectId).andOnSuccess(project ->
               projectService.getProjectUsers(projectId).andOnSuccess(projectUsers -> {
                   List<Long> organisationIds = removeDuplicates(simpleMap(projectUsers, ProjectUserResource::getOrganisation));
                   return generateSpendProfileForPartnerOrganisations(project, organisationIds);
               })
        );
    }

    @Override
    public ServiceResult<SpendProfileTableResource> getSpendProfileTable(ProjectOrganisationCompositeId projectOrganisationCompositeId) {

        return find(spendProfile(projectOrganisationCompositeId.getProjectId(), projectOrganisationCompositeId.getOrganisationId()),
                project(projectOrganisationCompositeId.getProjectId())).andOnSuccess((spendProfile, project) -> {

            CostGroup eligibleCosts = spendProfile.getEligibleCosts();
            CostGroup spendProfileFigures = spendProfile.getSpendProfileFigures();

            Map<String, BigDecimal> eligibleCostsPerCategory =
                    simpleToLinkedMap(eligibleCosts.getCosts(), c -> c.getCostCategory().getName(), cost -> cost.getValue());

            Map<CostCategory, List<Cost>> spendProfileCostsPerCategory =
                    spendProfileFigures.getCosts().stream().collect(groupingBy(c -> c.getCostCategory(), LinkedHashMap::new, toList()));

            Map<String, List<Cost>> spendFiguresPerCategory =
                    simpleLinkedMapKey(spendProfileCostsPerCategory, costCategory -> costCategory.getName());

            LocalDate startDate = spendProfile.getProject().getTargetStartDate();
            int durationInMonths = spendProfile.getProject().getDurationInMonths().intValue();

            List<LocalDate> months = IntStream.range(0, durationInMonths).mapToObj(startDate::plusMonths).collect(toList());
            List<LocalDateResource> monthResources = simpleMap(months, LocalDateResource::new);

            Map<String, List<BigDecimal>> spendFiguresPerCategoryOrderedByMonth =
                    simpleLinkedMapValue(spendFiguresPerCategory, costs -> orderCostsByMonths(costs, months, project.getTargetStartDate()));

            SpendProfileTableResource table = new SpendProfileTableResource();
            table.setMonths(monthResources);
            table.setEligibleCostPerCategoryMap(eligibleCostsPerCategory);
            table.setMonthlyCostsPerCategoryMap(spendFiguresPerCategoryOrderedByMonth);
            return serviceSuccess(table);
        });
    }

    @Override
    public ServiceResult<SpendProfileResource> getSpendProfile(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return getSpendProfileEntity(projectOrganisationCompositeId.getProjectId(), projectOrganisationCompositeId.getOrganisationId())
                .andOnSuccessReturn(profile -> {
            SpendProfileResource resource = new SpendProfileResource();
            resource.setId(profile.getId());
            return resource;
        });
    }

    @Override
    public ServiceResult<Void> saveSpendProfile(ProjectOrganisationCompositeId projectOrganisationCompositeId, SpendProfileTableResource table) {

        return validateSpendProfileCosts(table)
                .andOnSuccess(() -> saveSpendProfileData(projectOrganisationCompositeId, table)) // We have to save the data even if the totals don't match, so we do that first
                .andOnSuccess(() -> validateSpendProfileTotals(table));
    }

    private ServiceResult<Void> validateSpendProfileCosts(SpendProfileTableResource table) {

        List<Error> incorrectCosts = checkCostsForAllCategories(table);

        if (incorrectCosts.isEmpty()) {
            return serviceSuccess();
        } else {
            return serviceFailure(incorrectCosts);
        }
    }

    private List<Error> checkCostsForAllCategories(SpendProfileTableResource table) {

        Map<String, List<BigDecimal>> monthlyCostsPerCategoryMap = table.getMonthlyCostsPerCategoryMap();

        List<Error> incorrectCosts = new ArrayList<>();

        for (Map.Entry<String, List<BigDecimal>> entry : monthlyCostsPerCategoryMap.entrySet()) {
            String category = entry.getKey();
            List<BigDecimal> monthlyCosts = entry.getValue();

            int index = 0;
            for (BigDecimal cost : monthlyCosts) {
                isCostValid(cost, category, index, incorrectCosts);
                index++;
            }

        }

        return incorrectCosts;
    }

    private void isCostValid(BigDecimal cost, String category, int index, List<Error> incorrectCosts) {

        checkFractionalCost(cost, category, index, incorrectCosts);

        checkCostLessThanZero(cost, category, index, incorrectCosts);

        checkCostGreaterThanOrEqualToMillion(cost, category, index, incorrectCosts);
    }

    private void checkFractionalCost(BigDecimal cost, String category, int index, List<Error> incorrectCosts) {

        if (cost.scale() > 0) {
            String errorMessage = String.format("Cost cannot contain fractional part. Category: %s, Month#: %d", category, index + 1);
            incorrectCosts.add(new Error(SPEND_PROFILE_INCORRECT_COST_ERROR_KEY, errorMessage, HttpStatus.BAD_REQUEST));
        }
    }

    private void checkCostLessThanZero(BigDecimal cost, String category, int index, List<Error> incorrectCosts) {

        if (-1 == cost.compareTo(BigDecimal.ZERO)) { // Indicates that the cost is less than zero
            String errorMessage = String.format("Cost cannot be less than zero. Category: %s, Month#: %d", category, index + 1);
            incorrectCosts.add(new Error(SPEND_PROFILE_INCORRECT_COST_ERROR_KEY, errorMessage, HttpStatus.BAD_REQUEST));
        }
    }

    private void checkCostGreaterThanOrEqualToMillion(BigDecimal cost, String category, int index, List<Error> incorrectCosts) {

        if (-1 != cost.compareTo(new BigDecimal("1000000"))) { // Indicates that the cost million or more
            String errorMessage = String.format("Cost cannot be million or more. Category: %s, Month#: %d", category, index + 1);
            incorrectCosts.add(new Error(SPEND_PROFILE_INCORRECT_COST_ERROR_KEY, errorMessage, HttpStatus.BAD_REQUEST));
        }
    }

    private ServiceResult<Void> saveSpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, SpendProfileTableResource table) {

        SpendProfile spendProfile = spendProfileRepository.findOneByProjectIdAndOrganisationId(
                projectOrganisationCompositeId.getProjectId(), projectOrganisationCompositeId.getOrganisationId());

        updateSpendProfileCosts(spendProfile, table);

        spendProfileRepository.save(spendProfile);

        return serviceSuccess();
    }

    private void updateSpendProfileCosts(SpendProfile spendProfile, SpendProfileTableResource table) {

        Map<String, List<BigDecimal>> monthlyCostsPerCategoryMap = table.getMonthlyCostsPerCategoryMap();

        for (Map.Entry<String, List<BigDecimal>> entry : monthlyCostsPerCategoryMap.entrySet()) {

            String category = entry.getKey();
            List<BigDecimal> monthlyCosts = entry.getValue();

            updateSpendProfileCostsForCategory(category, monthlyCosts, spendProfile);

        }
    }

    private void updateSpendProfileCostsForCategory(String category, List<BigDecimal> monthlyCosts, SpendProfile spendProfile) {

        List<Cost> filteredAndSortedCostsToUpdate = spendProfile.getSpendProfileFigures().getCosts().stream()
                .filter(cost -> cost.getCostCategory().getName().equalsIgnoreCase(category))
                .sorted(Comparator.comparing(cost -> cost.getCostTimePeriod().getOffsetAmount()))
                .collect(Collectors.toList());

        int index = 0;
        for (Cost costToUpdate : filteredAndSortedCostsToUpdate) {
            costToUpdate.setValue(monthlyCosts.get(index));
            index++;
        }
    }

    private ServiceResult<Void> validateSpendProfileTotals(SpendProfileTableResource table) {

        List<Error> categoriesWithIncorrectTotal = checkTotalForMonths(table);

        if (categoriesWithIncorrectTotal.isEmpty()) {
            return serviceSuccess();
        } else {
            return serviceFailure(categoriesWithIncorrectTotal);
        }
    }

    private List<Error> checkTotalForMonths(SpendProfileTableResource table) {

        Map<String, List<BigDecimal>> monthlyCostsPerCategoryMap = table.getMonthlyCostsPerCategoryMap();
        Map<String, BigDecimal> eligibleCostPerCategoryMap = table.getEligibleCostPerCategoryMap();

        List<Error> categoriesWithIncorrectTotal = new ArrayList<>();

        for (Map.Entry<String, List<BigDecimal>> entry : monthlyCostsPerCategoryMap.entrySet()) {
            String category = entry.getKey();
            List<BigDecimal> monthlyCosts = entry.getValue();

            BigDecimal actualTotalCost = monthlyCosts.stream().reduce(BigDecimal.ZERO, (d1, d2) -> d1.add(d2));
            BigDecimal expectedTotalCost = eligibleCostPerCategoryMap.get(category);

            if (!actualTotalCost.equals(expectedTotalCost)) {
                String readableErrorMessage = String.format("Spend Profile: The total for all months does not match the eligible total for category: %s", category);

                categoriesWithIncorrectTotal.add(new Error(SPEND_PROFILE_TOTAL_FOR_ALL_MONTHS_INCORRECT_ERROR_KEY, readableErrorMessage, HttpStatus.BAD_REQUEST));
            }
        }

        return categoriesWithIncorrectTotal;
    }

    private List<BigDecimal> orderCostsByMonths(List<Cost> costs, List<LocalDate> months, LocalDate startDate) {
        return simpleMap(months, month -> findCostForMonth(costs, month, startDate));
    }

    private BigDecimal findCostForMonth(List<Cost> costs, LocalDate month, LocalDate startDate) {
        Optional<Cost> matching = simpleFindFirst(costs, cost -> cost.getCostTimePeriod().getStartDate(startDate).equals(month));
        return matching.map(Cost::getValue).orElse(BigDecimal.ZERO);
    }

    private Supplier<ServiceResult<SpendProfile>> spendProfile(Long projectId, Long organisationId) {
        return () -> getSpendProfileEntity(projectId, organisationId);
    }

    private ServiceResult<SpendProfile> getSpendProfileEntity(Long projectId, Long organisationId) {
        return find(spendProfileRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId), notFoundError(SpendProfile.class, projectId, organisationId));
    }

    private ServiceResult<Void> generateSpendProfileForPartnerOrganisations(Project project, List<Long> organisationIds) {

        List<ServiceResult<Void>> generationResults = simpleMap(organisationIds, organisationId -> {

            return spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(project.getId(), organisationId).
                    andOnSuccess(summaryPerCategory ->
                            generateSpendProfileForOrganisation(project.getId(), organisationId, summaryPerCategory));
        });

        return processAnyFailuresOrSucceed(generationResults);
    }

    private ServiceResult<Void> generateSpendProfileForOrganisation(
            Long projectId,
            Long organisationId,
            List<SpendProfileCostCategorySummary> summaryPerCategory) {

        return find(project(projectId), organisation(organisationId)).andOnSuccess(
                (project, organisation) -> generateSpendProfileForOrganisation(summaryPerCategory, project, organisation));
    }

    private ServiceResult<Void> generateSpendProfileForOrganisation(List<SpendProfileCostCategorySummary> summaryPerCategory, Project project, Organisation organisation) {

        return costCategoryTypeStrategy.getOrCreateCostCategoryTypeForSpendProfile(project.getId(), organisation.getId()).
                andOnSuccessReturnVoid(costCategoryType -> {

            List<Cost> eligibleCosts = generateEligibleCosts(summaryPerCategory, costCategoryType);
            List<Cost> spendProfileCosts = generateSpendProfileFigures(summaryPerCategory, project, costCategoryType);

            SpendProfile spendProfile = new SpendProfile(organisation, project, costCategoryType, eligibleCosts, spendProfileCosts);
            spendProfileRepository.save(spendProfile);
        });
    }

    private List<Cost> generateSpendProfileFigures(List<SpendProfileCostCategorySummary> summaryPerCategory, Project project, CostCategoryType costCategoryType) {

        List<List<Cost>> spendProfileCostsPerCategory = simpleMap(summaryPerCategory, summary -> {

            CostCategory matchingCategory = findMatchingCostCategory(costCategoryType, summary);

            return IntStream.range(0, project.getDurationInMonths().intValue()).mapToObj(i -> {

                BigDecimal costValueForThisMonth = i == 0 ? summary.getFirstMonthSpend() : summary.getOtherMonthsSpend();

                return new Cost(costValueForThisMonth).
                           withCategory(matchingCategory).
                           withTimePeriod(i, MONTH, 1, MONTH);

            }).collect(toList());
        });

        return flattenLists(spendProfileCostsPerCategory);
    }

    private List<Cost> generateEligibleCosts(List<SpendProfileCostCategorySummary> summaryPerCategory, CostCategoryType costCategoryType) {

        return simpleMap(summaryPerCategory, summary -> {

            CostCategory matchingCategory = findMatchingCostCategory(costCategoryType, summary);
            return new Cost(summary.getTotal().setScale(0, ROUND_HALF_UP)).withCategory(matchingCategory);
        });
    }

    private CostCategory findMatchingCostCategory(CostCategoryType costCategoryType, SpendProfileCostCategorySummary summary) {
        return simpleFindFirst(costCategoryType.getCostCategories(), cat -> cat.getName().equals(summary.getCategory().getName())).get();
    }

    private Supplier<ServiceResult<Project>> project(Long id) {
        return () -> getProject(id);
    }

    private ServiceResult<Project> getProject(Long id) {
        return find(projectRepository.findOne(id), notFoundError(Project.class, id));
    }

}
