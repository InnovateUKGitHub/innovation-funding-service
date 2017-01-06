package org.innovateuk.ifs.project.finance.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.finance.domain.*;
import org.innovateuk.ifs.project.finance.repository.FinanceCheckProcessRepository;
import org.innovateuk.ifs.project.finance.repository.FinanceCheckRepository;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.finance.workflow.financechecks.configuration.FinanceCheckWorkflowHandler;
import org.innovateuk.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.util.GraphBuilderContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import static org.innovateuk.ifs.project.finance.resource.FinanceCheckState.APPROVED;
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
    private ProjectService projectService;

    @Autowired
    private OrganisationFinanceDelegate organisationFinanceDelegate;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Override
    public ServiceResult<FinanceCheckResource> getByProjectAndOrganisation(ProjectOrganisationCompositeId key) {
        return find(financeCheckRepository.findByProjectIdAndOrganisationId(key.getProjectId(), key.getOrganisationId()),
                notFoundError(FinanceCheck.class, key)).
                andOnSuccessReturn(this::mapToResource);
    }

    @Override
    public ServiceResult<Void> save(FinanceCheckResource financeCheckResource) {
        return validate(financeCheckResource).andOnSuccess(() -> {
            FinanceCheck toSave = mapToDomain(financeCheckResource);
            financeCheckRepository.save(toSave);

            return getCurrentlyLoggedInUser().
                    andOnSuccess(user -> getPartnerOrganisation(toSave.getProject().getId(), toSave.getOrganisation().getId()).
                            andOnSuccessReturn(partnerOrganisation -> financeCheckWorkflowHandler.financeCheckFiguresEdited(partnerOrganisation, user))).
                    andOnSuccess(workflowResult -> workflowResult ? serviceSuccess() : serviceFailure(CommonFailureKeys.FINANCE_CHECKS_CANNOT_PROGRESS_WORKFLOW));
        });
    }

    @Override
    public ServiceResult<Void> approve(Long projectId, Long organisationId) {

        return getCurrentlyLoggedInUser().andOnSuccess(currentUser ->
                getPartnerOrganisation(projectId, organisationId).andOnSuccessReturn(partnerOrg ->
                        financeCheckWorkflowHandler.approveFinanceCheckFigures(partnerOrg, currentUser)).
                        andOnSuccess(workflowResult -> workflowResult ? serviceSuccess() : serviceFailure(FINANCE_CHECKS_CANNOT_PROGRESS_WORKFLOW)));
    }

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
                        LocalDateTime.ofInstant(process.getLastModified().toInstant(), ZoneId.systemDefault()),
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
        Optional<SpendProfile> spendProfile = spendProfileRepository.findOneByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(0).getOrganisation().getId());
        List<ApplicationFinanceResource> applicationFinanceResourceList = financeRowService.financeTotals(application.getId()).getSuccessObject();

        BigDecimal totalProjectCost = calculateTotalForAllOrganisations(applicationFinanceResourceList, ApplicationFinanceResource::getTotal);
        BigDecimal totalFundingSought = calculateTotalForAllOrganisations(applicationFinanceResourceList, ApplicationFinanceResource::getTotalFundingSought);
        BigDecimal totalOtherFunding = calculateTotalForAllOrganisations(applicationFinanceResourceList, ApplicationFinanceResource::getTotalOtherFunding);
        BigDecimal totalPercentageGrant = calculateGrantPercentage(totalProjectCost, totalFundingSought);

        boolean financeChecksAllApproved = getFinanceCheckApprovalStatus(projectId);

        String spendProfileGeneratedBy = spendProfile.map(p -> p.getGeneratedBy().getName()).orElse(null);
        LocalDate spendProfileGeneratedDate = spendProfile.map(p -> LocalDate.from(p.getGeneratedDate().toInstant().atOffset(ZoneOffset.UTC))).orElse(null);

        return serviceSuccess(new FinanceCheckSummaryResource(project.getId(), project.getName(), competition.getId(), competition.getName(), project.getTargetStartDate(),
                project.getDurationInMonths().intValue(), totalProjectCost, totalFundingSought, totalOtherFunding, totalPercentageGrant, spendProfile.isPresent(),
                getPartnerStatuses(partnerOrganisations), financeChecksAllApproved, spendProfileGeneratedBy, spendProfileGeneratedDate));
    }

    private boolean getFinanceCheckApprovalStatus(Long projectId) {
        ServiceResult<ProjectTeamStatusResource> teamStatusResult = projectService.getProjectTeamStatus(projectId, Optional.empty());
        return teamStatusResult.isSuccess() && !simpleFindFirst(teamStatusResult.getSuccessObject().getPartnerStatuses(), s -> !asList(COMPLETE, NOT_REQUIRED).contains(s.getFinanceChecksStatus())).isPresent();
    }

    private List<FinanceCheckPartnerStatusResource> getPartnerStatuses(List<PartnerOrganisation> partnerOrganisations) {

        return mapWithIndex(partnerOrganisations, (i, org) -> {

            FinanceCheckProcessResource financeCheckStatus = getFinanceCheckApprovalStatus(org).getSuccessObjectOrThrowException();
            boolean financeChecksApproved = APPROVED.equals(financeCheckStatus.getCurrentState());

            Pair<Viability, ViabilityStatus> viability = getViability(org);

            FinanceCheckPartnerStatusResource.Eligibility eligibilityStatus = financeChecksApproved ?
                    FinanceCheckPartnerStatusResource.Eligibility.APPROVED :
                    FinanceCheckPartnerStatusResource.Eligibility.REVIEW;

            return new FinanceCheckPartnerStatusResource(
                org.getOrganisation().getId(),
                org.getOrganisation().getName(),
                viability.getLeft(), viability.getRight(),
                eligibilityStatus);
        });
    }

    private Pair<Viability, ViabilityStatus> getViability(PartnerOrganisation org) {

        ProjectOrganisationCompositeId viabilityId = new ProjectOrganisationCompositeId(
                org.getProject().getId(), org.getOrganisation().getId());

        ViabilityResource viabilityDetails = projectFinanceService.getViability(viabilityId).getSuccessObjectOrThrowException();

        return Pair.of(viabilityDetails.getViability(), viabilityDetails.getViabilityStatus());

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

    private BigDecimal calculateTotalForAllOrganisations(List<ApplicationFinanceResource> applicationFinanceResourceList, Function<ApplicationFinanceResource, BigDecimal> keyExtractor) {
        return applicationFinanceResourceList.stream().map(keyExtractor).reduce(ZERO, BigDecimal::add).setScale(0, HALF_EVEN);
    }

    private BigDecimal calculateGrantPercentage(BigDecimal projectTotal, BigDecimal totalFundingSought) {

        if (projectTotal.equals(ZERO)) {
            return ZERO;
        }

        return totalFundingSought.multiply(BigDecimal.valueOf(100)).divide(projectTotal, 0, HALF_EVEN);
    }

    ServiceResult<Void> validate(FinanceCheckResource toSave) {
        List<BigDecimal> costs = simpleMap(toSave.getCostGroup().getCosts(), CostResource::getValue);
        return aggregate(costNull(costs), costFractional(costs), costLessThanZeroErrors(costs)).andOnSuccess(() -> serviceSuccess());
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



    /*
    //TODO: INFUND-5508 - totals need to be switched to look at updated FC costs
    //List<FinanceCheckURIs> financeChecks = financeCheckRepository.findByProjectId(projectId);
    public BigDecimal getTotal(List<FinanceCheckURIs> financeChecks) {
        if (financeChecks == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = financeChecks.stream()
                .map(fc -> sumOf(fc.getCostGroup()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total == null) {
            return BigDecimal.ZERO;
        }

        return total;
    }

    private BigDecimal sumOf(CostGroup costGroup){
        return costGroup.getCosts().stream().map(Cost::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
    }*/
}
