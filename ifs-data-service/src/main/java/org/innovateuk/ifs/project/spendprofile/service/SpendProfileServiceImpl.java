package org.innovateuk.ifs.project.spendprofile.service;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.AcademicCostCategoryGenerator;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.domain.EligibilityProcess;
import org.innovateuk.ifs.project.financechecks.domain.ViabilityProcess;
import org.innovateuk.ifs.project.financechecks.repository.CostCategoryRepository;
import org.innovateuk.ifs.project.financechecks.repository.CostCategoryTypeRepository;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.spendprofile.domain.*;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileCostCategorySummaries;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileCostCategorySummaryStrategy;
import org.innovateuk.ifs.project.transactional.EmailService;
import org.innovateuk.ifs.project.transactional.ProjectGrantOfferService;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.project.users.ProjectUsersHelper;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.innovateuk.ifs.util.EntityLookupCallbacks;
import org.innovateuk.ifs.validator.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.project.finance.resource.TimeUnit.MONTH;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service dealing with Project finance operations
 */
@Service
public class SpendProfileServiceImpl extends BaseTransactionalService implements SpendProfileService {

    private static final String CSV_MONTH = "Month";
    private static final String CSV_TOTAL = "TOTAL";
    private static final String CSV_ELIGIBLE_COST_TOTAL = "Eligible Costs Total";
    private static final String CSV_FILE_NAME_FORMAT = "%s_Spend_Profile_%s.csv";
    private static final String CSV_FILE_NAME_DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    private static final List<String> RESEARCH_CAT_GROUP_ORDER = new LinkedList<>();
    public static final String EMPTY_CELL = "";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    private SpendProfileRepository spendProfileRepository;

    @Autowired
    private CostCategoryTypeRepository costCategoryTypeRepository;

    @Autowired
    private CostCategoryRepository costCategoryRepository;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectFinanceRowService financeRowService;

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ValidationUtil validationUtil;

    @Autowired
    private SpendProfileCostCategorySummaryStrategy spendProfileCostCategorySummaryStrategy;

    @Autowired
    private ProjectGrantOfferService projectGrantOfferService;

    @Autowired
    private OrganisationFinanceDelegate organisationFinanceDelegate;

    @Autowired
    private ProjectUsersHelper projectUsersHelper;

    @Autowired
    private EmailService projectEmailService;

    static {
        RESEARCH_CAT_GROUP_ORDER.add(AcademicCostCategoryGenerator.DIRECTLY_INCURRED_STAFF.getLabel());
        RESEARCH_CAT_GROUP_ORDER.add(AcademicCostCategoryGenerator.DIRECTLY_ALLOCATED_INVESTIGATORS.getLabel());
        RESEARCH_CAT_GROUP_ORDER.add(AcademicCostCategoryGenerator.INDIRECT_COSTS.getLabel());
        RESEARCH_CAT_GROUP_ORDER.add(AcademicCostCategoryGenerator.INDIRECT_COSTS_STAFF.getLabel());
    }

    enum Notifications {
        FINANCE_CONTACT_SPEND_PROFILE_AVAILABLE
    }

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    public ServiceResult<Void> generateSpendProfile(Long projectId) {

        return getProject(projectId).andOnSuccess(project ->
               canSpendProfileCanBeGenerated(project).andOnSuccess(() ->
               projectService.getProjectUsers(projectId).andOnSuccess(projectUsers -> {
                   List<Long> organisationIds = removeDuplicates(simpleMap(projectUsers, ProjectUserResource::getOrganisation));
                   return generateSpendProfileForPartnerOrganisations(project, organisationIds);
               }))
        );
    }

    private ServiceResult<Void> canSpendProfileCanBeGenerated(Project project) {
        return (isViabilityApprovedOrNotApplicable(project))
                .andOnSuccess(() -> isEligibilityApprovedOrNotApplicable(project))
                .andOnSuccess(() -> isSpendProfileAlreadyGenerated(project));
    }

    private ServiceResult<Void> isViabilityApprovedOrNotApplicable(Project project) {

        List<PartnerOrganisation> partnerOrganisations = project.getPartnerOrganisations();

        Optional<PartnerOrganisation> existingReviewablePartnerOrganisation = simpleFindFirst(partnerOrganisations, partnerOrganisation ->
                ViabilityState.REVIEW == viabilityWorkflowHandler.getState(partnerOrganisation));

        if (!existingReviewablePartnerOrganisation.isPresent()) {
            return serviceSuccess();
        } else {
            return serviceFailure(SPEND_PROFILE_CANNOT_BE_GENERATED_UNTIL_ALL_VIABILITY_APPROVED);
        }
    }

    private ServiceResult<Void> isEligibilityApprovedOrNotApplicable(Project project) {

        List<PartnerOrganisation> partnerOrganisations = project.getPartnerOrganisations();

        Optional<PartnerOrganisation> existingReviewablePartnerOrganisation = simpleFindFirst(partnerOrganisations, partnerOrganisation ->
                EligibilityState.REVIEW == eligibilityWorkflowHandler.getState(partnerOrganisation));

        if (!existingReviewablePartnerOrganisation.isPresent()) {
            return serviceSuccess();
        } else {
            return serviceFailure(SPEND_PROFILE_CANNOT_BE_GENERATED_UNTIL_ALL_ELIGIBILITY_APPROVED);
        }
    }

    private ServiceResult<Void> isSpendProfileAlreadyGenerated(Project project) {

        List<PartnerOrganisation> partnerOrganisations = project.getPartnerOrganisations();

        Optional<PartnerOrganisation> partnerOrganisationWithSpendProfile = simpleFindFirst(partnerOrganisations, partnerOrganisation ->
                spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(), partnerOrganisation.getOrganisation().getId()).isPresent());

        if (!partnerOrganisationWithSpendProfile.isPresent()) {
            return serviceSuccess();
        } else {
            return serviceFailure(SPEND_PROFILE_HAS_ALREADY_BEEN_GENERATED);
        }
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

        return find(project(projectId), organisation(organisationId)).andOnSuccess((project, organisation) ->
                generateSpendProfileForOrganisation(spendProfileCostCategorySummaries , project, organisation, generatedBy, generatedDate).andOnSuccess(() ->
                             sendFinanceContactEmail(project, organisation)
                )
        );
    }

    private ServiceResult<Void> generateSpendProfileForOrganisation(SpendProfileCostCategorySummaries spendProfileCostCategorySummaries, Project project, Organisation organisation, User generatedBy, Calendar generatedDate) {
        List<Cost> eligibleCosts = generateEligibleCosts(spendProfileCostCategorySummaries);
        List<Cost> spendProfileCosts = generateSpendProfileFigures(spendProfileCostCategorySummaries, project);
        CostCategoryType costCategoryType = costCategoryTypeRepository.findOne(spendProfileCostCategorySummaries.getCostCategoryType().getId());
        SpendProfile spendProfile = new SpendProfile(organisation, project, costCategoryType, eligibleCosts, spendProfileCosts, generatedBy, generatedDate, false, ApprovalType.UNSET);
        spendProfileRepository.save(spendProfile);
        return serviceSuccess();
    }

    private ServiceResult<Void> sendFinanceContactEmail(Project project, Organisation organisation) {
        Optional<ProjectUser> financeContact = projectUsersHelper.getFinanceContact(project.getId(), organisation.getId());
        if (financeContact.isPresent() && financeContact.get().getUser() != null) {
            NotificationTarget financeContactTarget = new ExternalUserNotificationTarget(financeContact.get().getUser().getName(), financeContact.get().getUser().getEmail());
            Map<String, Object> globalArguments = createGlobalArgsForFinanceContactSpendProfileAvailableEmail();
            return projectEmailService.sendEmail(singletonList(financeContactTarget), globalArguments, Notifications.FINANCE_CONTACT_SPEND_PROFILE_AVAILABLE);
        }
        return serviceFailure(CommonFailureKeys.SPEND_PROFILE_FINANCE_CONTACT_NOT_PRESENT);
    }

    private Map<String, Object> createGlobalArgsForFinanceContactSpendProfileAvailableEmail() {
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("dashboardUrl", webBaseUrl);
        return globalArguments;

    }

    private List<Cost> generateEligibleCosts(SpendProfileCostCategorySummaries spendProfileCostCategorySummaries) {
        return simpleMap(spendProfileCostCategorySummaries.getCosts(), cost -> {
            CostCategory cc = costCategoryRepository.findOne(cost.getCategory().getId());
            return new Cost(cost.getTotal().setScale(0, ROUND_HALF_UP)).withCategory(cc);
        });
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

    @Override
    /**
     * This method was written to recreate Spend Profile for one of the partner organisations on Production.
     *
     * This method assumes that all the necessary stuff is in the database before the Spend Profile can be generated.
     * This does not perform any validations to check that the Finance Checks are complete, Viability is approved,
     * Eligibility is approved or if the Spend Profile is already generated.
     *
     */
    public ServiceResult<Void> generateSpendProfileForPartnerOrganisation(Long projectId, Long organisationId, Long userId) {
        User user = userRepository.findOne(userId);

        return spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(projectId, organisationId).
                andOnSuccess(spendProfileCostCategorySummaries ->
                        generateSpendProfileForOrganisation(projectId, organisationId, spendProfileCostCategorySummaries, user, Calendar.getInstance()));
    }

    @Override
    public ServiceResult<Void> approveOrRejectSpendProfile(Long projectId, ApprovalType approvalType) {
        updateApprovalOfSpendProfile(projectId, approvalType);
        return projectGrantOfferService.generateGrantOfferLetterIfReady(projectId).andOnFailure(() -> serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_FAILURE));
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
    public ServiceResult<Void> markSpendProfileComplete(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        SpendProfileTableResource table = getSpendProfileTable(projectOrganisationCompositeId).getSuccessObject();
        if(table.getValidationMessages().hasErrors()) { // validate before marking as complete
            return serviceFailure(SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE);
        } else {
            return saveSpendProfileData(projectOrganisationCompositeId, table, true);
        }
    }

    @Override
    public ServiceResult<Void> markSpendProfileIncomplete(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        SpendProfileTableResource table = getSpendProfileTable(projectOrganisationCompositeId).getSuccessObject();
            return saveSpendProfileData(projectOrganisationCompositeId, table, false);
    }

    @Override
    public ServiceResult<Void> completeSpendProfilesReview(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {
            if (project.getSpendProfileSubmittedDate() != null) {
                return serviceFailure(SPEND_PROFILES_HAVE_ALREADY_BEEN_SUBMITTED);
            }

            if (project.getSpendProfiles().stream().anyMatch(spendProfile -> !spendProfile.isMarkedAsComplete())) {
                return serviceFailure(SPEND_PROFILES_MUST_BE_COMPLETE_BEFORE_SUBMISSION);
            }

            project.setSpendProfileSubmittedDate(ZonedDateTime.now());
            updateApprovalOfSpendProfile(projectId, ApprovalType.UNSET);
            return serviceSuccess();
        });
    }

    private ServiceResult<Void> rejectSpendProfileSubmission(Long projectId) {
        return getProject(projectId).andOnSuccessReturnVoid(project -> project.setSpendProfileSubmittedDate(null));
    }

    @Override
    public ServiceResult<Void> saveCreditReport(Long projectId, Long organisationId, boolean reportPresent) {

        return getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(partnerOrganisation -> validateCreditReport(partnerOrganisation))
                .andOnSuccess(() -> getProjectFinance(projectId, organisationId))
                .andOnSuccessReturnVoid(projectFinance -> {

                    projectFinance.setCreditReportConfirmed(reportPresent);
                    projectFinanceRepository.save(projectFinance);

                });
    }

    private ServiceResult<Void> validateCreditReport(PartnerOrganisation partnerOrganisation) {

        return getViabilityProcess(partnerOrganisation)
        .andOnSuccess(viabilityProcess -> {
                        if (ViabilityState.APPROVED == viabilityProcess.getActivityState()) {
                            return serviceFailure(VIABILITY_HAS_ALREADY_BEEN_APPROVED);
                        } else {
                            return serviceSuccess();
                        }
                    });
    }

    @Override
    public ServiceResult<Boolean> getCreditReport(Long projectId, Long organisationId) {
        return getProjectFinance(projectId, organisationId).andOnSuccessReturn(ProjectFinance::getCreditReportConfirmed);
    }

    @Override
    public ServiceResult<List<ProjectFinanceResource>> getProjectFinances(Long projectId) {
        return financeRowService.financeChecksTotals(projectId);
    }

    @Override
    public ServiceResult<ViabilityResource> getViability(ProjectOrganisationCompositeId projectOrganisationCompositeId){

        Long projectId = projectOrganisationCompositeId.getProjectId();
        Long organisationId = projectOrganisationCompositeId.getOrganisationId();

        return getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(partnerOrganisation -> getViabilityProcess(partnerOrganisation))
                .andOnSuccess(viabilityProcess -> getProjectFinance(projectId, organisationId)
                        .andOnSuccess(projectFinance -> buildViabilityResource(viabilityProcess, projectFinance))
                );
    }

    private ServiceResult<ViabilityProcess> getViabilityProcess(PartnerOrganisation partnerOrganisation) {

        return serviceSuccess(viabilityWorkflowHandler.getProcess(partnerOrganisation));
    }

    private ServiceResult<ViabilityResource> buildViabilityResource(ViabilityProcess viabilityProcess, ProjectFinance projectFinance) {

        ViabilityResource viabilityResource = new ViabilityResource(convertViabilityState(viabilityProcess.getActivityState()), projectFinance.getViabilityStatus());

        if (viabilityProcess.getLastModified() != null) {
            viabilityResource.setViabilityApprovalDate(ZonedDateTime.ofInstant(viabilityProcess.getLastModified().toInstant(), ZoneId.systemDefault()).toLocalDate());
        }

        setViabilityApprovalUser(viabilityResource, viabilityProcess.getInternalParticipant());

        return serviceSuccess(viabilityResource);
    }

    private Viability convertViabilityState(ViabilityState viabilityState) {

        Viability viability;

        switch (viabilityState) {
            case REVIEW:
                viability = Viability.REVIEW;
                break;
            case NOT_APPLICABLE:
                viability = Viability.NOT_APPLICABLE;
                break;
            case APPROVED:
                viability = Viability.APPROVED;
                break;
            default:
                viability = Viability.REVIEW;
        }

        return viability;

    }

    private void setViabilityApprovalUser(ViabilityResource viabilityResource, User viabilityApprovalUser) {

        if (viabilityApprovalUser != null) {
            viabilityResource.setViabilityApprovalUserFirstName(viabilityApprovalUser.getFirstName());
            viabilityResource.setViabilityApprovalUserLastName(viabilityApprovalUser.getLastName());
        }
    }

    @Override
    public ServiceResult<EligibilityResource> getEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId){

        Long projectId = projectOrganisationCompositeId.getProjectId();
        Long organisationId = projectOrganisationCompositeId.getOrganisationId();

        return getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(partnerOrganisation -> getEligibilityProcess(partnerOrganisation))
                .andOnSuccess(eligibilityProcess -> getProjectFinance(projectId, organisationId)
                        .andOnSuccess(projectFinance -> buildEligibilityResource(eligibilityProcess, projectFinance))
                );
    }

    private ServiceResult<EligibilityProcess> getEligibilityProcess(PartnerOrganisation partnerOrganisation) {

        return serviceSuccess(eligibilityWorkflowHandler.getProcess(partnerOrganisation));
    }

    private ServiceResult<EligibilityResource> buildEligibilityResource(EligibilityProcess eligibilityProcess, ProjectFinance projectFinance) {
        EligibilityResource eligibilityResource = new EligibilityResource(convertEligibilityState(eligibilityProcess.getActivityState()), projectFinance.getEligibilityStatus());

        if (eligibilityProcess.getLastModified() != null) {
            eligibilityResource.setEligibilityApprovalDate(ZonedDateTime.ofInstant(eligibilityProcess.getLastModified().toInstant(), ZoneId.systemDefault()).toLocalDate());
        }

        setEligibilityApprovalUser(eligibilityResource, eligibilityProcess.getInternalParticipant());

        return serviceSuccess(eligibilityResource);
    }

    private Eligibility convertEligibilityState(EligibilityState eligibilityState) {

        Eligibility eligibility;

        switch (eligibilityState) {
            case REVIEW:
                eligibility = Eligibility.REVIEW;
                break;
            case NOT_APPLICABLE:
                eligibility = Eligibility.NOT_APPLICABLE;
                break;
            case APPROVED:
                eligibility = Eligibility.APPROVED;
                break;
            default:
                eligibility = Eligibility.REVIEW;
        }

        return eligibility;

    }

    private void setEligibilityApprovalUser(EligibilityResource eligibilityResource, User eligibilityApprovalUser) {

        if (eligibilityApprovalUser != null) {
            eligibilityResource.setEligibilityApprovalUserFirstName(eligibilityApprovalUser.getFirstName());
            eligibilityResource.setEligibilityApprovalUserLastName(eligibilityApprovalUser.getLastName());
        }
    }

    @Override
    public ServiceResult<Void> saveViability(ProjectOrganisationCompositeId projectOrganisationCompositeId, Viability viability, ViabilityRagStatus viabilityRagStatus){

        Long projectId = projectOrganisationCompositeId.getProjectId();
        Long organisationId = projectOrganisationCompositeId.getOrganisationId();

        return getCurrentlyLoggedInUser().andOnSuccess(currentUser ->
                getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(partnerOrganisation -> getViabilityProcess(partnerOrganisation)
                        .andOnSuccess(viabilityProcess -> validateViability(viabilityProcess.getActivityState(), viability, viabilityRagStatus))
                        .andOnSuccess(() -> getProjectFinance(projectId, organisationId))
                        .andOnSuccess(projectFinance -> triggerViabilityWorkflowEvent(currentUser, partnerOrganisation, viability)
                                .andOnSuccess(() -> saveViability(projectFinance, viabilityRagStatus))
                        )
                ));
    }

    private ServiceResult<Void> validateViability(ViabilityState currentViabilityState, Viability viability, ViabilityRagStatus viabilityRagStatus) {

        if (ViabilityState.APPROVED == currentViabilityState) {
            return serviceFailure(VIABILITY_HAS_ALREADY_BEEN_APPROVED);
        }

        if (Viability.APPROVED == viability && ViabilityRagStatus.UNSET == viabilityRagStatus) {
            return serviceFailure(VIABILITY_RAG_STATUS_MUST_BE_SET);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> triggerViabilityWorkflowEvent(User currentUser, PartnerOrganisation partnerOrganisation, Viability viability) {

        if (Viability.APPROVED == viability) {
            viabilityWorkflowHandler.viabilityApproved(partnerOrganisation, currentUser);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> saveViability(ProjectFinance projectFinance, ViabilityRagStatus viabilityRagStatus) {

        projectFinance.setViabilityStatus(viabilityRagStatus);

        projectFinanceRepository.save(projectFinance);

        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> saveEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId, Eligibility eligibility, EligibilityRagStatus eligibilityRagStatus){

        Long projectId = projectOrganisationCompositeId.getProjectId();
        Long organisationId = projectOrganisationCompositeId.getOrganisationId();

        return getCurrentlyLoggedInUser().andOnSuccess(currentUser -> getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(partnerOrganisation -> getEligibilityProcess(partnerOrganisation)
                        .andOnSuccess(eligibilityProcess -> validateEligibility(eligibilityProcess.getActivityState(), eligibility, eligibilityRagStatus))
                        .andOnSuccess(() -> getProjectFinance(projectId, organisationId))
                        .andOnSuccess(projectFinance -> triggerEligibilityWorkflowEvent(currentUser, partnerOrganisation, eligibility)
                                .andOnSuccess(() -> saveEligibility(projectFinance, eligibilityRagStatus)))));
    }

    private ServiceResult<Void> validateEligibility(EligibilityState currentEligibilityState, Eligibility eligibility, EligibilityRagStatus eligibilityRagStatus) {

        if (EligibilityState.APPROVED == currentEligibilityState) {
            return serviceFailure(ELIGIBILITY_HAS_ALREADY_BEEN_APPROVED);
        }

        if (Eligibility.APPROVED == eligibility && EligibilityRagStatus.UNSET == eligibilityRagStatus) {
            return serviceFailure(ELIGIBILITY_RAG_STATUS_MUST_BE_SET);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> triggerEligibilityWorkflowEvent(User currentUser, PartnerOrganisation partnerOrganisation, Eligibility eligibility) {

        if (Eligibility.APPROVED == eligibility) {
            eligibilityWorkflowHandler.eligibilityApproved(partnerOrganisation, currentUser);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> saveEligibility(ProjectFinance projectFinance, EligibilityRagStatus eligibilityRagStatus) {

        projectFinance.setEligibilityStatus(eligibilityRagStatus);

        projectFinanceRepository.save(projectFinance);

        return serviceSuccess();
    }

    private ServiceResult<Void> validateSpendProfileCosts(SpendProfileTableResource table) {

        Optional<ValidationMessages> validationMessages = validationUtil.validateSpendProfileTableResource(table);
        final List<Error> incorrectCosts = Lists.newArrayList();
        validationMessages.ifPresent(v -> incorrectCosts.addAll(v.getErrors()));

        if (incorrectCosts.isEmpty()) {
            return serviceSuccess();
        } else {
            return serviceFailure(incorrectCosts);
        }
    }

    private ServiceResult<Void> saveSpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, SpendProfileTableResource table, boolean markAsComplete) {
        return getSpendProfileEntity(projectOrganisationCompositeId.getProjectId(), projectOrganisationCompositeId.getOrganisationId()).
                andOnSuccess (
                        spendProfile -> {
                            if(spendProfile.getProject().getSpendProfileSubmittedDate() != null) {
                                return serviceFailure(SPEND_PROFILE_HAS_BEEN_SUBMITTED_AND_CANNOT_BE_EDITED);
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
        if(ApprovalType.REJECTED.equals(approvalType)) {
            rejectSpendProfileSubmission(projectId);
        }

        spendProfileRepository.save(spendProfiles);
    }

    private void checkTotalForMonthsAndAddToTable(SpendProfileTableResource table) {

        Map<Long, List<BigDecimal>> monthlyCostsPerCategoryMap = table.getMonthlyCostsPerCategoryMap();
        Map<Long, BigDecimal> eligibleCostPerCategoryMap = table.getEligibleCostPerCategoryMap();
        Map<Long, CostCategoryResource> categories = table.getCostCategoryResourceMap();

        List<Error> categoriesWithIncorrectTotal = new ArrayList<>();

        for (Map.Entry<Long, List<BigDecimal>> entry : monthlyCostsPerCategoryMap.entrySet()) {
            Long category = entry.getKey();
            List<BigDecimal> monthlyCosts = entry.getValue();

            BigDecimal actualTotalCost = monthlyCosts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal expectedTotalCost = eligibleCostPerCategoryMap.get(category);

            if (actualTotalCost.compareTo(expectedTotalCost) == 1) {
                String categoryName = categories.get(category).getName();
                //TODO INFUND-7502 could come up with a better way to send the name to the frontend
                categoriesWithIncorrectTotal.add(fieldError(String.valueOf(category), actualTotalCost, SPEND_PROFILE_TOTAL_FOR_ALL_MONTHS_DOES_NOT_MATCH_ELIGIBLE_TOTAL_FOR_SPECIFIED_CATEGORY.getErrorKey(), categoryName));
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
        return EntityLookupCallbacks.find(spendProfileRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId), notFoundError(SpendProfile.class, projectId, organisationId));
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

        ArrayList<String> byCategory = new ArrayList<>();

        ArrayList<String> monthsRow = new ArrayList<>();
        monthsRow.add(CSV_MONTH);
        monthsRow.add(EMPTY_CELL);
        spendProfileTableResource.getMonths().forEach(
                value -> monthsRow.add(value.getLocalDate().toString()));
        monthsRow.add(CSV_TOTAL);
        monthsRow.add(CSV_ELIGIBLE_COST_TOTAL);

        final int[] columnSize = new int[1];
        spendProfileTableResource.getMonthlyCostsPerCategoryMap().forEach((category, values)-> {

            CostCategory cc = costCategoryRepository.findOne(category);
            if ( cc.getLabel() != null ) {
                byCategory.add(cc.getLabel());
            }
            byCategory.add(String.valueOf(cc.getName()));
            values.forEach(val -> {
                byCategory.add(val.toString());
            });
            byCategory.add(categoryToActualTotal.get(category).toString());
            byCategory.add(spendProfileTableResource.getEligibleCostPerCategoryMap().get(category).toString());

            if ( monthsRow.size() > byCategory.size() && monthsRow.contains(EMPTY_CELL)) {
                monthsRow.remove(EMPTY_CELL);
                rows.add(monthsRow.stream().toArray(String[]::new));
            } else if (monthsRow.size() > 0 ){
                rows.add(monthsRow.stream().toArray(String[]::new));
            }
            monthsRow.clear();
            rows.add(byCategory.stream().toArray(String[]::new));
            columnSize[0] = byCategory.size();
            byCategory.clear();
        });

        ArrayList<String> totals = new ArrayList<>();
        totals.add(CSV_TOTAL);
        totals.add(EMPTY_CELL);
        totalForEachMonth.forEach(value -> totals.add(value.toString()));
        totals.add(totalOfAllActualTotals.toString());
        totals.add(totalOfAllEligibleTotals.toString());
        if ( totals.size() > columnSize[0] && totals.contains(EMPTY_CELL)) {
            totals.remove(EMPTY_CELL);
        }
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

    private ServiceResult<ProjectFinance> getProjectFinance(Long projectId, Long organisationId) {
        return find(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId), notFoundError(ProjectFinance.class, projectId, organisationId));
    }

    private ServiceResult<PartnerOrganisation> getPartnerOrganisation(Long projectId, Long organisationId) {
        return find(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId), notFoundError(PartnerOrganisation.class, projectId, organisationId));
    }
}
