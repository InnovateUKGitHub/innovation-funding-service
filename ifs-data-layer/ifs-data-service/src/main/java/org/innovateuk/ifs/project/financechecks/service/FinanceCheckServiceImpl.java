package org.innovateuk.ifs.project.financechecks.service;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.competitionsetup.CompetitionSetupTransactionalService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.FormInputResponseRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.domain.*;
import org.innovateuk.ifs.project.financechecks.repository.FinanceCheckRepository;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.queries.transactional.FinanceCheckQueriesService;
import org.innovateuk.ifs.project.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.transactional.StatusService;
import org.innovateuk.ifs.project.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.util.GraphBuilderContext;
import org.innovateuk.ifs.util.PrioritySorting;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.NOT_REQUIRED;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.getOnlyElementOrFail;

/**
 * A transactional service for finance check functionality
 */
@Service
public class FinanceCheckServiceImpl extends AbstractProjectServiceImpl implements FinanceCheckService {

    @Autowired
    private FinanceCheckRepository financeCheckRepository;

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private ProjectFinanceRowService projectFinanceRowService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Autowired
    private FinanceCheckQueriesService financeCheckQueriesService;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private CompetitionSetupTransactionalService competitionSetupTransactionalService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    private BigDecimal percentDivisor = new BigDecimal("100");

    @Override
    public ServiceResult<FinanceCheckResource> getByProjectAndOrganisation(ProjectOrganisationCompositeId key) {
        return find(financeCheckRepository.findByProjectIdAndOrganisationId(key.getProjectId(), key.getOrganisationId()),
                notFoundError(FinanceCheck.class, key)).
                andOnSuccessReturn(this::mapToResource);

    }

    @Override
    public ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId) {
        Project project = projectRepository.findOne(projectId);
        Application application = project.getApplication();
        Competition competition = application.getCompetition();
        List<PartnerOrganisation> partnerOrganisations = partnerOrganisationRepository.findByProjectId(projectId);
        final PartnerOrganisation leadPartner = simpleFindFirst(partnerOrganisations, PartnerOrganisation::isLeadOrganisation).get();
        final List<PartnerOrganisation> sortedPartnersList = new PrioritySorting<>(partnerOrganisations, leadPartner, po -> po.getOrganisation().getName()).unwrap();
        Optional<SpendProfile> spendProfile = spendProfileRepository.findOneByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(0).getOrganisation().getId());
        boolean bankDetailsApproved = getBankDetailsApprovalStatus(projectId);

        FinanceCheckOverviewResource overviewResource = getFinanceCheckOverview(projectId).getSuccessObjectOrThrowException();

        String spendProfileGeneratedBy = spendProfile.map(p -> p.getGeneratedBy().getName()).orElse(null);
        LocalDate spendProfileGeneratedDate = spendProfile.map(p -> LocalDate.from(p.getGeneratedDate().toInstant().atOffset(ZoneOffset.UTC))).orElse(null);

        return serviceSuccess(new FinanceCheckSummaryResource(overviewResource, competition.getId(), competition.getName(),
                spendProfile.isPresent(), getPartnerStatuses(sortedPartnersList, project), bankDetailsApproved,
                spendProfileGeneratedBy, spendProfileGeneratedDate, application.getId()));
    }

    @Override
    public ServiceResult<FinanceCheckOverviewResource> getFinanceCheckOverview(Long projectId) {
        Project project = projectRepository.findOne(projectId);
        Application application = project.getApplication();
        Competition competition = application.getCompetition();

        List<ProjectFinanceResource> projectFinanceResourceList = projectFinanceRowService.financeChecksTotals(projectId).getSuccessObject();

        BigDecimal totalProjectCost = calculateTotalForAllOrganisations(projectFinanceResourceList, ProjectFinanceResource::getTotal);
        BigDecimal totalFundingSought = calculateTotalForAllOrganisations(projectFinanceResourceList, ProjectFinanceResource::getTotalFundingSought);
        BigDecimal totalOtherFunding = calculateTotalForAllOrganisations(projectFinanceResourceList, ProjectFinanceResource::getTotalOtherFunding);
        BigDecimal totalPercentageGrant = calculateGrantPercentage(totalProjectCost, totalFundingSought);

        ServiceResult<Double> researchParticipationPercentage = financeRowService.getResearchParticipationPercentageFromProject(project.getId());
        BigDecimal researchParticipationPercentageValue = getResearchParticipationPercentage(researchParticipationPercentage);

        BigDecimal competitionMaximumResearchPercentage = BigDecimal.valueOf(competition.getMaxResearchRatio());

        return serviceSuccess(new FinanceCheckOverviewResource(projectId, project.getName(), project.getTargetStartDate(), project.getDurationInMonths().intValue(),
                totalProjectCost, totalFundingSought, totalOtherFunding, totalPercentageGrant, researchParticipationPercentageValue, competitionMaximumResearchPercentage));
    }

    @Override
    public ServiceResult<FinanceCheckEligibilityResource> getFinanceCheckEligibilityDetails(Long projectId, Long organisationId) {
        Project project = projectRepository.findOne(projectId);
        Application application = project.getApplication();

        return projectFinanceRowService.financeChecksDetails(projectId, organisationId).andOnSuccess(projectFinance ->

            financeRowService.financeDetails(application.getId(), organisationId).
                    andOnSuccessReturn(applicationFinanceResource -> {

                        BigDecimal grantPercentage = BigDecimal.valueOf(applicationFinanceResource.getGrantClaimPercentage());
                        BigDecimal fundingSought = projectFinance.getTotal().multiply(grantPercentage).divide(percentDivisor);
                        return new FinanceCheckEligibilityResource(project.getId(),
                                organisationId,
                                application.getDurationInMonths(),
                                projectFinance.getTotal(),
                                grantPercentage,
                                fundingSought,
                                projectFinance.getTotalOtherFunding(),
                                projectFinance.getTotal().subtract(fundingSought).subtract(projectFinance.getTotalOtherFunding()));
                    })
        );
    }

    private boolean getBankDetailsApprovalStatus(Long projectId) {
        ServiceResult<ProjectTeamStatusResource> teamStatusResult = statusService.getProjectTeamStatus(projectId, Optional.empty());
        return teamStatusResult.isSuccess() && !simpleFindFirst(teamStatusResult.getSuccessObject().getPartnerStatuses(), s -> !asList(COMPLETE, NOT_REQUIRED).contains(s.getBankDetailsStatus())).isPresent();
    }

    private List<FinanceCheckPartnerStatusResource> getPartnerStatuses(List<PartnerOrganisation> partnerOrganisations, Project project) {

        return mapWithIndex(partnerOrganisations, (i, org) -> {

            ProjectOrganisationCompositeId compositeId = getCompositeId(org);
            Pair<Viability, ViabilityRagStatus> viability = getViabilityStatus(compositeId);
            Pair<Eligibility, EligibilityRagStatus> eligibility = getEligibilityStatus(compositeId);

            boolean anyQueryAwaitingResponse = isQueryActionRequired(project.getId(), org.getOrganisation().getId()).getSuccessObject();

            return new FinanceCheckPartnerStatusResource(org.getOrganisation().getId(), org.getOrganisation().getName(),
                    org.isLeadOrganisation(), viability.getLeft(), viability.getRight(), eligibility.getLeft(),
                    eligibility.getRight(), anyQueryAwaitingResponse, getFinanceContact(project, org.getOrganisation()).isPresent());
        });
    }

    @Override
    public ServiceResult<Boolean> isQueryActionRequired(Long projectId, Long organisationId) {
        boolean actionRequired = false;

        ServiceResult<ProjectFinanceResource> resource = projectFinanceRowService.financeChecksDetails(projectId, organisationId);
        if(resource.isSuccess()) {
                ServiceResult<List<QueryResource>> queries = financeCheckQueriesService.findAll(resource.getSuccessObject().getId());
                if(queries.isSuccess()) {
                    actionRequired = queries.getSuccessObject().stream().anyMatch(q -> q.awaitingResponse);
                }
        }

        return serviceSuccess(actionRequired);
    }


    @Override
    public ServiceResult<Long> getTurnoverByOrganisationId(Long applicationId, Long organisationId) {
        return getByApplicationAndOrganisationId(applicationId, organisationId, FINANCIAL_YEAR_END, ORGANISATION_TURNOVER);
    }

    @Override
    public ServiceResult<Long> getHeadCountByOrganisationId(Long applicationId, Long organisationId) {
        return getByApplicationAndOrganisationId(applicationId, organisationId, FINANCIAL_STAFF_COUNT, STAFF_COUNT);
    }

    private ServiceResult<Long> getByApplicationAndOrganisationId(Long applicationId, Long organisationId, FormInputType financeType, FormInputType nonFinanceType) {
        Application app = applicationRepository.findOne(applicationId);
        return competitionSetupTransactionalService.isIncludeGrowthTable(app.getCompetition().getId()).
                andOnSuccess((isIncludeGrowthTable) -> {
                    if (isIncludeGrowthTable) {
                        return getOnlyForApplication(app, organisationId, financeType).andOnSuccessReturn(result -> Long.parseLong(result.getValue()));
                    } else {
                        return getOnlyForApplication(app, organisationId, nonFinanceType).andOnSuccessReturn(result -> Long.parseLong(result.getValue()));
                    }
                });
    }

    private ServiceResult<FormInputResponse> getOnlyForApplication(Application app, Long organisationId, FormInputType formInputType) {
        return getOnlyElementOrFail(formInputRepository.findByCompetitionIdAndTypeIn(app.getCompetition().getId(), singletonList(formInputType))).andOnSuccess((formInput) -> {
            List<FormInputResponse> inputResponse = formInputResponseRepository.findByApplicationIdAndFormInputId(app.getId(), formInput.getId())
                    .stream().filter(response -> organisationId.equals(response.getUpdatedBy().getOrganisationId())).collect(toList());
            return getOnlyElementOrFail(inputResponse);
        });
    }


    private ProjectOrganisationCompositeId getCompositeId(PartnerOrganisation org)  {
        return new ProjectOrganisationCompositeId(org.getProject().getId(), org.getOrganisation().getId());
    }

    private Pair<Viability, ViabilityRagStatus> getViabilityStatus(ProjectOrganisationCompositeId compositeId) {

        ViabilityResource viabilityDetails = getViability(compositeId).getSuccessObjectOrThrowException();

        return Pair.of(viabilityDetails.getViability(), viabilityDetails.getViabilityRagStatus());

    }

    private Pair<Eligibility, EligibilityRagStatus> getEligibilityStatus(ProjectOrganisationCompositeId compositeId) {

        EligibilityResource eligibilityDetails = getEligibility(compositeId).getSuccessObjectOrThrowException();

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

    private BigDecimal calculateTotalForAllOrganisations(List<ProjectFinanceResource> projectFinanceResourceList, Function<ProjectFinanceResource, BigDecimal> keyExtractor) {
        return projectFinanceResourceList.stream().map(keyExtractor).reduce(ZERO, BigDecimal::add).setScale(0, HALF_EVEN);
    }

    private BigDecimal calculateGrantPercentage(BigDecimal projectTotal, BigDecimal totalFundingSought) {

        if (projectTotal.equals(ZERO)) {
            return ZERO;
        }

        return totalFundingSought.multiply(BigDecimal.valueOf(100)).divide(projectTotal, 0, HALF_EVEN);
    }

    private BigDecimal getResearchParticipationPercentage(ServiceResult<Double> researchParticipationPercentage) {
        BigDecimal researchParticipationPercentageValue = BigDecimal.ZERO;
        if (researchParticipationPercentage.isSuccess() && researchParticipationPercentage.getSuccessObject() != null) {
            researchParticipationPercentageValue = BigDecimal.valueOf(researchParticipationPercentage.getSuccessObject());
        }
        return researchParticipationPercentageValue;
    }

    @Override
    public ServiceResult<Boolean> getCreditReport(Long projectId, Long organisationId) {
        return getProjectFinance(projectId, organisationId).andOnSuccessReturn(ProjectFinance::getCreditReportConfirmed);
    }

    @Override
    public ServiceResult<List<ProjectFinanceResource>> getProjectFinances(Long projectId) {
        return projectFinanceRowService.financeChecksTotals(projectId);
    }

    @Override
    public ServiceResult<ViabilityResource> getViability(ProjectOrganisationCompositeId projectOrganisationCompositeId) {

        Long projectId = projectOrganisationCompositeId.getProjectId();
        Long organisationId = projectOrganisationCompositeId.getOrganisationId();

        return getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(this::getViabilityProcess)
                .andOnSuccess(viabilityProcess -> getProjectFinance(projectId, organisationId)
                        .andOnSuccess(projectFinance -> buildViabilityResource(viabilityProcess, projectFinance))
                );
    }

    @Override
    public ServiceResult<EligibilityResource> getEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId) {

        Long projectId = projectOrganisationCompositeId.getProjectId();
        Long organisationId = projectOrganisationCompositeId.getOrganisationId();

        return getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(this::getEligibilityProcess)
                .andOnSuccess(eligibilityProcess -> getProjectFinance(projectId, organisationId)
                        .andOnSuccess(projectFinance -> buildEligibilityResource(eligibilityProcess, projectFinance))
                );
    }

    @Override
    @Transactional
    public ServiceResult<Void> saveViability(ProjectOrganisationCompositeId projectOrganisationCompositeId, Viability viability, ViabilityRagStatus viabilityRagStatus) {

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

    @Override
    @Transactional
    public ServiceResult<Void> saveEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId, Eligibility eligibility, EligibilityRagStatus eligibilityRagStatus) {

        Long projectId = projectOrganisationCompositeId.getProjectId();
        Long organisationId = projectOrganisationCompositeId.getOrganisationId();

        return getCurrentlyLoggedInUser().andOnSuccess(currentUser -> getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(partnerOrganisation -> getEligibilityProcess(partnerOrganisation)
                        .andOnSuccess(eligibilityProcess -> validateEligibility(eligibilityProcess.getActivityState(), eligibility, eligibilityRagStatus))
                        .andOnSuccess(() -> getProjectFinance(projectId, organisationId))
                        .andOnSuccess(projectFinance -> triggerEligibilityWorkflowEvent(currentUser, partnerOrganisation, eligibility)
                                .andOnSuccess(() -> saveEligibility(projectFinance, eligibilityRagStatus)))));
    }

    @Override
    @Transactional
    public ServiceResult<Void> saveCreditReport(Long projectId, Long organisationId, boolean reportPresent) {

        return getPartnerOrganisation(projectId, organisationId)
                .andOnSuccess(this::validateCreditReport)
                .andOnSuccess(() -> getProjectFinance(projectId, organisationId))
                .andOnSuccessReturnVoid(projectFinance -> {

                    projectFinance.setCreditReportConfirmed(reportPresent);
                    projectFinanceRepository.save(projectFinance);

                });
    }

    private ServiceResult<ProjectFinance> getProjectFinance(Long projectId, Long organisationId) {
        return find(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId), notFoundError(ProjectFinance.class, projectId, organisationId));
    }

    private ServiceResult<PartnerOrganisation> getPartnerOrganisation(Long projectId, Long organisationId) {
        return find(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId), notFoundError(PartnerOrganisation.class, projectId, organisationId));
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

    private ServiceResult<ViabilityProcess> getViabilityProcess(PartnerOrganisation partnerOrganisation) {
        return serviceSuccess(viabilityWorkflowHandler.getProcess(partnerOrganisation));
    }

    private ServiceResult<ViabilityResource> buildViabilityResource(ViabilityProcess viabilityProcess, ProjectFinance projectFinance) {

        ViabilityResource viabilityResource = new ViabilityResource(convertViabilityState(viabilityProcess.getActivityState()), projectFinance.getViabilityStatus());

        if (viabilityProcess.getLastModified() != null) {
            viabilityResource.setViabilityApprovalDate(viabilityProcess.getLastModified().toLocalDate());
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

    private ServiceResult<EligibilityProcess> getEligibilityProcess(PartnerOrganisation partnerOrganisation) {

        return serviceSuccess(eligibilityWorkflowHandler.getProcess(partnerOrganisation));
    }

    private ServiceResult<EligibilityResource> buildEligibilityResource(EligibilityProcess eligibilityProcess, ProjectFinance projectFinance) {
        EligibilityResource eligibilityResource = new EligibilityResource(convertEligibilityState(eligibilityProcess.getActivityState()), projectFinance.getEligibilityStatus());

        if (eligibilityProcess.getLastModified() != null) {
            eligibilityResource.setEligibilityApprovalDate(eligibilityProcess.getLastModified().toLocalDate());
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
}
