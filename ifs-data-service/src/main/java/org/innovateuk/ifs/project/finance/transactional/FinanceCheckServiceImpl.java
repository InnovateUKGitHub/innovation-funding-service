package org.innovateuk.ifs.project.finance.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.util.PrioritySorting;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.finance.domain.*;
import org.innovateuk.ifs.project.finance.repository.FinanceCheckProcessRepository;
import org.innovateuk.ifs.project.finance.repository.FinanceCheckRepository;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceQueriesService;
import org.innovateuk.ifs.project.finance.workflow.financechecks.configuration.FinanceCheckWorkflowHandler;
import org.innovateuk.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.util.GraphBuilderContext;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.NOT_REQUIRED;
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
    private FinanceCheckWorkflowHandler financeCheckWorkflowHandler;

    @Autowired
    private FinanceCheckProcessRepository financeCheckProcessRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private ProjectFinanceRowService projectFinanceRowService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private ProjectFinanceQueriesService projectFinanceQueriesService;

    @Override
    public ServiceResult<FinanceCheckResource> getByProjectAndOrganisation(ProjectOrganisationCompositeId key) {
        return find(financeCheckRepository.findByProjectIdAndOrganisationId(key.getProjectId(), key.getOrganisationId()),
                notFoundError(FinanceCheck.class, key)).
                andOnSuccessReturn(this::mapToResource);

    }
    private BigDecimal percentDivisor = new BigDecimal("100");

    @Override
    public ServiceResult<FinanceCheckProcessResource> getFinanceCheckApprovalStatus(Long projectId, Long organisationId) {
        return getPartnerOrganisation(projectId, organisationId).andOnSuccess(this::getFinanceCheckApprovalStatus);
    }

    private ServiceResult<FinanceCheckProcessResource> getFinanceCheckApprovalStatus(PartnerOrganisation partnerOrganisation) {

        return findFinanceCheckProcess(partnerOrganisation).andOnSuccessReturn(process ->
                new FinanceCheckProcessResource(
                        process.getActivityState(),
                        projectUserMapper.mapToResource(process.getParticipant()),
                        userMapper.mapToResource(process.getInternalParticipant()),
                        ZonedDateTime.ofInstant(process.getLastModified().toInstant(), ZoneId.systemDefault()),
                        false));
    }

    private ServiceResult<FinanceCheckProcess> findFinanceCheckProcess(PartnerOrganisation partnerOrganisation) {
        return find(financeCheckProcessRepository.findOneByTargetId(partnerOrganisation.getId()), notFoundError(FinanceCheckProcess.class, partnerOrganisation.getId()));
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
        boolean financeChecksAllApproved = getFinanceCheckApprovalStatus(projectId);

        FinanceCheckOverviewResource overviewResource = getFinanceCheckOverview(projectId).getSuccessObjectOrThrowException();

        String spendProfileGeneratedBy = spendProfile.map(p -> p.getGeneratedBy().getName()).orElse(null);
        LocalDate spendProfileGeneratedDate = spendProfile.map(p -> LocalDate.from(p.getGeneratedDate().toInstant().atOffset(ZoneOffset.UTC))).orElse(null);

        return serviceSuccess(new FinanceCheckSummaryResource(overviewResource, competition.getId(), competition.getName(),
                spendProfile.isPresent(), getPartnerStatuses(sortedPartnersList, projectId), financeChecksAllApproved,
                spendProfileGeneratedBy, spendProfileGeneratedDate));
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

    public ServiceResult<FinanceCheckEligibilityResource> getFinanceCheckEligibilityDetails(Long projectId, Long organisationId) {
        Project project = projectRepository.findOne(projectId);
        Application application = project.getApplication();

        return projectFinanceRowService.financeChecksDetails(projectId, organisationId).andOnSuccess(projectFinance ->

            financeRowService.financeDetails(application.getId(), organisationId).
                    andOnSuccessReturn(applicationFinanceResource -> {

                        BigDecimal grantPercentage = BigDecimal.valueOf(applicationFinanceResource.getGrantClaimPercentage());
                        BigDecimal fundingSought = projectFinance.getTotal().multiply(grantPercentage).divide(percentDivisor);
                        FinanceCheckEligibilityResource eligibilityResource = new FinanceCheckEligibilityResource(project.getId(),
                                organisationId,
                                application.getDurationInMonths(),
                                projectFinance.getTotal(),
                                grantPercentage,
                                fundingSought,
                                projectFinance.getTotalOtherFunding(),
                                projectFinance.getTotal().subtract(fundingSought).subtract(projectFinance.getTotalOtherFunding()));
                        return eligibilityResource;
                    })
        );
    }

    private boolean getFinanceCheckApprovalStatus(Long projectId) {
        ServiceResult<ProjectTeamStatusResource> teamStatusResult = projectService.getProjectTeamStatus(projectId, Optional.empty());
        return teamStatusResult.isSuccess() && !simpleFindFirst(teamStatusResult.getSuccessObject().getPartnerStatuses(), s -> !asList(COMPLETE, NOT_REQUIRED).contains(s.getFinanceChecksStatus())).isPresent();
    }

    private List<FinanceCheckPartnerStatusResource> getPartnerStatuses(List<PartnerOrganisation> partnerOrganisations, Long projectId) {

        return mapWithIndex(partnerOrganisations, (i, org) -> {

            ProjectOrganisationCompositeId compositeId = getCompositeId(org);
            Pair<Viability, ViabilityRagStatus> viability = getViability(compositeId);
            Pair<Eligibility, EligibilityRagStatus> eligibility = getEligibility(compositeId);

            boolean anyQueryAwaitingResponse = isQueryActionRequired(projectId, org.getOrganisation().getId()).getSuccessObject();

            return new FinanceCheckPartnerStatusResource(org.getOrganisation().getId(), org.getOrganisation().getName(),
                    org.isLeadOrganisation(), viability.getLeft(), viability.getRight(), eligibility.getLeft(),
                    eligibility.getRight(), anyQueryAwaitingResponse);
        });
    }

    @Override
    public ServiceResult<Boolean> isQueryActionRequired(Long projectId, Long organisationId) {
        boolean actionRequired = false;

        ServiceResult<ProjectFinanceResource> resource = projectFinanceRowService.financeChecksDetails(projectId, organisationId);
        if(resource.isSuccess()) {
                ServiceResult<List<QueryResource>> queries = projectFinanceQueriesService.findAll(resource.getSuccessObject().getId());
                if(queries.isSuccess()) {
                    actionRequired |= queries.getSuccessObject().stream().anyMatch(q -> q.awaitingResponse);
                }
        }

        return serviceSuccess(actionRequired);
    }

    private ProjectOrganisationCompositeId getCompositeId(PartnerOrganisation org)  {
        return new ProjectOrganisationCompositeId(org.getProject().getId(), org.getOrganisation().getId());
    }

    private Pair<Viability, ViabilityRagStatus> getViability(ProjectOrganisationCompositeId compositeId) {

        ViabilityResource viabilityDetails = projectFinanceService.getViability(compositeId).getSuccessObjectOrThrowException();

        return Pair.of(viabilityDetails.getViability(), viabilityDetails.getViabilityRagStatus());

    }

    private Pair<Eligibility, EligibilityRagStatus> getEligibility(ProjectOrganisationCompositeId compositeId) {

        EligibilityResource eligibilityDetails = projectFinanceService.getEligibility(compositeId).getSuccessObjectOrThrowException();

        return Pair.of(eligibilityDetails.getEligibility(), eligibilityDetails.getEligibilityRagStatus());
    }

    private FinanceCheck mapToDomain(FinanceCheckResource financeCheckResource) {
        FinanceCheck fc = financeCheckRepository.findByProjectIdAndOrganisationId(financeCheckResource.getProject(), financeCheckResource.getOrganisation());
        for (CostResource cr : financeCheckResource.getCostGroup().getCosts()) {
            Optional<Cost> oc = fc.getCostGroup().getCostById(cr.getId());
            if (oc.isPresent()) {
                Cost c = oc.get();
                c.setValue(cr.getValue());
            }
        }
        return fc;
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

    ServiceResult<Void> validate(FinanceCheckResource toSave) {
        List<BigDecimal> costs = simpleMap(toSave.getCostGroup().getCosts(), CostResource::getValue);

        return getPartnerOrganisation(toSave.getProject(), toSave.getOrganisation()).andOnSuccess(
                partnerOrganisation -> {
                    OrganisationType organisationType = partnerOrganisation.getOrganisation().getOrganisationType();
                    if(organisationType.getId().equals(OrganisationTypeEnum.RESEARCH.getOrganisationTypeId())){
                        return aggregate(costNull(costs), costLessThanZeroErrors(costs)).andOnSuccess(() -> serviceSuccess());
                    } else {
                        return aggregate(costNull(costs), costFractional(costs), costLessThanZeroErrors(costs)).andOnSuccess(() -> serviceSuccess());
                    }
                }
        );
    }

    private ServiceResult<Void> costFractional(List<BigDecimal> costs) {
        for (BigDecimal cost : costs) {
            if (cost != null && cost.remainder(ONE).compareTo(ZERO) != 0) {
                return serviceFailure(new Error(FINANCE_CHECKS_CONTAINS_FRACTIONS_IN_COST, HttpStatus.BAD_REQUEST));
            }
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> costLessThanZeroErrors(List<BigDecimal> costs) {
        for (BigDecimal cost : costs) {
            if (cost != null && cost.compareTo(ZERO) < 0) {
                return serviceFailure(new Error(FINANCE_CHECKS_COST_LESS_THAN_ZERO, HttpStatus.BAD_REQUEST));
            }
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> costNull(List<BigDecimal> costs) {
        for (BigDecimal cost : costs) {
            if (cost == null) {
                return serviceFailure(new Error(FINANCE_CHECKS_COST_NULL, HttpStatus.BAD_REQUEST));
            }
        }
        return serviceSuccess();
    }

    private BigDecimal getResearchParticipationPercentage(ServiceResult<Double> researchParticipationPercentage) {
        BigDecimal researchParticipationPercentageValue = BigDecimal.ZERO;
        if (researchParticipationPercentage.isSuccess() && researchParticipationPercentage.getSuccessObject() != null) {
            researchParticipationPercentageValue = BigDecimal.valueOf(researchParticipationPercentage.getSuccessObject());
        }
        return researchParticipationPercentageValue;
    }
}
