package com.worth.ifs.project.finance.transactional;

import au.com.bytecode.opencsv.CSVWriter;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.resource.cost.AcademicCostCategoryGenerator;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.finance.domain.*;
import com.worth.ifs.project.finance.mapper.CostCategoryTypeMapper;
import com.worth.ifs.project.finance.repository.CostCategoryRepository;
import com.worth.ifs.project.finance.repository.CostCategoryTypeRepository;
import com.worth.ifs.project.finance.repository.FinanceCheckProcessRepository;
import com.worth.ifs.project.finance.repository.SpendProfileRepository;
import com.worth.ifs.project.finance.resource.CostCategoryResource;
import com.worth.ifs.project.finance.resource.CostCategoryTypeResource;
import com.worth.ifs.project.finance.resource.FinanceCheckState;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.resource.*;
import com.worth.ifs.project.transactional.ProjectService;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import com.worth.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.service.ServiceResult.*;
import static com.worth.ifs.project.finance.domain.TimeUnit.MONTH;
import static com.worth.ifs.util.CollectionFunctions.*;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Service dealing with Project finance operations
 */
@Service
public class ProjectFinanceServiceImpl extends BaseTransactionalService implements ProjectFinanceService {

    private static final String CSV_MONTH = "Month";
    private static final String CSV_TOTAL = "TOTAL";
    private static final String CSV_ELIGIBLE_COST_TOTAL = "Eligible Costs Total";
    private static final String CSV_FILE_NAME_FORMAT = "%s_Spend_Profile_%s.csv";
    private static final String CSV_FILE_NAME_DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    private static final List<String> RESEARCH_CAT_GROUP_ORDER = new LinkedList<>();

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private SpendProfileRepository spendProfileRepository;

    @Autowired
    private CostCategoryTypeRepository costCategoryTypeRepository;

    @Autowired
    private CostCategoryTypeMapper costCategoryTypeMapper;

    @Autowired
    private CostCategoryRepository costCategoryRepository;

    @Autowired
    private FinanceCheckProcessRepository financeCheckProcessRepository;

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private SpendProfileCostCategorySummaryStrategy spendProfileCostCategorySummaryStrategy;

    static {
        RESEARCH_CAT_GROUP_ORDER.add(AcademicCostCategoryGenerator.DIRECTLY_INCURRED_STAFF.getLabel());
        RESEARCH_CAT_GROUP_ORDER.add(AcademicCostCategoryGenerator.DIRECTLY_ALLOCATED_INVESTIGATORS.getLabel());
        RESEARCH_CAT_GROUP_ORDER.add(AcademicCostCategoryGenerator.INDIRECT_COSTS.getLabel());
        RESEARCH_CAT_GROUP_ORDER.add(AcademicCostCategoryGenerator.INDIRECT_COSTS_STAFF.getLabel());
    }


    @Override
    public ServiceResult<Void> generateSpendProfile(Long projectId) {

        return getProject(projectId).andOnSuccess(project ->
               validateSpendProfileCanBeGenerated(project).andOnSuccess(() ->
               projectService.getProjectUsers(projectId).andOnSuccess(projectUsers -> {
                   List<Long> organisationIds = removeDuplicates(simpleMap(projectUsers, ProjectUserResource::getOrganisation));
                   return generateSpendProfileForPartnerOrganisations(project, organisationIds);
               }))
        );
    }

    private ServiceResult<Void> validateSpendProfileCanBeGenerated(Project project) {

        List<FinanceCheckProcess> financeCheckProcesses = simpleMap(project.getPartnerOrganisations(), po ->
                financeCheckProcessRepository.findOneByTargetId(po.getId()));

        Optional<FinanceCheckProcess> existingNonApprovedFinanceCheck = simpleFindFirst(financeCheckProcesses, process ->
                !FinanceCheckState.APPROVED.equals(process.getActivityState()));

        if (!existingNonApprovedFinanceCheck.isPresent()) {
            return serviceSuccess();
        } else {
            return serviceFailure(SPEND_PROFILE_CANNOT_BE_GENERATED_UNTIL_ALL_FINANCE_CHECKS_APPROVED);
        }
    }

    @Override
    public ServiceResult<Void> approveOrRejectSpendProfile(Long projectId, ApprovalType approvalType) {
        updateApprovalOfSpendProfile(projectId, approvalType);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<ApprovalType> getSpendProfileStatusByProjectId(Long projectId) {
        List<SpendProfile> spendProfiles = getSpendProfileByProjectId(projectId);

        if(spendProfiles.isEmpty()) {
            return serviceSuccess(ApprovalType.EMPTY);
        } else if(spendProfiles.stream().anyMatch(spendProfile -> spendProfile.getApproval().equals(ApprovalType.REJECTED))) {
           return serviceSuccess(ApprovalType.REJECTED);
        } else if (spendProfiles.stream().allMatch(spendProfile -> spendProfile.getApproval().equals(ApprovalType.APPROVED))) {
           return serviceSuccess(ApprovalType.APPROVED);
        }

        return serviceSuccess(ApprovalType.UNSET);
    }

    @Override
    public ServiceResult<SpendProfileTableResource> getSpendProfileTable(ProjectOrganisationCompositeId projectOrganisationCompositeId) {

        return find(
                spendProfile(projectOrganisationCompositeId.getProjectId(), projectOrganisationCompositeId.getOrganisationId()),
                project(projectOrganisationCompositeId.getProjectId())).
                andOnSuccess((spendProfile, project) -> {

            List<CostCategory> costCategories = spendProfile.getCostCategoryType().getCostCategories();
            Organisation organisation = organisationRepository.findOne(projectOrganisationCompositeId.getOrganisationId());
            CostGroup eligibleCosts = spendProfile.getEligibleCosts();
            CostGroup spendProfileFigures = spendProfile.getSpendProfileFigures();

            Map<Long, BigDecimal> eligibleCostsPerCategory =
                    simpleToLinkedMap(
                            costCategories,
                            CostCategory::getId,
                            category -> findSingleMatchingCostByCategory(eligibleCosts, category).getValue());

            Map<Long, List<Cost>> spendProfileCostsPerCategory =
                    simpleToLinkedMap(
                            costCategories,
                            CostCategory::getId,
                            category -> findMultipleMatchingCostsByCategory(spendProfileFigures, category));

            LocalDate startDate = spendProfile.getProject().getTargetStartDate();
            int durationInMonths = spendProfile.getProject().getDurationInMonths().intValue();

            List<LocalDate> months = IntStream.range(0, durationInMonths).mapToObj(startDate::plusMonths).collect(toList());
            List<LocalDateResource> monthResources = simpleMap(months, LocalDateResource::new);

            Map<Long, List<BigDecimal>> spendFiguresPerCategoryOrderedByMonth =
                    simpleLinkedMapValue(spendProfileCostsPerCategory, costs -> orderCostsByMonths(costs, months, project.getTargetStartDate()));

            SpendProfileTableResource table = new SpendProfileTableResource();
            table.setCostCategoryResourceMap(buildCostCategoryIdMap(costCategories));
            table.setMonths(monthResources);
            table.setEligibleCostPerCategoryMap(eligibleCostsPerCategory);
            table.setMonthlyCostsPerCategoryMap(spendFiguresPerCategoryOrderedByMonth);
            table.setMarkedAsComplete(spendProfile.isMarkedAsComplete());
            checkTotalForMonthsAndAddToTable(table);

           boolean isResearch = OrganisationTypeEnum.isResearch(organisation.getOrganisationType().getId());
            if (isResearch) {
                table.setCostCategoryGroupMap(groupCategories(table));
            }

            return serviceSuccess(table);
        });
    }

    private Map<Long, CostCategoryResource> buildCostCategoryIdMap(List<CostCategory> costCategories) {
        Map<Long, CostCategoryResource> returnMap = new HashMap<>();
        costCategories.forEach(costCategory -> {
            CostCategoryResource cr = new CostCategoryResource();
            cr.setId(costCategory.getId());
            cr.setName(costCategory.getName());
            cr.setLabel(costCategory.getLabel());
            returnMap.put(costCategory.getId(), cr);
         });
        return returnMap;
    }


    private Map<String, List<Map<Long, List<BigDecimal>>>> groupCategories(SpendProfileTableResource spendProfileTableResource) {
        Map<String, List<Map<Long, List<BigDecimal>>>> catGroupMap = new HashMap<>();
        spendProfileTableResource.getMonthlyCostsPerCategoryMap().forEach((category, values)-> {
            CostCategory costCategory = costCategoryRepository.findOne(category);
            if (costCategory.getLabel() == null) {
                costCategory.setLabel("DEFAULT");
            }
            if (catGroupMap.get(costCategory.getLabel()) != null) {
                Map<Long, List<BigDecimal>> tempRow = new HashMap<>();
                tempRow.put(category, values);
                catGroupMap.get(costCategory.getLabel()).add(tempRow);
            } else {
                List<Map<Long, List<BigDecimal>>> newList = new ArrayList<>();
                Map<Long, List<BigDecimal>> tempRow = new HashMap<>();
                tempRow.put(category, values);
                newList.add(tempRow);
                catGroupMap.put(costCategory.getLabel(), newList);
            }
        });
        return orderResearchCategoryMap(catGroupMap);
    }

    private Map<String, List<Map<Long, List<BigDecimal>>>> orderResearchCategoryMap(Map<String, List<Map<Long, List<BigDecimal>>>> catGroupMap) {
        Map<String, List<Map<Long, List<BigDecimal>>>> orderedCatGroupMap = new LinkedHashMap<>();
        RESEARCH_CAT_GROUP_ORDER.forEach(groupName -> {
            orderedCatGroupMap.put(groupName, catGroupMap.get(groupName));
        });
        return orderedCatGroupMap;
    }

    @Override
    public ServiceResult<SpendProfileCSVResource> getSpendProfileCSV(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        SpendProfileTableResource spendProfileTableResource = getSpendProfileTable(projectOrganisationCompositeId).getSuccessObject();
        try {
            return serviceSuccess(generateSpendProfileCSVData(spendProfileTableResource, projectOrganisationCompositeId));
        } catch (IOException ioe) {
            return serviceFailure(SPEND_PROFILE_CSV_GENERATION_FAILURE);
        }
    }

    @Override
    public ServiceResult<SpendProfileResource> getSpendProfile(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return getSpendProfileEntity(projectOrganisationCompositeId.getProjectId(), projectOrganisationCompositeId.getOrganisationId())
                .andOnSuccessReturn(profile -> {

            SpendProfileResource resource = new SpendProfileResource();
            resource.setId(profile.getId());
            resource.setGeneratedBy(userMapper.mapToResource(profile.getGeneratedBy()));
            resource.setGeneratedDate(profile.getGeneratedDate());
            resource.setMarkedAsComplete(profile.isMarkedAsComplete());
            return resource;
        });
    }

    @Override
    public ServiceResult<Void> saveSpendProfile(ProjectOrganisationCompositeId projectOrganisationCompositeId, SpendProfileTableResource table) {
        return validateSpendProfileCosts(table)
                .andOnSuccess(() -> saveSpendProfileData(projectOrganisationCompositeId, table, false)); // We have to save the data even if the totals don't match
    }

    @Override
    public ServiceResult<Void> markSpendProfile(ProjectOrganisationCompositeId projectOrganisationCompositeId, Boolean complete) {
        SpendProfileTableResource table = getSpendProfileTable(projectOrganisationCompositeId).getSuccessObject();
        if(complete && table.getValidationMessages().hasErrors()){ // validate before marking as complete
            return serviceFailure(SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE);
        } else {
            return saveSpendProfileData(projectOrganisationCompositeId, table, complete);
        }
    }

    @Override
    public ServiceResult<CostCategoryTypeResource> findByCostCategoryGroupId(Long costCategoryGroupId) {
        return find(costCategoryTypeRepository.findByCostCategoryGroupId(costCategoryGroupId), notFoundError(CostCategoryType.class, costCategoryGroupId)).
                andOnSuccessReturn(costCategoryTypeMapper::mapToResource);
    }

    public ServiceResult<Void> completeSpendProfilesReview(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {
            if (project.getSpendProfileSubmittedDate() != null) {
                return serviceFailure(new Error(SPEND_PROFILES_HAVE_ALREADY_BEEN_SUBMITTED));
            }

            if (project.getSpendProfiles().stream().anyMatch(spendProfile -> !spendProfile.isMarkedAsComplete())) {
                return serviceFailure(new Error(SPEND_PROFILES_MUST_BE_COMPLETE_BEFORE_SUBMISSION));
            }

            project.setSpendProfileSubmittedDate(LocalDateTime.now());
            return serviceSuccess();
        });
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

        Map<Long, List<BigDecimal>> monthlyCostsPerCategoryMap = table.getMonthlyCostsPerCategoryMap();

        List<Error> incorrectCosts = new ArrayList<>();

        for (Map.Entry<Long, List<BigDecimal>> entry : monthlyCostsPerCategoryMap.entrySet()) {
            Long category = entry.getKey();
            List<BigDecimal> monthlyCosts = entry.getValue();

            int index = 0;
            for (BigDecimal cost : monthlyCosts) {
                isCostValid(cost, category, index, incorrectCosts);
                index++;
            }

        }

        return incorrectCosts;
    }

    private void isCostValid(BigDecimal cost, Long category, int index, List<Error> incorrectCosts) {

        checkFractionalCost(cost, category, index, incorrectCosts);

        checkCostLessThanZero(cost, category, index, incorrectCosts);

        checkCostGreaterThanOrEqualToMillion(cost, category, index, incorrectCosts);
    }

    private void checkFractionalCost(BigDecimal cost, Long category, int index, List<Error> incorrectCosts) {

        if (cost.scale() > 0) {
            incorrectCosts.add(new Error(SPEND_PROFILE_CONTAINS_FRACTIONS_IN_COST_FOR_SPECIFIED_CATEGORY_AND_MONTH, asList(category, index + 1), HttpStatus.BAD_REQUEST));
        }
    }

    private void checkCostLessThanZero(BigDecimal cost, Long category, int index, List<Error> incorrectCosts) {

        if (-1 == cost.compareTo(BigDecimal.ZERO)) { // Indicates that the cost is less than zero
            incorrectCosts.add(new Error(SPEND_PROFILE_COST_LESS_THAN_ZERO_FOR_SPECIFIED_CATEGORY_AND_MONTH, asList(category, index + 1), HttpStatus.BAD_REQUEST));
        }
    }

    private void checkCostGreaterThanOrEqualToMillion(BigDecimal cost, Long category, int index, List<Error> incorrectCosts) {

        if (-1 != cost.compareTo(new BigDecimal("1000000"))) { // Indicates that the cost million or more
            incorrectCosts.add(new Error(SPEND_PROFILE_COST_MORE_THAN_MILLION_FOR_SPECIFIED_CATEGORY_AND_MONTH, asList(category, index + 1), HttpStatus.BAD_REQUEST));
        }
    }

    private ServiceResult<Void> saveSpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, SpendProfileTableResource table, boolean markAsComplete) {
        return getSpendProfileEntity(projectOrganisationCompositeId.getProjectId(), projectOrganisationCompositeId.getOrganisationId()).
                andOnSuccess (
                        spendProfile -> {
                            if(spendProfile.getProject().getSpendProfileSubmittedDate() != null) {
                                return serviceFailure(new Error(SPEND_PROFILE_HAS_BEEN_SUBMITTED_AND_CANNOT_BE_EDITED));
                            }

                            spendProfile.setMarkedAsComplete(markAsComplete);

                            updateSpendProfileCosts(spendProfile, table);

                            spendProfileRepository.save(spendProfile);

                            return serviceSuccess();
                        }
                );
    }

    private void updateSpendProfileCosts(SpendProfile spendProfile, SpendProfileTableResource table) {

        Map<Long, List<BigDecimal>> monthlyCostsPerCategoryMap = table.getMonthlyCostsPerCategoryMap();

        for (Map.Entry<Long, List<BigDecimal>> entry : monthlyCostsPerCategoryMap.entrySet()) {

            Long category = entry.getKey();
            List<BigDecimal> monthlyCosts = entry.getValue();

            updateSpendProfileCostsForCategory(category, monthlyCosts, spendProfile);

        }
    }

    private void updateSpendProfileCostsForCategory(Long category, List<BigDecimal> monthlyCosts, SpendProfile spendProfile) {

        List<Cost> filteredAndSortedCostsToUpdate = spendProfile.getSpendProfileFigures().getCosts().stream()
                .filter(cost -> cost.getCostCategory().getId() == category)
                .sorted(Comparator.comparing(cost -> cost.getCostTimePeriod().getOffsetAmount()))
                .collect(Collectors.toList());

        int index = 0;
        for (Cost costToUpdate : filteredAndSortedCostsToUpdate) {
            costToUpdate.setValue(monthlyCosts.get(index));
            index++;
        }
    }

    private void updateApprovalOfSpendProfile(Long projectId, ApprovalType approvalType) {
        List<SpendProfile> spendProfiles = spendProfileRepository.findByProjectId(projectId);
        spendProfiles.forEach(spendProfile -> spendProfile.setApproval(approvalType));

        spendProfileRepository.save(spendProfiles);
    }

    private void checkTotalForMonthsAndAddToTable(SpendProfileTableResource table) {

        Map<Long, List<BigDecimal>> monthlyCostsPerCategoryMap = table.getMonthlyCostsPerCategoryMap();
        Map<Long, BigDecimal> eligibleCostPerCategoryMap = table.getEligibleCostPerCategoryMap();

        List<Error> categoriesWithIncorrectTotal = new ArrayList<>();

        for (Map.Entry<Long, List<BigDecimal>> entry : monthlyCostsPerCategoryMap.entrySet()) {
            Long category = entry.getKey();
            List<BigDecimal> monthlyCosts = entry.getValue();

            BigDecimal actualTotalCost = monthlyCosts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal expectedTotalCost = eligibleCostPerCategoryMap.get(category);

            if (actualTotalCost.compareTo(expectedTotalCost) == 1) {
                categoriesWithIncorrectTotal.add(fieldError(String.valueOf(category), actualTotalCost, SPEND_PROFILE_TOTAL_FOR_ALL_MONTHS_DOES_NOT_MATCH_ELIGIBLE_TOTAL_FOR_SPECIFIED_CATEGORY.getErrorKey()));
            }
        }

        ValidationMessages validationMessages = new ValidationMessages(categoriesWithIncorrectTotal);
        validationMessages.setObjectName("SPEND_PROFILE");
        table.setValidationMessages(validationMessages);
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

        Calendar now = Calendar.getInstance();

        List<ServiceResult<Void>> generationResults = simpleMap(organisationIds, organisationId ->
                getCurrentlyLoggedInUser().andOnSuccess(user ->
                spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(project.getId(), organisationId).
                andOnSuccess(spendProfileCostCategorySummaries ->
                        generateSpendProfileForOrganisation(project.getId(), organisationId, spendProfileCostCategorySummaries, user, now))));

        return processAnyFailuresOrSucceed(generationResults);
    }

    private ServiceResult<Void> generateSpendProfileForOrganisation(
            Long projectId,
            Long organisationId,
            SpendProfileCostCategorySummaries spendProfileCostCategorySummaries,
            User generatedBy,
            Calendar generatedDate) {

        return find(project(projectId), organisation(organisationId)).andOnSuccess(
                (project, organisation) -> generateSpendProfileForOrganisation(spendProfileCostCategorySummaries , project, organisation, generatedBy, generatedDate));
    }

    private ServiceResult<Void> generateSpendProfileForOrganisation(SpendProfileCostCategorySummaries spendProfileCostCategorySummaries, Project project, Organisation organisation, User generatedBy, Calendar generatedDate) {
        List<Cost> eligibleCosts = generateEligibleCosts(spendProfileCostCategorySummaries);
        List<Cost> spendProfileCosts = generateSpendProfileFigures(spendProfileCostCategorySummaries, project);
        CostCategoryType costCategoryType = costCategoryTypeRepository.findOne(spendProfileCostCategorySummaries.getCostCategoryType().getId());
        SpendProfile spendProfile = new SpendProfile(organisation, project, costCategoryType, eligibleCosts, spendProfileCosts, generatedBy, generatedDate, false, ApprovalType.UNSET);
        spendProfileRepository.save(spendProfile);
        return serviceSuccess();
    }

    private List<Cost> generateSpendProfileFigures(SpendProfileCostCategorySummaries summaryPerCategory, Project project) {

        List<List<Cost>> spendProfileCostsPerCategory = simpleMap(summaryPerCategory.getCosts(), summary -> {
            CostCategory cc = costCategoryRepository.findOne(summary.getCategory().getId());

            return IntStream.range(0, project.getDurationInMonths().intValue()).mapToObj(i -> {

                BigDecimal costValueForThisMonth = i == 0 ? summary.getFirstMonthSpend() : summary.getOtherMonthsSpend();

                return new Cost(costValueForThisMonth).
                           withCategory(cc).
                           withTimePeriod(i, MONTH, 1, MONTH);

            }).collect(toList());
        });

        return flattenLists(spendProfileCostsPerCategory);
    }

    private List<Cost> generateEligibleCosts(SpendProfileCostCategorySummaries spendProfileCostCategorySummaries) {
        return simpleMap(spendProfileCostCategorySummaries.getCosts(), cost -> {
            CostCategory cc = costCategoryRepository.findOne(cost.getCategory().getId());
            return new Cost(cost.getTotal().setScale(0, ROUND_HALF_UP)).withCategory(cc);
        });
    }

    private Supplier<ServiceResult<Project>> project(Long id) {
        return () -> getProject(id);
    }

    private ServiceResult<Project> getProject(Long id) {
        return find(projectRepository.findOne(id), notFoundError(Project.class, id));
    }


    private List<SpendProfile> getSpendProfileByProjectId(Long projectId) {
        return spendProfileRepository.findByProjectId(projectId);
    }

    private SpendProfileCSVResource generateSpendProfileCSVData(SpendProfileTableResource spendProfileTableResource,
                                                                ProjectOrganisationCompositeId projectOrganisationCompositeId) throws IOException {
        Map<Long, BigDecimal> categoryToActualTotal = buildSpendProfileActualTotalsForAllCategories(spendProfileTableResource);
        List<BigDecimal> totalForEachMonth = buildTotalForEachMonth(spendProfileTableResource);
        BigDecimal totalOfAllActualTotals = buildTotalOfTotals(categoryToActualTotal);
        BigDecimal totalOfAllEligibleTotals = buildTotalOfTotals(spendProfileTableResource.getEligibleCostPerCategoryMap());
        Organisation organisation = organisationRepository.findOne(projectOrganisationCompositeId.getOrganisationId());

        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);
        ArrayList<String[]> rows = new ArrayList<>();
        ArrayList<String> monthsRow = new ArrayList<>();
        monthsRow.add(CSV_MONTH);
        spendProfileTableResource.getMonths().forEach(
                value -> monthsRow.add(value.getLocalDate().toString()));
        monthsRow.add(CSV_TOTAL);
        monthsRow.add(CSV_ELIGIBLE_COST_TOTAL);
        rows.add(monthsRow.stream().toArray(String[]::new));

        ArrayList<String> byCategory = new ArrayList<>();
        spendProfileTableResource.getMonthlyCostsPerCategoryMap().forEach((category, values)-> {
            CostCategory cc = costCategoryRepository.findOne(category);
            if ( cc.getLabel() != null && rows.stream().noneMatch(s -> Arrays.asList(s).contains(cc.getLabel()))) {
                byCategory.add(cc.getLabel());
            } else if ( cc.getLabel() != null ){
                byCategory.add("");
            }
            byCategory.add(String.valueOf(cc.getName()));
            values.forEach(val -> {
                byCategory.add(val.toString());
            });
            byCategory.add(categoryToActualTotal.get(category).toString());
            byCategory.add(spendProfileTableResource.getEligibleCostPerCategoryMap().get(category).toString());
            rows.add(byCategory.stream().toArray(String[]::new));
            byCategory.clear();
        });

        ArrayList<String> totals = new ArrayList<>();
        totals.add(CSV_TOTAL);
        totalForEachMonth.forEach(value -> totals.add(value.toString()));
        totals.add(totalOfAllActualTotals.toString());
        totals.add(totalOfAllEligibleTotals.toString());
        rows.add(totals.stream().toArray(String[]::new));
        csvWriter.writeAll(rows);
        csvWriter.close();

        SpendProfileCSVResource spendProfileCSVResource = new SpendProfileCSVResource();
        spendProfileCSVResource.setCsvData(stringWriter.toString());
        spendProfileCSVResource.setFileName(generateSpendProfileFileName(organisation.getName()));

        return spendProfileCSVResource;
    }

    private String generateSpendProfileFileName(String organisationName) {
        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat(CSV_FILE_NAME_DATE_FORMAT);
        return String.format(CSV_FILE_NAME_FORMAT, organisationName, dateFormat.format(date));
    }

    private Map<Long, BigDecimal> buildSpendProfileActualTotalsForAllCategories(SpendProfileTableResource table) {
        return CollectionFunctions.simpleLinkedMapValue(table.getMonthlyCostsPerCategoryMap(),
                (List<BigDecimal> monthlyCosts) -> monthlyCosts.stream().reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private List<BigDecimal> buildTotalForEachMonth(SpendProfileTableResource table) {
        Map<Long, List<BigDecimal>> monthlyCostsPerCategoryMap = table.getMonthlyCostsPerCategoryMap();
        List<BigDecimal> totalForEachMonth = Stream.generate(() -> BigDecimal.ZERO).limit(table.getMonths().size()).collect(Collectors.toList());
        AtomicInteger atomicInteger = new AtomicInteger(0);
        totalForEachMonth.forEach(totalForThisMonth -> {
            int index = atomicInteger.getAndIncrement();
            monthlyCostsPerCategoryMap.forEach((category, value) -> {
                BigDecimal costForThisMonthForCategory = value.get(index);
                if (totalForEachMonth.get(index) != null) {
                    totalForEachMonth.set(index, totalForEachMonth.get(index).add(costForThisMonthForCategory));
                } else {
                    totalForEachMonth.set(index, costForThisMonthForCategory);
                }
            });
        });
        return totalForEachMonth;
    }

    private BigDecimal buildTotalOfTotals(Map<Long, BigDecimal> input) {
        return input.values().stream().reduce(BigDecimal.ZERO, (d1, d2) -> d1.add(d2));
    }

    private List<Cost> findMultipleMatchingCostsByCategory(CostGroup spendProfileFigures, CostCategory category) {
        return simpleFilter(spendProfileFigures.getCosts(), f -> f.getCostCategory().equals(category));
    }

    private Cost findSingleMatchingCostByCategory(CostGroup eligibleCosts, CostCategory category) {
        return simpleFindFirst(eligibleCosts.getCosts(), f -> f.getCostCategory().equals(category)).get();
    }
}
