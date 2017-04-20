package org.innovateuk.ifs.project.financecheck.service;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.competitionsetup.CompetitionSetupTransactionalService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
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
import org.innovateuk.ifs.project.financecheck.domain.*;
import org.innovateuk.ifs.project.financecheck.repository.FinanceCheckRepository;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.util.GraphBuilderContext;
import org.innovateuk.ifs.util.PrioritySorting;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
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
    private UserMapper userMapper;

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private ProjectFinanceRowService projectFinanceRowService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SpendProfileService spendProfileService;

    @Autowired
    private FinanceCheckQueriesService financeCheckQueriesService;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private CompetitionSetupTransactionalService competitionSetupTransactionalService;

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
                spendProfile.isPresent(), getPartnerStatuses(sortedPartnersList, projectId), bankDetailsApproved,
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

    private boolean getBankDetailsApprovalStatus(Long projectId) {
        ServiceResult<ProjectTeamStatusResource> teamStatusResult = projectService.getProjectTeamStatus(projectId, Optional.empty());
        return teamStatusResult.isSuccess() && !simpleFindFirst(teamStatusResult.getSuccessObject().getPartnerStatuses(), s -> !asList(COMPLETE, NOT_REQUIRED).contains(s.getBankDetailsStatus())).isPresent();
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
                ServiceResult<List<QueryResource>> queries = financeCheckQueriesService.findAll(resource.getSuccessObject().getId());
                if(queries.isSuccess()) {
                    actionRequired |= queries.getSuccessObject().stream().anyMatch(q -> q.awaitingResponse);
                }
        }

        return serviceSuccess(actionRequired);
    }


    @Override
    public ServiceResult<Long> getTurnoverByOrganisationId(Long applicationId, Long organisationId) {
        return getByApplicationAndOrganisationId(applicationId, organisationId, FINANCIAL_YEAR_END, STAFF_TURNOVER);
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
        return getOnlyElementOrFail(formInputRepository.findByCompetitionIdAndTypeIn(app.getCompetition().getId(), asList(formInputType))).andOnSuccess((formInput) -> {
            List<FormInputResponse> inputResponse = formInputResponseRepository.findByApplicationIdAndFormInputId(app.getId(), formInput.getId())
                    .stream().filter(response -> organisationId.equals(response.getUpdatedBy().getOrganisationId())).collect(toList());
            return getOnlyElementOrFail(inputResponse);
        });
    }


    private ProjectOrganisationCompositeId getCompositeId(PartnerOrganisation org)  {
        return new ProjectOrganisationCompositeId(org.getProject().getId(), org.getOrganisation().getId());
    }

    private Pair<Viability, ViabilityRagStatus> getViability(ProjectOrganisationCompositeId compositeId) {

        ViabilityResource viabilityDetails = spendProfileService.getViability(compositeId).getSuccessObjectOrThrowException();

        return Pair.of(viabilityDetails.getViability(), viabilityDetails.getViabilityRagStatus());

    }

    private Pair<Eligibility, EligibilityRagStatus> getEligibility(ProjectOrganisationCompositeId compositeId) {

        EligibilityResource eligibilityDetails = spendProfileService.getEligibility(compositeId).getSuccessObjectOrThrowException();

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
}
