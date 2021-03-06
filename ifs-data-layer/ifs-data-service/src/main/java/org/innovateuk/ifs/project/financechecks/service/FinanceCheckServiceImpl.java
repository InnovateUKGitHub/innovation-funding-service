package org.innovateuk.ifs.project.financechecks.service;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.domain.*;
import org.innovateuk.ifs.project.financechecks.repository.FinanceCheckRepository;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.FundingRulesWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.PaymentMilestoneWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.repository.GrantOfferLetterProcessRepository;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterService;
import org.innovateuk.ifs.project.queries.transactional.FinanceCheckQueriesService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileService;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.transactional.StatusService;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.util.GraphBuilderContext;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.math.BigDecimal.valueOf;
import static java.math.BigDecimal.*;
import static java.math.RoundingMode.HALF_EVEN;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.MAX_DECIMAL_PLACES;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.NOT_REQUIRED;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState.SENT;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * A transactional service for finance check functionality
 */
@Service
public class FinanceCheckServiceImpl extends AbstractProjectServiceImpl implements FinanceCheckService {

    @Autowired
    private FinanceCheckRepository financeCheckRepository;

    @Autowired
    private StatusService statusService;

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Autowired
    private PaymentMilestoneWorkflowHandler paymentMilestoneWorkflowHandler;

    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Autowired
    private FundingRulesWorkflowHandler fundingRulesWorkflowHandler;

    @Autowired
    private FinanceCheckQueriesService financeCheckQueriesService;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private SpendProfileRepository spendProfileRepository;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private ApplicationFinanceService applicationFinanceService;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private SpendProfileService spendProfileService;

    @Autowired
    private GrantOfferLetterService grantOfferLetterService;

    @Autowired
    private GrantOfferLetterProcessRepository grantOfferLetterProcessRepository;

    @Override
    public ServiceResult<FinanceCheckResource> getByProjectAndOrganisation(ProjectOrganisationCompositeId key) {
        return find(financeCheckRepository.findByProjectIdAndOrganisationId(key.getProjectId(), key.getOrganisationId()),
                notFoundError(FinanceCheck.class, key)).
                andOnSuccessReturn(this::mapToResource);
    }

    @Override
    public ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(long projectId) {
        Project project = projectRepository.findById(projectId).get();
        Application application = project.getApplication();
        Competition competition = application.getCompetition();
        List<PartnerOrganisation> partnerOrganisations = partnerOrganisationRepository.findByProjectId(projectId);
        final PartnerOrganisation leadPartner = simpleFindFirst(partnerOrganisations, PartnerOrganisation::isLeadOrganisation).get();
        final List<PartnerOrganisation> sortedPartnersList = new PrioritySorting<>(partnerOrganisations, leadPartner, po -> po.getOrganisation().getName()).unwrap();
        Optional<SpendProfile> spendProfile = spendProfileRepository.findOneByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(0).getOrganisation().getId());
        boolean bankDetailsApproved = getBankDetailsApprovalStatus(projectId);

        FinanceCheckOverviewResource overviewResource = getFinanceCheckOverview(projectId).getSuccess();

        String spendProfileGeneratedBy = spendProfile.map(p -> p.getGeneratedBy().getName()).orElse(null);
        LocalDate spendProfileGeneratedDate = spendProfile.map(p -> LocalDate.from(p.getGeneratedDate().toInstant().atOffset(ZoneOffset.UTC))).orElse(null);

        return serviceSuccess(new FinanceCheckSummaryResource(overviewResource, competition.getId(), competition.getName(),
                spendProfile.isPresent(), getPartnerStatuses(sortedPartnersList, project), bankDetailsApproved,
                spendProfileGeneratedBy, spendProfileGeneratedDate, application.getId(), competition.isH2020(), competition.getFundingType(), competition.getFinanceRowTypes().contains(FinanceRowType.FINANCE)));
    }

    @Override
    public ServiceResult<FinanceCheckOverviewResource> getFinanceCheckOverview(long projectId) {
        Project project = projectRepository.findById(projectId).get();
        Application application = project.getApplication();
        Competition competition = application.getCompetition();

        List<ProjectFinanceResource> projectFinanceResourceList = projectFinanceService.financeChecksTotals(projectId).getSuccess();
        List<ApplicationFinanceResource> applicationFinanceResourceList = applicationFinanceService.financeTotals(application.getId()).getSuccess();

        BigDecimal totalProjectCost = calculateTotalForAllOrganisations(projectFinanceResourceList, BaseFinanceResource::getTotal);
        BigDecimal totalFundingSought = calculateTotalForAllOrganisations(projectFinanceResourceList, BaseFinanceResource::getTotalFundingSought);
        BigDecimal fundingAppliedFor = calculateTotalForAllOrganisations(applicationFinanceResourceList, BaseFinanceResource::getTotalFundingSought);
        BigDecimal totalOtherFunding = calculateTotalForAllOrganisations(projectFinanceResourceList, BaseFinanceResource::getTotalOtherFunding);
        BigDecimal totalPercentageGrant = calculateGrantPercentage(totalProjectCost, totalFundingSought).setScale(MAX_DECIMAL_PLACES, ROUND_HALF_UP);

        ServiceResult<Double> researchParticipationPercentage = projectFinanceService.getResearchParticipationPercentageFromProject(project.getId());
        BigDecimal researchParticipationPercentageValue = getResearchParticipationPercentage(researchParticipationPercentage);

        BigDecimal competitionMaximumResearchPercentage = valueOf(competition.getMaxResearchRatio());

        return serviceSuccess(new FinanceCheckOverviewResource(projectId, project.getName(), project.getTargetStartDate(), project.getDurationInMonths().intValue(),
                totalProjectCost, totalFundingSought, fundingAppliedFor, totalOtherFunding, totalPercentageGrant, researchParticipationPercentageValue, competitionMaximumResearchPercentage));
    }

    @Override
    public ServiceResult<FinanceCheckEligibilityResource> getFinanceCheckEligibilityDetails(long projectId, long organisationId) {
        Project project = projectRepository.findById(projectId).get();
        Application application = project.getApplication();

        return projectFinanceService.financeChecksDetails(projectId, organisationId).andOnSuccessReturn(projectFinance ->
                new FinanceCheckEligibilityResource(project.getId(),
                        organisationId,
                        project.getDurationInMonths(),
                        projectFinance.getTotal(),
                        projectFinance.getGrantClaimPercentage(),
                        projectFinance.getTotalFundingSought(),
                        projectFinance.getTotalOtherFunding(),
                        getTotalContribution(project, projectFinance),
                        hasAnyApplicationFinances(application, projectFinance),
                        calculateContributionPercentage(project, projectFinance))
        );
    }

    private BigDecimal getTotalContribution(Project project, ProjectFinanceResource finance) {
        Competition competition = project.getApplication().getCompetition();
        if (competition.isKtp()) {
            Optional<PartnerOrganisation> leadOrganisation = project.getLeadOrganisation();
            if (!leadOrganisation.isPresent()) {
                return finance.getTotalContribution();
            }
            if (finance.getOrganisation().equals(leadOrganisation.get().getOrganisation().getId())) {
                return ZERO; // Lead in KTP doesn't contribute
            }
            ProjectFinanceResource leadOrgFinance = projectFinanceService.financeChecksDetails(project.getId(), leadOrganisation.get().getOrganisation().getId()).getSuccess();
            return leadOrgFinance.getTotalContribution();
        }
        return finance.getTotalContribution();
    }

    private BigDecimal calculateContributionPercentage(Project project, ProjectFinanceResource finance) {
        Competition competition = project.getApplication().getCompetition();
        if (competition.isKtp()) {
            Optional<PartnerOrganisation> leadOrganisation = project.getLeadOrganisation();

            if (!leadOrganisation.isPresent()) {
                return ZERO;
            }
            if (finance.getOrganisation().equals(leadOrganisation.get().getOrganisation().getId())) {
                return ZERO; // Lead in KTP doesn't contribute
            }

            ProjectFinanceResource leadOrgFinance = projectFinanceService.financeChecksDetails(project.getId(), leadOrganisation.get().getOrganisation().getId()).getSuccess();
            if (leadOrgFinance.getTotal().signum() == 0 || leadOrgFinance.getTotalContribution().signum() == 0) {
                return ZERO;
            }
            return leadOrgFinance.getTotalContribution()
                    .multiply(new BigDecimal(100))
                    .divide(leadOrgFinance.getTotal(), 1, RoundingMode.HALF_UP);
        } else {
            return getTotalContribution(project, finance);
        }
    }

    private boolean hasAnyApplicationFinances(Application application, ProjectFinanceResource projectFinance) {
        return applicationFinanceRepository.existsByApplicationIdAndOrganisationId(application.getId(), projectFinance.getOrganisation());
    }

    private boolean getBankDetailsApprovalStatus(Long projectId) {
        ServiceResult<ProjectTeamStatusResource> teamStatusResult = statusService.getProjectTeamStatus(projectId, Optional.empty());
        return teamStatusResult.isSuccess() && !simpleFindFirst(teamStatusResult.getSuccess().getPartnerStatuses(), s -> !asList(COMPLETE, NOT_REQUIRED).contains(s.getBankDetailsStatus())).isPresent();
    }

    private List<FinanceCheckPartnerStatusResource> getPartnerStatuses(List<PartnerOrganisation> partnerOrganisations, Project project) {

        return mapWithIndex(partnerOrganisations, (i, org) -> {

            ProjectOrganisationCompositeId compositeId = getCompositeId(org);
            Pair<ViabilityState, ViabilityRagStatus> viability = getViabilityStatus(compositeId);
            Pair<EligibilityState, EligibilityRagStatus> eligibility = getEligibilityStatus(compositeId);
            ServiceResult<FundingRulesResource> fundingRules = getFundingRules(compositeId);

            boolean anyQueryAwaitingResponse = isQueryActionRequired(project.getId(), org.getOrganisation().getId()).getSuccess();

            return new FinanceCheckPartnerStatusResource(org.getOrganisation().getId(), org.getOrganisation().getName(),
                    org.isLeadOrganisation(), viability.getLeft(), viability.getRight(), eligibility.getLeft(),
                    eligibility.getRight(), getPaymentMilestoneState(getCompositeId(org), project),
                    fundingRules.getSuccess().getFundingRulesState(), fundingRules.getSuccess().getFundingRules(),
                    anyQueryAwaitingResponse, getFinanceContact(project, org.getOrganisation()).isPresent());
        });
    }

    @Override
    public ServiceResult<Boolean> isQueryActionRequired(long projectId, long organisationId) {
        boolean actionRequired = false;

        ServiceResult<ProjectFinanceResource> resource = projectFinanceService.financeChecksDetails(projectId, organisationId);
        if (resource.isSuccess()) {
            ServiceResult<List<QueryResource>> queries = financeCheckQueriesService.findAll(resource.getSuccess().getId());
            if (queries.isSuccess()) {
                actionRequired = queries.getSuccess().stream().anyMatch(q -> q.awaitingResponse);
            }
        }

        return serviceSuccess(actionRequired);
    }

    private ProjectOrganisationCompositeId getCompositeId(PartnerOrganisation org) {
        return new ProjectOrganisationCompositeId(org.getProject().getId(), org.getOrganisation().getId());
    }

    private Pair<ViabilityState, ViabilityRagStatus> getViabilityStatus(ProjectOrganisationCompositeId compositeId) {

        ViabilityResource viabilityDetails = getViability(compositeId).getSuccess();

        return Pair.of(viabilityDetails.getViability(), viabilityDetails.getViabilityRagStatus());

    }

    private Pair<EligibilityState, EligibilityRagStatus> getEligibilityStatus(ProjectOrganisationCompositeId compositeId) {

        EligibilityResource eligibilityDetails = getEligibility(compositeId).getSuccess();

        return Pair.of(eligibilityDetails.getEligibility(), eligibilityDetails.getEligibilityRagStatus());
    }

    private FinanceCheckResource mapToResource(FinanceCheck fc) {
        FinanceCheckResource financeCheckResource = new FinanceCheckResource();
        financeCheckResource.setId(fc.getId());
        financeCheckResource.setOrganisation(fc.getOrganisation().getId());
        financeCheckResource.setProject(fc.getProject().getId());
        financeCheckResource.setCostGroup(mapCostGroupToResource(fc.getCostGroup(), new GraphBuilderContext()));
        return financeCheckResource;
    }


    private CostGroupResource mapCostGroupToResource(CostGroup cg, GraphBuilderContext ctx) {
        return ctx.resource(cg, CostGroupResource::new, cgr -> {
            cgr.setId(cg.getId());
            cgr.setDescription(cg.getDescription());
            List<CostResource> costResources = simpleMap(cg.getCosts(), c -> mapCostsToCostResource(c, ctx));
            cgr.setCosts(costResources);
        });
    }

    private CostResource mapCostsToCostResource(Cost c, GraphBuilderContext ctx) {
        return ctx.resource(c, CostResource::new, cr -> {
            cr.setId(c.getId());
            cr.setValue(c.getValue());
            CostCategoryResource costCategoryResource = mapCostCategoryToCostCategoryResource(c.getCostCategory(), ctx);
            cr.setCostCategory(costCategoryResource);
        });
    }

    private CostCategoryResource mapCostCategoryToCostCategoryResource(CostCategory cc, GraphBuilderContext ctx) {
        return ctx.resource(cc, CostCategoryResource::new, ccr -> {
            ccr.setLabel(cc.getLabel());
            ccr.setId(cc.getId());
            CostCategoryGroupResource costCategoryGroupResource = mapCostCategoryGroupToCostCategoryGroupResource(cc.getCostCategoryGroup(), ctx);
            ccr.setCostCategoryGroup(costCategoryGroupResource);
        });

    }

    private CostCategoryGroupResource mapCostCategoryGroupToCostCategoryGroupResource(CostCategoryGroup ccg, GraphBuilderContext ctx) {
        return ctx.resource(ccg, CostCategoryGroupResource::new, ccgr -> {
            ccgr.setId(ccg.getId());
            List<CostCategoryResource> costCategoryResources = simpleMap(ccg.getCostCategories(), cc -> mapCostCategoryToCostCategoryResource(cc, ctx));
            ccgr.setCostCategories(costCategoryResources);
            ccgr.setDescription(ccg.getDescription());
        });
    }

    private <F extends BaseFinanceResource> BigDecimal calculateTotalForAllOrganisations(List<F> financeResources, Function<BaseFinanceResource, BigDecimal> keyExtractor) {
        return financeResources.stream().map(keyExtractor).reduce(ZERO, BigDecimal::add).setScale(0, HALF_EVEN);
    }

    private BigDecimal calculateGrantPercentage(BigDecimal projectTotal, BigDecimal totalFundingSought) {

        if (projectTotal.equals(ZERO)) {
            return ZERO;
        }

        return totalFundingSought.multiply(valueOf(100)).divide(projectTotal, MAX_DECIMAL_PLACES, HALF_EVEN);
    }

    private BigDecimal getResearchParticipationPercentage(ServiceResult<Double> researchParticipationPercentage) {
        BigDecimal researchParticipationPercentageValue = ZERO;
        if (researchParticipationPercentage.isSuccess() && researchParticipationPercentage.getSuccess() != null) {
            researchParticipationPercentageValue = valueOf(researchParticipationPercentage.getSuccess());
        }
        return researchParticipationPercentageValue;
    }

    @Override
    public ServiceResult<Boolean> getCreditReport(long projectId, long organisationId) {
        return getProjectFinance(projectId, organisationId).andOnSuccessReturn(ProjectFinance::getCreditReportConfirmed);
    }

    @Override
    @Transactional
    public ServiceResult<Void> approvePaymentMilestoneState(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        long organisationId = projectOrganisationCompositeId.getOrganisationId();
        long projectId = projectOrganisationCompositeId.getProjectId();
        return getCurrentlyLoggedInUser().andOnSuccess(currentUser ->
                getPartnerOrganisation(projectId, organisationId)
                        .andOnSuccess(partnerOrganisation -> triggerPaymentMilestoneApprovalWorkflowHandlerEvent(currentUser, partnerOrganisation))
        );
    }

    @Override
    @Transactional
    public ServiceResult<Void> resetPaymentMilestoneState(ProjectOrganisationCompositeId projectOrganisationCompositeId, String reason) {
        long organisationId = projectOrganisationCompositeId.getOrganisationId();
        long projectId = projectOrganisationCompositeId.getProjectId();
        return getCurrentlyLoggedInUser().andOnSuccess(currentUser ->
                getPartnerOrganisation(projectId, organisationId)
                        .andOnSuccess(this::getPaymentMilestoneProcess)
                        .andOnSuccess(process -> {
                            deleteSpendProfileAndResetGol(projectId);
                            return triggerPaymentMilestoneResetWorkflowHandlerEvent(currentUser, process, reason);
                        })
        );
    }

    @Override
    public ServiceResult<PaymentMilestoneResource> getPaymentMilestone(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        long projectId = projectOrganisationCompositeId.getProjectId();
        long organisationId = projectOrganisationCompositeId.getOrganisationId();

        return getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(this::getPaymentMilestoneProcess)
                .andOnSuccess(paymentMilestoneProcess -> buildProjectProcurementMilestoneResource(paymentMilestoneProcess));
    }

    private ServiceResult<Void> triggerPaymentMilestoneApprovalWorkflowHandlerEvent(User currentUser, PartnerOrganisation partnerOrganisation) {
        if (paymentMilestoneWorkflowHandler.paymentMilestoneApproved(partnerOrganisation, currentUser)) {
            return serviceSuccess();
        } else {
            return serviceFailure(PAYMENT_MILESTONE_CANNOT_BE_APPROVED);
        }
    }

    private ServiceResult<Void> triggerPaymentMilestoneResetWorkflowHandlerEvent(User currentUser, PaymentMilestoneProcess paymentMilestoneProcess, String reason) {
        if (paymentMilestoneProcess.getProcessState().isApproved()) {
            if (paymentMilestoneWorkflowHandler.paymentMilestoneReset(paymentMilestoneProcess.getTarget(), currentUser, reason)) {
                return serviceSuccess();
            } else {
                return serviceFailure(PAYMENT_MILESTONE_CANNOT_BE_RESET);
            }
        }
        return serviceSuccess();
    }

    private PaymentMilestoneState getPaymentMilestoneState(ProjectOrganisationCompositeId projectOrganisationCompositeId, Project project) {
        if (project.getApplication().getCompetition().isProcurementMilestones()) {
            long organisationId = projectOrganisationCompositeId.getOrganisationId();
            long projectId = projectOrganisationCompositeId.getProjectId();
            PaymentMilestoneProcess process = getPartnerOrganisation(projectId, organisationId)
                    .andOnSuccess(partnerOrganisation -> getPaymentMilestoneProcess(partnerOrganisation)).getSuccess();
            return process.getProcessState();
        }
        return null;
    }

    private ServiceResult<Void> triggerFundingRulesApprovalWorkflowHandlerEvent(User currentUser, PartnerOrganisation partnerOrganisation) {
        if (fundingRulesWorkflowHandler.fundingRulesApproved(partnerOrganisation, currentUser)) {
            return serviceSuccess();
        } else {
            return serviceFailure(FUNDING_RULES_CANNOT_BE_APPROVED);
        }
    }

    @Override
    public ServiceResult<List<ProjectFinanceResource>> getProjectFinances(long projectId) {
        return projectFinanceService.financeChecksTotals(projectId);
    }

    @Override
    public ServiceResult<ViabilityResource> getViability(ProjectOrganisationCompositeId projectOrganisationCompositeId) {

        long projectId = projectOrganisationCompositeId.getProjectId();
        long organisationId = projectOrganisationCompositeId.getOrganisationId();

        return getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(this::getViabilityProcess)
                .andOnSuccess(viabilityProcess -> getProjectFinance(projectId, organisationId)
                        .andOnSuccess(projectFinance -> buildViabilityResource(viabilityProcess, projectFinance))
                );
    }

    @Override
    public ServiceResult<EligibilityResource> getEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId) {

        long projectId = projectOrganisationCompositeId.getProjectId();
        long organisationId = projectOrganisationCompositeId.getOrganisationId();

        return getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(this::getEligibilityProcess)
                .andOnSuccess(eligibilityProcess -> getProjectFinance(projectId, organisationId)
                        .andOnSuccess(projectFinance -> buildEligibilityResource(eligibilityProcess, projectFinance))
                );
    }

    @Override
    public ServiceResult<FundingRulesResource> getFundingRules(ProjectOrganisationCompositeId projectOrganisationCompositeId) {

        long projectId = projectOrganisationCompositeId.getProjectId();
        long organisationId = projectOrganisationCompositeId.getOrganisationId();

        return getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(this::getFundingRulesProcess)
                .andOnSuccess(fundingRulesProcess -> getProjectFinance(projectId, organisationId)
                        .andOnSuccess(projectFinance -> buildFundingRulesResource(fundingRulesProcess, projectFinance))
                );
    }

    @Override
    @Transactional
    public ServiceResult<Void> saveFundingRules(ProjectOrganisationCompositeId projectOrganisationCompositeId, FundingRules fundingRules) {
        long organisationId = projectOrganisationCompositeId.getOrganisationId();
        long projectId = projectOrganisationCompositeId.getProjectId();

        return getCurrentlyLoggedInUser().andOnSuccess(currentUser ->
                getPartnerOrganisation(projectId, organisationId)
                        .andOnSuccess(partnerOrganisation -> getFundingRulesProcess(partnerOrganisation)
                                .andOnSuccess(() -> getProjectFinance(projectId, organisationId))
                                .andOnSuccess(projectFinance -> triggerFundingRulesWorkflowEvent(currentUser, partnerOrganisation, FundingRulesState.REVIEW)
                                        .andOnSuccess(() -> saveFundingRules(projectFinance, fundingRules))
                                )
                        ));
    }

    @Override
    @Transactional
    public ServiceResult<Void> approveFundingRules(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        long organisationId = projectOrganisationCompositeId.getOrganisationId();
        long projectId = projectOrganisationCompositeId.getProjectId();
        return getCurrentlyLoggedInUser().andOnSuccess(currentUser ->
                getPartnerOrganisation(projectId, organisationId)
                        .andOnSuccess(partnerOrganisation -> triggerFundingRulesApprovalWorkflowHandlerEvent(currentUser, partnerOrganisation))
        );
    }

    @Override
    @Transactional
    public ServiceResult<Void> saveViability(ProjectOrganisationCompositeId projectOrganisationCompositeId, ViabilityState viability, ViabilityRagStatus viabilityRagStatus) {
        long organisationId = projectOrganisationCompositeId.getOrganisationId();
        long projectId = projectOrganisationCompositeId.getProjectId();

        return getCurrentlyLoggedInUser().andOnSuccess(currentUser ->
                getPartnerOrganisation(projectId, organisationId)
                        .andOnSuccess(partnerOrganisation -> getViabilityProcess(partnerOrganisation)
                                .andOnSuccess(viabilityProcess -> validateViability(projectId, viabilityProcess.getProcessState(), viability, viabilityRagStatus))
                                .andOnSuccess(() -> getProjectFinance(projectId, organisationId))
                                .andOnSuccess(projectFinance -> triggerViabilityWorkflowEvent(currentUser, partnerOrganisation, viability, null)
                                        .andOnSuccess(() -> saveViability(projectFinance, viabilityRagStatus))
                                )
                        ));
    }

    @Override
    @Transactional
    public ServiceResult<Void> resetViability(Long projectId, Long organisationId, String reason) {
        projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId).ifPresent(projectFinance -> {
            viabilityWorkflowHandler.viabilityReset(getPartnerOrganisation(projectId, organisationId).getSuccess(), getCurrentlyLoggedInUser().getSuccess(), reason);
            projectFinance.setViabilityStatus(ViabilityRagStatus.UNSET);
            deleteSpendProfileAndResetGol(projectId);
        });

        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> resetEligibility(Long projectId, Long organisationId, String reason) {
        projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId).ifPresent(projectFinance -> {
            eligibilityWorkflowHandler.eligibilityReset(getPartnerOrganisation(projectId, organisationId).getSuccess(), getCurrentlyLoggedInUser().getSuccess(), reason);
            projectFinance.setEligibilityStatus(EligibilityRagStatus.UNSET);
            deleteSpendProfileAndResetGol(projectId);
        });
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> resetFinanceChecks(Long projectId) {
        projectFinanceRepository.findByProjectId(projectId).forEach(projectFinance -> {
            long organisationId = projectFinance.getOrganisation().getId();
            resetViability(projectId, organisationId, "Finance reset");
            resetEligibility(projectId, organisationId, "Finance reset");
        });

        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> saveEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId, EligibilityState eligibility, EligibilityRagStatus eligibilityRagStatus) {

        long projectId = projectOrganisationCompositeId.getProjectId();
        long organisationId = projectOrganisationCompositeId.getOrganisationId();

        return getCurrentlyLoggedInUser().andOnSuccess(currentUser -> getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(partnerOrganisation -> getEligibilityProcess(partnerOrganisation)
                        .andOnSuccess(eligibilityProcess -> validateEligibility(projectId, eligibilityProcess.getProcessState(), eligibility, eligibilityRagStatus))
                        .andOnSuccess(() -> getProjectFinance(projectId, organisationId))
                        .andOnSuccess(projectFinance -> triggerEligibilityWorkflowEvent(currentUser, partnerOrganisation, eligibility, null)
                                .andOnSuccess(() -> saveEligibility(projectFinance, eligibilityRagStatus)))));
    }

    @Override
    @Transactional
    public ServiceResult<Void> saveCreditReport(long projectId, long organisationId, boolean reportPresent) {

        return getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(this::validateCreditReport)
                .andOnSuccess(() -> getProjectFinance(projectId, organisationId))
                .andOnSuccessReturnVoid(projectFinance -> {

                    projectFinance.setCreditReportConfirmed(reportPresent);
                    projectFinanceRepository.save(projectFinance);

                });
    }

    private void deleteSpendProfileAndResetGol(Long projectId) {
        if (projectRepository.findById(projectId).get().isSpendProfileGenerated()) {
            spendProfileService.deleteSpendProfile(projectId);
        }

        if (grantOfferLetterProcessRepository.findOneByTargetId(projectId).isInState(SENT)) {
            grantOfferLetterService.resetGrantOfferLetter(projectId);
        }
    }

    private ServiceResult<ProjectFinance> getProjectFinance(Long projectId, Long organisationId) {
        return find(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId), notFoundError(ProjectFinance.class, projectId, organisationId));
    }

    private ServiceResult<Void> validateCreditReport(PartnerOrganisation partnerOrganisation) {

        return getViabilityProcess(partnerOrganisation)
                .andOnSuccess(viabilityProcess -> {
                    if (ViabilityState.APPROVED == viabilityProcess.getProcessState()) {
                        return serviceFailure(VIABILITY_HAS_ALREADY_BEEN_APPROVED);
                    } else {
                        return serviceSuccess();
                    }
                });
    }

    private ServiceResult<ViabilityProcess> getViabilityProcess(PartnerOrganisation partnerOrganisation) {
        return serviceSuccess(viabilityWorkflowHandler.getProcess(partnerOrganisation));
    }

    private ServiceResult<PaymentMilestoneProcess> getPaymentMilestoneProcess(PartnerOrganisation partnerOrganisation) {
        return serviceSuccess(paymentMilestoneWorkflowHandler.getProcess(partnerOrganisation));
    }

    private ServiceResult<ViabilityResource> buildViabilityResource(ViabilityProcess viabilityProcess, ProjectFinance projectFinance) {

        ViabilityResource viabilityResource = new ViabilityResource(convertViabilityState(viabilityProcess.getProcessState()), projectFinance.getViabilityStatus());

        if (viabilityProcess.getLastModified() != null) {
            if (ViabilityState.APPROVED == viabilityProcess.getProcessState()) {
                viabilityResource.setViabilityApprovalDate(viabilityProcess.getLastModified().toLocalDate());
                setViabilityApprovalUser(viabilityResource, viabilityProcess.getInternalParticipant());
            } else if (ViabilityState.REVIEW == viabilityProcess.getProcessState()) {
                viabilityResource.setViabilityResetDate(viabilityProcess.getLastModified().toLocalDate());
                setViabilityResetUser(viabilityResource, viabilityProcess.getInternalParticipant());
            }
        }

        return serviceSuccess(viabilityResource);
    }

    private ServiceResult<FundingRulesResource> buildFundingRulesResource(FundingRulesProcess fundingRulesProcess, ProjectFinance projectFinance) {
        FundingRulesResource fundingRulesResource = new FundingRulesResource();

        FundingRules fundingRules;
        if (Boolean.TRUE == projectFinance.getNorthernIrelandDeclaration()) {
            fundingRules = FundingRules.STATE_AID;
        } else {
            fundingRules = FundingRules.SUBSIDY_CONTROL;
        }

        fundingRulesResource.setFundingRules(fundingRules);

        if (fundingRulesProcess != null) {
            fundingRulesResource.setFundingRulesState(fundingRulesProcess.getProcessState());
            if (fundingRulesProcess.getLastModified() != null) {
                fundingRulesResource.setFundingRulesLastModifiedDate(fundingRulesProcess.getLastModified().toLocalDate());
            }
            setFundingRulesLastModifiedUser(fundingRulesResource, fundingRulesProcess.getInternalParticipant());
        }

        return serviceSuccess(fundingRulesResource);
    }

    private ViabilityState convertViabilityState(ViabilityState viabilityState) {

        ViabilityState viability;

        switch (viabilityState) {
            case REVIEW:
                viability = ViabilityState.REVIEW;
                break;
            case NOT_APPLICABLE:
                viability = ViabilityState.NOT_APPLICABLE;
                break;
            case APPROVED:
                viability = ViabilityState.APPROVED;
                break;
            default:
                viability = ViabilityState.REVIEW;
        }

        return viability;

    }

    private void setViabilityApprovalUser(ViabilityResource viabilityResource, User viabilityApprovalUser) {
        if (viabilityApprovalUser != null) {
            viabilityResource.setViabilityApprovalUserFirstName(viabilityApprovalUser.getFirstName());
            viabilityResource.setViabilityApprovalUserLastName(viabilityApprovalUser.getLastName());
        }
    }

    private void setFundingRulesLastModifiedUser(FundingRulesResource fundingRulesResource, User fundingRulesLastModifiedUser) {

        if (fundingRulesLastModifiedUser != null) {
            fundingRulesResource.setFundingRulesInternalUserFirstName(fundingRulesLastModifiedUser.getFirstName());
            fundingRulesResource.setFundingRulesInternalUserLastName(fundingRulesLastModifiedUser.getLastName());
        }
    }

    private void setViabilityResetUser(ViabilityResource viabilityResource, User viabilityResetUser) {
        if (viabilityResetUser != null) {
            viabilityResource.setViabilityResetUserFirstName(viabilityResetUser.getFirstName());
            viabilityResource.setViabilityResetUserLastName(viabilityResetUser.getLastName());
        }
    }

    private ServiceResult<EligibilityProcess> getEligibilityProcess(PartnerOrganisation partnerOrganisation) {
        return serviceSuccess(eligibilityWorkflowHandler.getProcess(partnerOrganisation));
    }

    private ServiceResult<FundingRulesProcess> getFundingRulesProcess(PartnerOrganisation partnerOrganisation) {
        return serviceSuccess(fundingRulesWorkflowHandler.getProcess(partnerOrganisation));
    }

    private ServiceResult<EligibilityResource> buildEligibilityResource(EligibilityProcess eligibilityProcess, ProjectFinance projectFinance) {
        EligibilityResource eligibilityResource = new EligibilityResource(eligibilityProcess.getProcessState(), projectFinance.getEligibilityStatus());

        if (eligibilityProcess.getLastModified() != null) {
            if (EligibilityState.APPROVED == eligibilityProcess.getProcessState()) {
                eligibilityResource.setEligibilityApprovalDate(eligibilityProcess.getLastModified().toLocalDate());
                setEligibilityApprovalUser(eligibilityResource, eligibilityProcess.getInternalParticipant());
            } else if (EligibilityState.REVIEW == eligibilityProcess.getProcessState()) {
                eligibilityResource.setEligibilityResetDate(eligibilityProcess.getLastModified().toLocalDate());
                setEligibilityResetUser(eligibilityResource, eligibilityProcess.getInternalParticipant());
            }
        }

        return serviceSuccess(eligibilityResource);
    }

    private ServiceResult<PaymentMilestoneResource> buildProjectProcurementMilestoneResource(PaymentMilestoneProcess paymentMilestoneProcess) {

        if (paymentMilestoneProcess.getInternalParticipant() == null) {
            return serviceSuccess(new PaymentMilestoneResource(
                    paymentMilestoneProcess.getProcessState(),
                    paymentMilestoneProcess.getLastModified().toLocalDate()
            ));
        }

        return serviceSuccess(new PaymentMilestoneResource(paymentMilestoneProcess.getProcessState(),
                paymentMilestoneProcess.getInternalParticipant().getFirstName(),
                paymentMilestoneProcess.getInternalParticipant().getLastName(),
                paymentMilestoneProcess.getLastModified().toLocalDate()
        ));
    }

    private void setEligibilityApprovalUser(EligibilityResource eligibilityResource, User eligibilityApprovalUser) {
        if (eligibilityApprovalUser != null) {
            eligibilityResource.setEligibilityApprovalUserFirstName(eligibilityApprovalUser.getFirstName());
            eligibilityResource.setEligibilityApprovalUserLastName(eligibilityApprovalUser.getLastName());
        }
    }

    private void setEligibilityResetUser(EligibilityResource eligibilityResource, User eligibilityResetUser) {
        if (eligibilityResetUser != null) {
            eligibilityResource.setEligibilityResetUserFirstName(eligibilityResetUser.getFirstName());
            eligibilityResource.setEligibilityResetUserLastName(eligibilityResetUser.getLastName());
        }
    }

    private ServiceResult<Void> validateViability(long projectId, ViabilityState currentViabilityState, ViabilityState viability, ViabilityRagStatus viabilityRagStatus) {

        if (ViabilityState.APPROVED == currentViabilityState) {
            Optional<Project> project = projectRepository.findById(projectId);

            if (!(ViabilityState.REVIEW == viability && project.isPresent() && !project.get().isSpendProfileGenerated())) {
                return serviceFailure(VIABILITY_HAS_ALREADY_BEEN_APPROVED);
            }
        }

        if (ViabilityState.APPROVED == viability && ViabilityRagStatus.UNSET == viabilityRagStatus) {
            return serviceFailure(VIABILITY_RAG_STATUS_MUST_BE_SET);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> triggerViabilityWorkflowEvent(User currentUser, PartnerOrganisation partnerOrganisation, ViabilityState viability, String reason) {

        if (ViabilityState.APPROVED == viability) {
            viabilityWorkflowHandler.viabilityApproved(partnerOrganisation, currentUser);
        }

        if (ViabilityState.REVIEW == viability) {
            viabilityWorkflowHandler.viabilityReset(partnerOrganisation, currentUser, reason);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> triggerFundingRulesWorkflowEvent(User currentUser, PartnerOrganisation partnerOrganisation, FundingRulesState fundingRulesState) {
        if (FundingRulesState.APPROVED == fundingRulesState) {
            fundingRulesWorkflowHandler.fundingRulesApproved(partnerOrganisation, currentUser);
        } else {
            fundingRulesWorkflowHandler.fundingRulesUpdated(partnerOrganisation, currentUser);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> saveViability(ProjectFinance projectFinance, ViabilityRagStatus viabilityRagStatus) {

        projectFinance.setViabilityStatus(viabilityRagStatus);
        projectFinanceRepository.save(projectFinance);

        return serviceSuccess();
    }

    private ServiceResult<Void> saveFundingRules(ProjectFinance projectFinance, FundingRules fundingRules) {

        Boolean niDeclaration = FundingRules.STATE_AID == fundingRules;
        projectFinance.setNorthernIrelandDeclaration(niDeclaration);
        projectFinanceRepository.save(projectFinance);

        return serviceSuccess();
    }

    private ServiceResult<Void> validateEligibility(long projectId, EligibilityState currentEligibilityState, EligibilityState eligibility, EligibilityRagStatus eligibilityRagStatus) {

        if (EligibilityState.APPROVED == currentEligibilityState) {
            Optional<Project> project = projectRepository.findById(projectId);

            if (!(EligibilityState.REVIEW == eligibility && project.isPresent() && !project.get().isSpendProfileGenerated())) {
                return serviceFailure(ELIGIBILITY_HAS_ALREADY_BEEN_APPROVED);
            }
        }

        if (EligibilityState.APPROVED == eligibility && EligibilityRagStatus.UNSET == eligibilityRagStatus) {
            return serviceFailure(ELIGIBILITY_RAG_STATUS_MUST_BE_SET);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> triggerEligibilityWorkflowEvent(User currentUser, PartnerOrganisation partnerOrganisation, EligibilityState eligibility, String reason) {

        if (EligibilityState.APPROVED == eligibility) {
            eligibilityWorkflowHandler.eligibilityApproved(partnerOrganisation, currentUser);
        }
        if (EligibilityState.REVIEW == eligibility) {
            eligibilityWorkflowHandler.eligibilityReset(partnerOrganisation, currentUser, reason);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> saveEligibility(ProjectFinance projectFinance, EligibilityRagStatus eligibilityRagStatus) {

        projectFinance.setEligibilityStatus(eligibilityRagStatus);
        projectFinanceRepository.save(projectFinance);

        return serviceSuccess();
    }
}
