package org.innovateuk.ifs.project.spendprofile.transactional;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.cost.AcademicCostCategoryGenerator;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.CostCategoryResource;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.financechecks.domain.CostGroup;
import org.innovateuk.ifs.project.financechecks.repository.CostCategoryRepository;
import org.innovateuk.ifs.project.financechecks.repository.CostCategoryTypeRepository;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.spendprofile.configuration.workflow.SpendProfileWorkflowHandler;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfileNotifications;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.innovateuk.ifs.project.transactional.EmailService;
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

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    public static final String EMPTY_CELL = "";
    private static final String CSV_MONTH = "Month";
    private static final String CSV_TOTAL = "TOTAL";
    private static final String CSV_ELIGIBLE_COST_TOTAL = "Eligible Costs Total";
    private static final String CSV_FILE_NAME_FORMAT = "%s_Spend_Profile_%s.csv";
    private static final String CSV_FILE_NAME_DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    private static final List<String> RESEARCH_CAT_GROUP_ORDER = new LinkedList<>();

    static {
        RESEARCH_CAT_GROUP_ORDER.add(AcademicCostCategoryGenerator.DIRECTLY_INCURRED_STAFF.getLabel());
        RESEARCH_CAT_GROUP_ORDER.add(AcademicCostCategoryGenerator.DIRECTLY_ALLOCATED_INVESTIGATORS.getLabel());
        RESEARCH_CAT_GROUP_ORDER.add(AcademicCostCategoryGenerator.INDIRECT_COSTS.getLabel());
        RESEARCH_CAT_GROUP_ORDER.add(AcademicCostCategoryGenerator.INDIRECT_COSTS_STAFF.getLabel());
    }

    @Autowired
    private ProjectService projectService;
    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private SpendProfileRepository spendProfileRepository;
    @Autowired
    private CostCategoryTypeRepository costCategoryTypeRepository;
    @Autowired
    private CostCategoryRepository costCategoryRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ValidationUtil validationUtil;
    @Autowired
    private SpendProfileCostCategorySummaryStrategy spendProfileCostCategorySummaryStrategy;
    @Autowired
    private GrantOfferLetterService grantOfferLetterService;
    @Autowired
    private ProjectUsersHelper projectUsersHelper;
    @Autowired
    private EmailService projectEmailService;
    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;
    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;
    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;
    @Autowired
    private SpendProfileWorkflowHandler spendProfileWorkflowHandler;

    private static final Log LOG = LogFactory.getLog(SpendProfileServiceImpl.class);

    private static final String SPEND_PROFILE_STATE_ERROR = "Set Spend Profile workflow status to sent failed for project %s";

    @Override
    @Transactional
    public ServiceResult<Void> generateSpendProfile(Long projectId) {
        return getProject(projectId)
                .andOnSuccess(project -> canSpendProfileCanBeGenerated(project)
                        .andOnSuccess(() -> projectService.getProjectUsers(projectId)
                                .andOnSuccess(projectUsers -> {
                                    List<Long> organisationIds = removeDuplicates(simpleMap(projectUsers, ProjectUserResource::getOrganisation));
                                    return generateSpendProfileForPartnerOrganisations(project, organisationIds);
                                }))
                                .andOnSuccess(() -> {
                                    getCurrentlyLoggedInUser().andOnSuccess(user -> {
                                        if (spendProfileWorkflowHandler.spendProfileGenerated(project, user)) {
                                            return serviceSuccess();
                                        } else {
                                            LOG.error(String.format(SPEND_PROFILE_STATE_ERROR, project.getId()));
                                            return serviceFailure(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR);
                                        }
                                    });
                                })
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
        if (!spendProfileWorkflowHandler.isAlreadyGenerated(project)) {
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
                generateSpendProfileForOrganisation(spendProfileCostCategorySummaries, project, organisation, generatedBy, generatedDate).andOnSuccess(() ->
                        sendFinanceContactEmail(project, organisation)
                )
        );
    }

    private ServiceResult<Void> generateSpendProfileForOrganisation(SpendProfileCostCategorySummaries spendProfileCostCategorySummaries, Project project, Organisation organisation, User generatedBy, Calendar generatedDate) {
        List<Cost> eligibleCosts = generateEligibleCosts(spendProfileCostCategorySummaries);
        List<Cost> spendProfileCosts = generateSpendProfileFigures(spendProfileCostCategorySummaries, project);
        CostCategoryType costCategoryType = costCategoryTypeRepository.findOne(spendProfileCostCategorySummaries.getCostCategoryType().getId());
        SpendProfile spendProfile = new SpendProfile(organisation, project, costCategoryType, eligibleCosts, spendProfileCosts, generatedBy, generatedDate, false);
        spendProfileRepository.save(spendProfile);
        return serviceSuccess();
    }

    private ServiceResult<Void> sendFinanceContactEmail(Project project, Organisation organisation) {
        Optional<ProjectUser> financeContact = projectUsersHelper.getFinanceContact(project.getId(), organisation.getId());
        if (financeContact.isPresent() && financeContact.get().getUser() != null) {
            NotificationTarget financeContactTarget = new ExternalUserNotificationTarget(financeContact.get().getUser().getName(), financeContact.get().getUser().getEmail());
            Map<String, Object> globalArguments = createGlobalArgsForFinanceContactSpendProfileAvailableEmail();
            return projectEmailService.sendEmail(singletonList(financeContactTarget), globalArguments, SpendProfileNotifications.FINANCE_CONTACT_SPEND_PROFILE_AVAILABLE);
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

    /**
     * This method was written to recreate Spend Profile for one of the partner organisations on Production.
     *
     * This method assumes that all the necessary stuff is in the database before the Spend Profile can be generated.
     * This does not perform any validations to check that the Finance Checks are complete, Viability is approved,
     * Eligibility is approved, if the Spend Profile is already generated or the Spend Profile process state is valid.
     *
     */
    @Override
    @Transactional
    public ServiceResult<Void> generateSpendProfileForPartnerOrganisation(Long projectId, Long organisationId, Long userId) {
        User user = userRepository.findOne(userId);

        return spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(projectId, organisationId).
                andOnSuccess(spendProfileCostCategorySummaries ->
                        generateSpendProfileForOrganisation(projectId, organisationId, spendProfileCostCategorySummaries, user, Calendar.getInstance()));
    }

    @Override
    @Transactional
    public ServiceResult<Void> approveOrRejectSpendProfile(Long projectId, ApprovalType approvalType) {
        Project project = projectRepository.findOne(projectId);
        if (null != project && spendProfileWorkflowHandler.isReadyToApprove(project) && Arrays.asList(ApprovalType.APPROVED, ApprovalType.REJECTED).stream().anyMatch(e -> e.equals(approvalType))) {
            updateApprovalOfSpendProfile(projectId, approvalType);
            return approveSpendProfile(approvalType, project);
        } else {
            return serviceFailure(CommonFailureKeys.SPEND_PROFILE_NOT_READY_TO_APPROVE);
        }
    }

    private ServiceResult<Void> approveSpendProfile(ApprovalType approvalType, Project project) {
        return getCurrentlyLoggedInUser().andOnSuccess(user -> {
            if (approvalType.equals(ApprovalType.APPROVED)) {
                if (spendProfileWorkflowHandler.spendProfileApproved(project, user))
                    return grantOfferLetterService.generateGrantOfferLetterIfReady(project.getId());
                else
                    return serviceFailure(SPEND_PROFILE_CANNOT_BE_APPROVED);
            }
            if (approvalType.equals(ApprovalType.REJECTED)) {
                if (spendProfileWorkflowHandler.spendProfileRejected(project, user))
                    return serviceSuccess();
                else
                    return serviceFailure(SPEND_PROFILE_CANNOT_BE_REJECTED);
            }
            return serviceFailure(SPEND_PROFILE_NOT_READY_TO_APPROVE);
        });
    }

    @Override
    public ServiceResult<ApprovalType> getSpendProfileStatusByProjectId(Long projectId) {
        return serviceSuccess(getSpendProfileStatusBy(projectId));
    }

    @Override
    public ServiceResult<ApprovalType> getSpendProfileStatus(Long projectId) {
        return serviceSuccess(getSpendProfileStatusBy(projectId));
    }

    private ApprovalType getSpendProfileStatusBy(Long projectId) {
        Project project = projectRepository.findOne(projectId);
        if (project != null)
            return spendProfileWorkflowHandler.getApproval(project);
        else
            return ApprovalType.UNSET;
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
        spendProfileTableResource.getMonthlyCostsPerCategoryMap().forEach((category, values) -> {
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
    @Transactional
    public ServiceResult<Void> saveSpendProfile(ProjectOrganisationCompositeId projectOrganisationCompositeId, SpendProfileTableResource table) {
        return validateSpendProfileCosts(table)
                .andOnSuccess(() -> saveSpendProfileData(projectOrganisationCompositeId, table, false)); // We have to save the data even if the totals don't match
    }

    @Override
    @Transactional
    public ServiceResult<Void> markSpendProfileComplete(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        SpendProfileTableResource table = getSpendProfileTable(projectOrganisationCompositeId).getSuccessObject();
        if (table.getValidationMessages().hasErrors()) { // validate before marking as complete
            return serviceFailure(SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE);
        } else {
            return saveSpendProfileData(projectOrganisationCompositeId, table, true);
        }
    }

    @Override
    @Transactional
    public ServiceResult<Void> markSpendProfileIncomplete(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        SpendProfileTableResource table = getSpendProfileTable(projectOrganisationCompositeId).getSuccessObject();
        return saveSpendProfileData(projectOrganisationCompositeId, table, false);
    }

    @Override
    @Transactional
    public ServiceResult<Void> completeSpendProfilesReview(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {
            if (project.getSpendProfiles().stream().anyMatch(spendProfile -> !spendProfile.isMarkedAsComplete())) {
                return serviceFailure(SPEND_PROFILES_MUST_BE_COMPLETE_BEFORE_SUBMISSION);
            }
            if (spendProfileWorkflowHandler.submit(project)) {
                project.setSpendProfileSubmittedDate(ZonedDateTime.now());
                updateApprovalOfSpendProfile(projectId, ApprovalType.UNSET);
                return serviceSuccess();
            } else {
                return serviceFailure(SPEND_PROFILES_HAVE_ALREADY_BEEN_SUBMITTED);
            }
        });
    }

    private ServiceResult<Void> rejectSpendProfileSubmission(Long projectId) {
        return getProject(projectId).andOnSuccessReturnVoid(project -> project.setSpendProfileSubmittedDate(null));
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
                andOnSuccess(
                        spendProfile -> {
                            if (spendProfile.getProject().getSpendProfileSubmittedDate() != null) {
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
                .filter(cost -> Objects.equals(cost.getCostCategory().getId(), category))
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
        if (ApprovalType.REJECTED.equals(approvalType)) {
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
        spendProfileTableResource.getMonthlyCostsPerCategoryMap().forEach((category, values) -> {

            CostCategory cc = costCategoryRepository.findOne(category);
            if (cc.getLabel() != null) {
                byCategory.add(cc.getLabel());
            }
            byCategory.add(String.valueOf(cc.getName()));
            values.forEach(val -> {
                byCategory.add(val.toString());
            });
            byCategory.add(categoryToActualTotal.get(category).toString());
            byCategory.add(spendProfileTableResource.getEligibleCostPerCategoryMap().get(category).toString());

            if (monthsRow.size() > byCategory.size() && monthsRow.contains(EMPTY_CELL)) {
                monthsRow.remove(EMPTY_CELL);
                rows.add(monthsRow.stream().toArray(String[]::new));
            } else if (monthsRow.size() > 0) {
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
        if (totals.size() > columnSize[0] && totals.contains(EMPTY_CELL)) {
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
        Date date = new Date();
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
