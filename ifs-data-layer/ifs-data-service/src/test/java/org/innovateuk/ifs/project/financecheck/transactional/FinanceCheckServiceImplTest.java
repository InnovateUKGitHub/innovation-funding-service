package org.innovateuk.ifs.project.financecheck.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.domain.*;
import org.innovateuk.ifs.project.financechecks.repository.FinanceCheckRepository;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckServiceImpl;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.FundingRulesWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.PaymentMilestoneWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.domain.GOLProcess;
import org.innovateuk.ifs.project.grantofferletter.repository.GrantOfferLetterProcessRepository;
import org.innovateuk.ifs.project.queries.transactional.FinanceCheckQueriesService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.transactional.StatusService;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.util.PrioritySorting;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static freemarker.template.utility.Collections12.singletonList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.PROCUREMENT;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.ExcludedCostCategoryBuilder.newExcludedCostCategory;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaimPercentage;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostBuilder.newOtherFunding;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostCategoryBuilder.newOtherFundingCostCategory;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.finance.resource.category.LabourCostCategory.WORKING_DAYS_PER_YEAR;
import static org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.form.resource.SectionType.PAYMENT_MILESTONES;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.*;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.financecheck.builder.CostBuilder.newCost;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.financecheck.builder.CostGroupBuilder.newCostGroup;
import static org.innovateuk.ifs.project.financecheck.builder.FinanceCheckBuilder.newFinanceCheck;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState.PENDING;
import static org.innovateuk.ifs.project.spendprofile.builder.SpendProfileBuilder.newSpendProfile;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FinanceCheckServiceImplTest extends BaseServiceUnitTest<FinanceCheckServiceImpl> {
    private Long applicationId = 123L;
    private Long organisationId = 234L;
    private Long projectId = 789L;

    @Mock
    private FinanceCheckRepository financeCheckRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Mock
    private SpendProfileRepository spendProfileRepository;

    @Mock
    private ProjectFinanceService projectFinanceService;

    @Mock
    private StatusService statusService;

    @Mock
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Mock
    private PaymentMilestoneWorkflowHandler paymentMilestoneWorkflowHandler;

    @Mock
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Mock
    private FundingRulesWorkflowHandler fundingRulesWorkflowHandler;

    @Mock
    private ProjectFinanceRepository projectFinanceRepository;

    @Mock
    private FinanceCheckQueriesService financeCheckQueriesService;

    @Mock
    private ApplicationFinanceService financeService;

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Mock
    private GrantOfferLetterProcessRepository grantOfferLetterProcessRepository;

    @Test
    public void getByProjectAndOrganisationNotFound() {
        // Set up
        ProjectOrganisationCompositeId compositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        when(financeCheckRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(null);
        // Method under test
        ServiceResult<FinanceCheckResource> result = service.getByProjectAndOrganisation(compositeId);
        // Assertions
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FinanceCheck.class, compositeId)));
    }

    @Test
    public void getByProjectAndOrganisation() {
        // Set up
        ProjectOrganisationCompositeId compositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        FinanceCheck financeCheck = newFinanceCheck().
                withProject(newProject().with(id(projectId)).build()).
                withOrganisation(newOrganisation().with(id(organisationId)).build()).
                withCostGroup(newCostGroup().
                        withCosts(newCost().
                                withValue("1", "2").
                                withCostCategory(
                                        newCostCategory().
                                                withName("cat 1", "cat 2").
                                                buildArray(2, CostCategory.class)).
                                build(2)).
                        build()).
                build();
        // Method under test
        when(financeCheckRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(financeCheck);
        ServiceResult<FinanceCheckResource> result = service.getByProjectAndOrganisation(compositeId);
        // Assertions - basically testing the deserialisation into resource objects
        assertTrue(result.isSuccess());
        assertEquals(financeCheck.getId(), result.getSuccess().getId());
        assertEquals(financeCheck.getOrganisation().getId(), result.getSuccess().getOrganisation());
        assertEquals(financeCheck.getProject().getId(), result.getSuccess().getProject());
        assertEquals(financeCheck.getCostGroup().getId(), result.getSuccess().getCostGroup().getId());
        assertEquals(financeCheck.getCostGroup().getCosts().size(), result.getSuccess().getCostGroup().getCosts().size());
        assertEquals(financeCheck.getCostGroup().getCosts().get(0).getCostCategory().getId(), result.getSuccess().getCostGroup().getCosts().get(0).getCostCategory().getId());
    }

    @Test
    public void getFinanceCheckSummary(){
        Section section = newSection().withSectionType(PAYMENT_MILESTONES).build();
        Competition competition = newCompetition()
                .withSections(Arrays.asList(section))
                .withFundingType(PROCUREMENT).withMaxResearchRatio(2).build();

        Application application = newApplication().withId(applicationId).withCompetition(competition).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).build();

        Organisation[] organisations = newOrganisation().
                withOrganisationType(BUSINESS, RESEARCH, BUSINESS).
                buildArray(3, Organisation.class);

        List<PartnerOrganisation> partnerOrganisations = newPartnerOrganisation()
                .withProject(project)
                .withLeadOrganisation(false, true, false)
                .withOrganisation(organisations).
                build(3);

        User projectFinanceUser = newUser().withFirstName("Project").withLastName("Finance").build();
        Optional<SpendProfile> spendProfile = Optional.of(newSpendProfile().withGeneratedBy(projectFinanceUser).withGeneratedDate(new GregorianCalendar()).build());
        List<ProjectFinanceResource> projectFinanceResourceList = newProjectFinanceResource().withGrantClaimPercentage(BigDecimal.valueOf(20)).build(3);
        ProjectTeamStatusResource projectTeamStatus = newProjectTeamStatusResource().build();

        List<ApplicationFinanceResource> applicationFinanceResources = newApplicationFinanceResource().
                withApplication(applicationId)
                .withGrantClaimPercentage(BigDecimal.valueOf(20)).
                build(3);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(financeService.financeTotals(project.getApplication().getId())).thenReturn(serviceSuccess(applicationFinanceResources));
        when(partnerOrganisationRepository.findByProjectId(projectId)).thenReturn(partnerOrganisations);
        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(0).getOrganisation().getId())).thenReturn(spendProfile);
        when(projectFinanceService.financeChecksTotals(project.getId())).thenReturn(serviceSuccess(projectFinanceResourceList));
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(serviceSuccess(projectTeamStatus));

        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisations[0].getId())).thenReturn(partnerOrganisations.get(0));
        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisations[1].getId())).thenReturn(partnerOrganisations.get(1));
        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisations[2].getId())).thenReturn(partnerOrganisations.get(2));

        when(eligibilityWorkflowHandler.getProcess(partnerOrganisations.get(0))).thenReturn(new EligibilityProcess(projectFinanceUser, partnerOrganisations.get(0), EligibilityState.APPROVED));
        when(eligibilityWorkflowHandler.getProcess(partnerOrganisations.get(1))).thenReturn(new EligibilityProcess(projectFinanceUser, partnerOrganisations.get(1), EligibilityState.REVIEW));
        when(eligibilityWorkflowHandler.getProcess(partnerOrganisations.get(2))).thenReturn(new EligibilityProcess(projectFinanceUser, partnerOrganisations.get(2), EligibilityState.REVIEW));

        when(viabilityWorkflowHandler.getProcess(partnerOrganisations.get(0))).thenReturn(new ViabilityProcess(projectFinanceUser, partnerOrganisations.get(0), ViabilityState.APPROVED));
        when(viabilityWorkflowHandler.getProcess(partnerOrganisations.get(1))).thenReturn(new ViabilityProcess(projectFinanceUser, partnerOrganisations.get(1), ViabilityState.NOT_APPLICABLE));
        when(viabilityWorkflowHandler.getProcess(partnerOrganisations.get(2))).thenReturn(new ViabilityProcess(projectFinanceUser, partnerOrganisations.get(2), ViabilityState.REVIEW));

        when(paymentMilestoneWorkflowHandler.getProcess(partnerOrganisations.get(0))).thenReturn(new PaymentMilestoneProcess(projectFinanceUser, partnerOrganisations.get(0), PaymentMilestoneState.REVIEW));
        when(paymentMilestoneWorkflowHandler.getProcess(partnerOrganisations.get(1))).thenReturn(new PaymentMilestoneProcess(projectFinanceUser, partnerOrganisations.get(1), PaymentMilestoneState.REVIEW));
        when(paymentMilestoneWorkflowHandler.getProcess(partnerOrganisations.get(2))).thenReturn(new PaymentMilestoneProcess(projectFinanceUser, partnerOrganisations.get(2), PaymentMilestoneState.REVIEW));

        when(paymentMilestoneWorkflowHandler.paymentMilestoneApproved(partnerOrganisations.get(0), projectFinanceUser)).thenReturn(true);
        when(paymentMilestoneWorkflowHandler.paymentMilestoneApproved(partnerOrganisations.get(0), projectFinanceUser)).thenReturn(true);
        when(paymentMilestoneWorkflowHandler.paymentMilestoneApproved(partnerOrganisations.get(0), projectFinanceUser)).thenReturn(true);

        when(fundingRulesWorkflowHandler.getProcess(partnerOrganisations.get(0))).thenReturn(new FundingRulesProcess(projectFinanceUser, partnerOrganisations.get(0), FundingRulesState.REVIEW));
        when(fundingRulesWorkflowHandler.getProcess(partnerOrganisations.get(1))).thenReturn(new FundingRulesProcess(projectFinanceUser, partnerOrganisations.get(1), FundingRulesState.REVIEW));
        when(fundingRulesWorkflowHandler.getProcess(partnerOrganisations.get(2))).thenReturn(new FundingRulesProcess(projectFinanceUser, partnerOrganisations.get(2), FundingRulesState.REVIEW));

        ProjectFinance projectFinanceInDB1 = new ProjectFinance();
        projectFinanceInDB1.setViabilityStatus(ViabilityRagStatus.AMBER);
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(0).getOrganisation().getId())).thenReturn(Optional.of(projectFinanceInDB1));
        ProjectFinance projectFinanceInDB2 = new ProjectFinance();
        projectFinanceInDB2.setViabilityStatus(ViabilityRagStatus.UNSET);
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(1).getOrganisation().getId())).thenReturn(Optional.of(projectFinanceInDB2));
        ProjectFinance projectFinanceInDB3 = new ProjectFinance();
        projectFinanceInDB3.setViabilityStatus(ViabilityRagStatus.UNSET);
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(2).getOrganisation().getId())).thenReturn(Optional.of(projectFinanceInDB3));

        ProjectFinanceResource[] projectFinanceResources = newProjectFinanceResource().withId(234L, 345L, 456L).withOrganisation(organisations[0].getId(), organisations[1].getId(), organisations[2].getId()).buildArray(3, ProjectFinanceResource.class);
        when(projectFinanceService.financeChecksDetails(projectId, organisations[0].getId())).thenReturn(ServiceResult.serviceSuccess(projectFinanceResources[0]));
        when(projectFinanceService.financeChecksDetails(projectId, organisations[1].getId())).thenReturn(ServiceResult.serviceSuccess(projectFinanceResources[1]));
        when(projectFinanceService.financeChecksDetails(projectId, organisations[2].getId())).thenReturn(ServiceResult.serviceSuccess(projectFinanceResources[2]));

        QueryResource queryResource1 = new QueryResource(12L, 23L, new ArrayList<>(), FinanceChecksSectionType.ELIGIBILITY, "Title" , true, ZonedDateTime.now(), null, null);
        QueryResource queryResource2 = new QueryResource(12L, 23L, new ArrayList<>(), FinanceChecksSectionType.ELIGIBILITY, "Title" , false, ZonedDateTime.now(), null, null);
        when(financeCheckQueriesService.findAll(234L)).thenReturn(serviceSuccess(Arrays.asList(queryResource1)));
        when(financeCheckQueriesService.findAll(345L)).thenReturn(serviceSuccess(new ArrayList<>()));
        when(financeCheckQueriesService.findAll(456L)).thenReturn(serviceSuccess(Arrays.asList(queryResource2)));
        when(projectFinanceService.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceSuccess(Double.valueOf(3.0)));

        ServiceResult<FinanceCheckSummaryResource> result = service.getFinanceCheckSummary(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckSummaryResource summary = result.getSuccess();
        List<FinanceCheckPartnerStatusResource> partnerStatuses = summary.getPartnerStatusResources();
        assertEquals(3, partnerStatuses.size());

        assertTrue(organisationsOrderedWithLeadOnTopAndPartnersAlphabetically(partnerOrganisations,
                simpleMap(summary.getPartnerStatusResources(), FinanceCheckPartnerStatusResource::getName)));

        FinanceCheckPartnerStatusResource organisation1Results = partnerStatuses.get(0);
        assertEquals(ViabilityState.NOT_APPLICABLE, organisation1Results.getViability());
        assertEquals(ViabilityRagStatus.UNSET, organisation1Results.getViabilityRagStatus());
        assertEquals(PaymentMilestoneState.REVIEW, organisation1Results.getPaymentMilestoneState());
        assertFalse(organisation1Results.isAwaitingResponse());

        FinanceCheckPartnerStatusResource organisation2Results = partnerStatuses.get(1);
        assertEquals(ViabilityState.APPROVED, organisation2Results.getViability());
        assertEquals(ViabilityRagStatus.AMBER, organisation2Results.getViabilityRagStatus());
        assertEquals(PaymentMilestoneState.REVIEW, organisation2Results.getPaymentMilestoneState());
        assertTrue(organisation2Results.isAwaitingResponse());

        FinanceCheckPartnerStatusResource organisation3Results = partnerStatuses.get(2);
        assertEquals(ViabilityState.REVIEW, organisation3Results.getViability());
        assertEquals(ViabilityRagStatus.UNSET, organisation3Results.getViabilityRagStatus());
        assertEquals(PaymentMilestoneState.REVIEW, organisation3Results.getPaymentMilestoneState());
        assertFalse(organisation3Results.isAwaitingResponse());
    }

    private boolean organisationsOrderedWithLeadOnTopAndPartnersAlphabetically(List<PartnerOrganisation> beforeOrdered, List<String> organisationsNames) {
        PartnerOrganisation leadPartner = simpleFindFirst(beforeOrdered, PartnerOrganisation::isLeadOrganisation).get();
        List<PartnerOrganisation> orderedPartnerOrganisations = new PrioritySorting<>(beforeOrdered, leadPartner, po -> po.getOrganisation().getName()).unwrap();
        return organisationsNames.equals(simpleMap(orderedPartnerOrganisations, po -> po.getOrganisation().getName()));
    }

    @Test
    public void resetFinanceChecks() {
        User internalUser = newUser().withRoles(singleton(PROJECT_FINANCE)).build();
        Organisation organisation = newOrganisation().withId(organisationId).build();
        ProjectFinance projectFinance = newProjectFinance()
                .withOrganisation(organisation)
                .build();
        PartnerOrganisation partnerOrganisation = newPartnerOrganisation()
                .withOrganisation(organisation)
                .build();
        Project project = newProject()
                .withId(projectId)
                .withSpendProfileSubmittedDate(null)
                .withPartnerOrganisations(singletonList(partnerOrganisation))
                .withGrantOfferLetter(null)
                .build();
        Long userId = 7L;
        User user = newUser().withId(userId).build();
        setUpSaveEligibilityMocking(partnerOrganisation, user, EligibilityState.APPROVED);
        setUpSaveViabilityMocking(user, partnerOrganisation, ViabilityState.APPROVED);
        GOLProcess currentGOLProcess = new GOLProcess((ProjectUser) null, project, PENDING);

        when(projectFinanceRepository.findByProjectId(project.getId())).thenReturn(singletonList(projectFinance));
        when(viabilityWorkflowHandler.viabilityReset(partnerOrganisation, internalUser, null)).thenReturn(true);
        when(eligibilityWorkflowHandler.eligibilityReset(partnerOrganisation, internalUser, null)).thenReturn(true);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(grantOfferLetterProcessRepository.findOneByTargetId(project.getId())).thenReturn(currentGOLProcess);

        ServiceResult<Void> result = service.resetFinanceChecks(projectId);

        assertTrue(result.isSuccess());
        assertEquals(EligibilityRagStatus.UNSET, projectFinance.getEligibilityStatus());
        assertEquals(ViabilityRagStatus.UNSET, projectFinance.getViabilityStatus());
    }

    @Test
    public void getFinanceCheckEligibility(){
        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(BUSINESS).withId(organisationId).withName("Organisation1").build();

        Map<FinanceRowType, FinanceRowCostCategory> projectFinances = createProjectFinance();
        projectFinances.forEach((type, category) -> category.calculateTotal());
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().
                withProject(projectId).
                withOrganisation(organisation.getId()).
                withFinanceOrganisationDetails(projectFinances).
                build();


        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(organisationRepository.findById(organisationId)).thenReturn(Optional.of(organisation));
        when(projectFinanceService.financeChecksDetails(projectId, organisationId)).thenReturn(serviceSuccess(projectFinanceResource));
        when(applicationFinanceRepository.existsByApplicationIdAndOrganisationId(application.getId(), organisationId)).thenReturn(false);
        ServiceResult<FinanceCheckEligibilityResource> result = service.getFinanceCheckEligibilityDetails(projectId, organisationId);
        assertTrue(result.isSuccess());

        FinanceCheckEligibilityResource eligibility = result.getSuccess();

        assertEquals((long) eligibility.getDurationInMonths(), 6L);
        assertEquals(projectFinanceResource.getTotal(), eligibility.getTotalCost());
        assertEquals(projectFinanceResource.getGrantClaimPercentage(), eligibility.getPercentageGrant());
        assertEquals(projectFinanceResource.getTotalFundingSought(), eligibility.getFundingSought());
        assertEquals(projectFinanceResource.getTotalOtherFunding(), eligibility.getOtherPublicSectorFunding());
        assertEquals(projectFinanceResource.getTotalContribution(), eligibility.getContributionToProject());
        assertFalse(eligibility.isHasApplicationFinances());
    }

    @Test
    public void getFinanceCheckEligibilityNoProjectFinances(){
        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(organisationRepository.findById(organisationId)).thenReturn(Optional.of(organisation));
        when(projectFinanceService.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(GENERAL_NOT_FOUND));

        ServiceResult<FinanceCheckEligibilityResource> result = service.getFinanceCheckEligibilityDetails(projectId, organisationId);
        assertTrue(result.isFailure());

    }

    @Test
    public void queryActionRequired() {

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();
        QueryResource fakeQuery = new QueryResource(1L, 1L, Collections.emptyList(), FinanceChecksSectionType.ELIGIBILITY, "", true, ZonedDateTime.now(), null, null);
        List<QueryResource> queries = Collections.singletonList(fakeQuery);

        when(projectFinanceService.financeChecksDetails(projectId, organisationId)).thenReturn(serviceSuccess(resource));
        when(financeCheckQueriesService.findAll(resource.getId())).thenReturn(serviceSuccess(queries));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertTrue(result.getSuccess());
    }

    @Test
    public void queryNoActionRequired() {

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();
        QueryResource fakeQuery = new QueryResource(1L, 1L, Collections.emptyList(), FinanceChecksSectionType.ELIGIBILITY, "", false, ZonedDateTime.now(), null, null);
        List<QueryResource> queries = Collections.singletonList(fakeQuery);

        when(projectFinanceService.financeChecksDetails(projectId, organisationId)).thenReturn(serviceSuccess(resource));
        when(financeCheckQueriesService.findAll(resource.getId())).thenReturn(serviceSuccess(queries));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccess());
    }

    @Test
    public void queryWithNoProjectFinance() {

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();
        QueryResource fakeQuery = new QueryResource(1L, 1L, Collections.emptyList(), FinanceChecksSectionType.ELIGIBILITY, "", false, ZonedDateTime.now(), null, null);
        List<QueryResource> queries = Collections.singletonList(fakeQuery);

        when(projectFinanceService.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(internalServerErrorError()));
        when(financeCheckQueriesService.findAll(resource.getId())).thenReturn(serviceSuccess(queries));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccess());
    }

    @Test
    public void queryWithNoQueries() {
        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();
        List<QueryResource> queries = Collections.emptyList();

        when(projectFinanceService.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(internalServerErrorError()));
        when(financeCheckQueriesService.findAll(resource.getId())).thenReturn(serviceSuccess(queries));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccess());
    }

    @Test
    public void queryWithQueriesFailure() {

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();

        when(projectFinanceService.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(internalServerErrorError()));
        when(financeCheckQueriesService.findAll(resource.getId())).thenReturn(serviceFailure(internalServerErrorError()));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccess());
    }

    @Test
    public void queryWithNullQueries() {

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();

        when(projectFinanceService.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(internalServerErrorError()));
        when(financeCheckQueriesService.findAll(resource.getId())).thenReturn(serviceSuccess(null));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccess());
    }

    @Test
    public void getFinanceCheckOverview() {

        Competition competition = newCompetition().withMaxResearchRatio(2).build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation[] organisations = newOrganisation().
                withOrganisationType(BUSINESS, RESEARCH, BUSINESS).
                buildArray(3, Organisation.class);

        List<PartnerOrganisation> partnerOrganisations = newPartnerOrganisation().
                withProject(project).
                withOrganisation(organisations).
                build(3);

        Map<FinanceRowType, FinanceRowCostCategory> projectFinances = createProjectFinance();

        Map<FinanceRowType, FinanceRowCostCategory> applicationFinances = asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withGrossEmployeeCost(new BigDecimal("1.0"), BigDecimal.ZERO).
                                withDescription("Developers", WORKING_DAYS_PER_YEAR).
                                withLabourDays(1, 200).
                                build(2)).
                        build(),
                FinanceRowType.MATERIALS, newDefaultCostCategory().withCosts(
                        newMaterials().
                                withCost(new BigDecimal("1.0")).
                                withQuantity(1).
                                build(1)).
                        build(),
                FinanceRowType.OTHER_FUNDING, newOtherFundingCostCategory().withCosts(
                        newOtherFunding().
                                withOtherPublicFunding("Yes", "").
                                withFundingSource(OTHER_FUNDING, "other funding").
                                withFundingAmount(null, BigDecimal.valueOf(2)).
                                build(2)).
                        build());

        projectFinances.forEach((type, category) -> category.calculateTotal());
        applicationFinances.forEach((type, category) -> category.calculateTotal());

        List<ProjectFinanceResource> projectFinanceResource = newProjectFinanceResource().
                withProject(projectId).
                withFinanceOrganisationDetails(projectFinances).
                build(2);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectFinanceService.financeChecksTotals(projectId)).thenReturn(serviceSuccess(projectFinanceResource));
        when(financeService.financeTotals(project.getApplication().getId())).thenReturn(serviceSuccess(emptyList()));
        when(projectFinanceService.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceSuccess(3.0));

        ServiceResult<FinanceCheckOverviewResource> result = service.getFinanceCheckOverview(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckOverviewResource overview = result.getSuccess();
        assertEquals(projectId, overview.getProjectId());
        assertEquals(6, overview.getDurationInMonths());
        assertEquals(new BigDecimal("10000066"), overview.getTotalProjectCost());
        assertEquals(new BigDecimal("2998020"), overview.getGrantAppliedFor());
        assertEquals(new BigDecimal("2000"), overview.getOtherPublicSectorFunding());
        assertEquals(new BigDecimal("29.98"), overview.getTotalPercentageGrant());
        assertEquals(BigDecimal.valueOf(2), overview.getCompetitionMaximumResearchPercentage());
        assertEquals(BigDecimal.valueOf(3.0), overview.getResearchParticipationPercentage());
    }

    @Test
    public void getFinanceCheckOverviewNoResearch() {

        Competition competition = newCompetition().withMaxResearchRatio(2).build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation[] organisations = newOrganisation().
                withOrganisationType(BUSINESS, RESEARCH, BUSINESS).
                buildArray(3, Organisation.class);

        List<PartnerOrganisation> partnerOrganisations = newPartnerOrganisation().
                withProject(project).
                withOrganisation(organisations).
                build(3);

        Map<FinanceRowType, FinanceRowCostCategory> projectFinances = createProjectFinance();

        Map<FinanceRowType, FinanceRowCostCategory> applicationFinances = asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withGrossEmployeeCost(new BigDecimal("1.0"), BigDecimal.ZERO).
                                withDescription("Developers", WORKING_DAYS_PER_YEAR).
                                withLabourDays(1, 200).
                                build(2)).
                        build(),
                FinanceRowType.MATERIALS, newDefaultCostCategory().withCosts(
                        newMaterials().
                                withCost(new BigDecimal("1.0")).
                                withQuantity(1).
                                build(1)).
                        build(),
                FinanceRowType.OTHER_FUNDING, newOtherFundingCostCategory().withCosts(
                        newOtherFunding().
                                withOtherPublicFunding("Yes", "").
                                withFundingSource(OTHER_FUNDING, "other funding").
                                withFundingAmount(null, BigDecimal.valueOf(2)).
                                build(2)).
                        build());

        projectFinances.forEach((type, category) -> category.calculateTotal());
        applicationFinances.forEach((type, category) -> category.calculateTotal());

        List<ProjectFinanceResource> projectFinanceResource = newProjectFinanceResource().
                withProject(projectId).
                withFinanceOrganisationDetails(projectFinances).
                build(2);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectFinanceService.financeChecksTotals(projectId)).thenReturn(serviceSuccess(projectFinanceResource));
        when(financeService.financeTotals(project.getApplication().getId())).thenReturn(serviceSuccess(emptyList()));
        when(projectFinanceService.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceFailure(GENERAL_FORBIDDEN));

        ServiceResult<FinanceCheckOverviewResource> result = service.getFinanceCheckOverview(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckOverviewResource overview = result.getSuccess();
        assertEquals(projectId, overview.getProjectId());
        assertEquals(6, overview.getDurationInMonths());
        assertEquals(new BigDecimal("10000066"), overview.getTotalProjectCost());
        assertEquals(new BigDecimal("2998020"), overview.getGrantAppliedFor());
        assertEquals(new BigDecimal("2000"), overview.getOtherPublicSectorFunding());
        assertEquals(new BigDecimal("29.98"), overview.getTotalPercentageGrant());
        assertEquals(BigDecimal.valueOf(2), overview.getCompetitionMaximumResearchPercentage());
        assertEquals(BigDecimal.valueOf(0), overview.getResearchParticipationPercentage());
    }

    @Test
    public void getFinanceCheckOverviewResearchNull() {

        Competition competition = newCompetition().withMaxResearchRatio(2).build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation[] organisations = newOrganisation().
                withOrganisationType(BUSINESS, RESEARCH, BUSINESS).
                buildArray(3, Organisation.class);

        List<PartnerOrganisation> partnerOrganisations = newPartnerOrganisation().
                withProject(project).
                withOrganisation(organisations).
                build(3);

        Map<FinanceRowType, FinanceRowCostCategory> projectFinances = createProjectFinance();

        Map<FinanceRowType, FinanceRowCostCategory> applicationFinances = asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withGrossEmployeeCost(new BigDecimal("1.0"), BigDecimal.ZERO).
                                withDescription("Developers", WORKING_DAYS_PER_YEAR).
                                withLabourDays(1, 200).
                                build(2)).
                        build(),
                FinanceRowType.MATERIALS, newDefaultCostCategory().withCosts(
                        newMaterials().
                                withCost(new BigDecimal("1.0")).
                                withQuantity(1).
                                build(1)).
                        build(),
                FinanceRowType.OTHER_FUNDING, newOtherFundingCostCategory().withCosts(
                        newOtherFunding().
                                withOtherPublicFunding("Yes", "").
                                withFundingSource(OTHER_FUNDING, "other funding").
                                withFundingAmount(null, BigDecimal.valueOf(2)).
                                build(2)).
                        build());

        projectFinances.forEach((type, category) -> category.calculateTotal());
        applicationFinances.forEach((type, category) -> category.calculateTotal());

        List<ProjectFinanceResource> projectFinanceResource = newProjectFinanceResource().
                withProject(projectId).
                withFinanceOrganisationDetails(projectFinances).
                build(2);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectFinanceService.financeChecksTotals(projectId)).thenReturn(serviceSuccess(projectFinanceResource));
        when(financeService.financeTotals(project.getApplication().getId())).thenReturn(serviceSuccess(emptyList()));
        when(projectFinanceService.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceSuccess(null));

        ServiceResult<FinanceCheckOverviewResource> result = service.getFinanceCheckOverview(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckOverviewResource overview = result.getSuccess();
        assertEquals(projectId, overview.getProjectId());
        assertEquals(6, overview.getDurationInMonths());
        assertEquals(new BigDecimal("10000066"), overview.getTotalProjectCost());
        assertEquals(new BigDecimal("2998020"), overview.getGrantAppliedFor());
        assertEquals(new BigDecimal("2000"), overview.getOtherPublicSectorFunding());
        assertEquals(new BigDecimal("29.98"), overview.getTotalPercentageGrant());
        assertEquals(BigDecimal.valueOf(2), overview.getCompetitionMaximumResearchPercentage());
        assertEquals(BigDecimal.valueOf(0), overview.getResearchParticipationPercentage());
    }

    @Override
    protected FinanceCheckServiceImpl supplyServiceUnderTest() {
        return new FinanceCheckServiceImpl();
    }

    private Map<FinanceRowType, FinanceRowCostCategory> createProjectFinance() {
        return asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withGrossEmployeeCost(new BigDecimal("10000000"), BigDecimal.ZERO).
                                withDescription("Developers", WORKING_DAYS_PER_YEAR).
                                withLabourDays(100, 200).
                                build(2)).
                        build(),
                FinanceRowType.MATERIALS, newDefaultCostCategory().withCosts(
                        newMaterials().
                                withCost(new BigDecimal("33.33")).
                                withQuantity(1).
                                build(1)).
                        build(),
                FinanceRowType.FINANCE, newExcludedCostCategory().withCosts(
                        newGrantClaimPercentage().
                                withGrantClaimPercentage(BigDecimal.valueOf(30)).
                                build(1)).
                        build(),
                FinanceRowType.OTHER_FUNDING, newOtherFundingCostCategory().withCosts(
                        newOtherFunding().
                                withOtherPublicFunding("Yes", "").
                                withFundingSource(OTHER_FUNDING, "Other funder").
                                withFundingAmount(null, BigDecimal.valueOf(1000)).
                                build(2)).
                        build());
    }

    @Test
    public void saveViabilityWhenViabilityAlreadyApproved() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveViabilityMocking(user, partnerOrganisationInDB, ViabilityState.APPROVED);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, ViabilityState.APPROVED, ViabilityRagStatus.AMBER);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(VIABILITY_HAS_ALREADY_BEEN_APPROVED));

        verify(projectFinanceRepository, never()).save(projectFinanceInDB);
        verify(viabilityWorkflowHandler, never()).viabilityApproved(partnerOrganisationInDB, user);

    }

    @Test
    public void saveViabilityWhenViabilityRagStatusIsUnset() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveViabilityMocking(user, partnerOrganisationInDB, ViabilityState.REVIEW);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, ViabilityState.APPROVED, ViabilityRagStatus.UNSET);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(VIABILITY_RAG_STATUS_MUST_BE_SET));

        verify(projectFinanceRepository, never()).save(projectFinanceInDB);
        verify(viabilityWorkflowHandler, never()).viabilityApproved(partnerOrganisationInDB, user);

    }

    @Test
    public void saveViabilityWhenViabilityRagStatusIsUnsetButViabilityAlsoNotApproved() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveViabilityMocking(user, partnerOrganisationInDB, ViabilityState.REVIEW);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, ViabilityState.REVIEW, ViabilityRagStatus.UNSET);

        assertTrue(result.isSuccess());

        assertSaveViabilityResults(projectFinanceInDB, ViabilityRagStatus.UNSET);
        verify(viabilityWorkflowHandler, never()).viabilityApproved(partnerOrganisationInDB, user);
    }

    @Test
    public void saveViabilityWhenViabilityRagStatusIsSetButViabilityNotApproved() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveViabilityMocking(user, partnerOrganisationInDB, ViabilityState.REVIEW);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, ViabilityState.REVIEW, ViabilityRagStatus.AMBER);

        assertTrue(result.isSuccess());

        assertSaveViabilityResults(projectFinanceInDB, ViabilityRagStatus.AMBER);

        verify(viabilityWorkflowHandler, never()).viabilityApproved(partnerOrganisationInDB, user);
    }

    @Test
    public void saveViabilityWhenViabilityApproved() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveViabilityMocking(user, partnerOrganisationInDB, ViabilityState.REVIEW);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, ViabilityState.APPROVED, ViabilityRagStatus.AMBER);

        assertTrue(result.isSuccess());

        assertSaveViabilityResults(projectFinanceInDB, ViabilityRagStatus.AMBER);

        // Ensure the workflow is called with the correct target and participant
        verify(viabilityWorkflowHandler).viabilityApproved(partnerOrganisationInDB, user);

    }

    private ProjectFinance setUpSaveViabilityMocking(User user, PartnerOrganisation partnerOrganisationInDB, ViabilityState viabilityStateInDB) {

        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ViabilityProcess viabilityProcess = new ViabilityProcess(user, partnerOrganisationInDB, viabilityStateInDB);
        when(viabilityWorkflowHandler.getProcess(partnerOrganisationInDB)).thenReturn(viabilityProcess);
        when(viabilityWorkflowHandler.getState(partnerOrganisationInDB)).thenReturn(viabilityStateInDB);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(projectFinanceInDB));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        return projectFinanceInDB;

    }

    private void assertSaveViabilityResults(ProjectFinance projectFinanceInDB, ViabilityRagStatus expectedViabilityRagStatus) {

        assertEquals(expectedViabilityRagStatus, projectFinanceInDB.getViabilityStatus());

        verify(projectFinanceRepository).save(projectFinanceInDB);
    }

    @Test
    public void saveEligibilityWhenEligibilityAlreadyApproved() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();
        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveEligibilityMocking(partnerOrganisationInDB, user, EligibilityState.APPROVED);

        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, EligibilityState.APPROVED, EligibilityRagStatus.AMBER);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(ELIGIBILITY_HAS_ALREADY_BEEN_APPROVED));

        verify(projectFinanceRepository, never()).save(projectFinanceInDB);
        verify(eligibilityWorkflowHandler, never()).eligibilityApproved(partnerOrganisationInDB, user);
    }

    @Test
    public void saveEligibilityWhenEligibilityApprovedButStatusIsUnset() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();
        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveEligibilityMocking(partnerOrganisationInDB, user, EligibilityState.REVIEW);

        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, EligibilityState.APPROVED, EligibilityRagStatus.UNSET);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(ELIGIBILITY_RAG_STATUS_MUST_BE_SET));

        verify(projectFinanceRepository, never()).save(projectFinanceInDB);
        verify(eligibilityWorkflowHandler, never()).eligibilityApproved(partnerOrganisationInDB, user);

    }

    @Test
    public void saveEligibilityWhenEligibilityNotApprovedAndStatusIsUnset() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveEligibilityMocking(partnerOrganisationInDB, user, EligibilityState.REVIEW);

        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, EligibilityState.REVIEW, EligibilityRagStatus.UNSET);

        assertTrue(result.isSuccess());

        assertSaveEligibilityResults(projectFinanceInDB, EligibilityRagStatus.UNSET);
        verify(eligibilityWorkflowHandler, never()).eligibilityApproved(partnerOrganisationInDB, user);
    }

    @Test
    public void saveEligibilityWhenEligibilityNotApprovedAndStatusIsSet() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveEligibilityMocking(partnerOrganisationInDB, user, EligibilityState.REVIEW);

        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, EligibilityState.REVIEW, EligibilityRagStatus.AMBER);

        assertTrue(result.isSuccess());

        assertSaveEligibilityResults(projectFinanceInDB, EligibilityRagStatus.AMBER);

        verify(eligibilityWorkflowHandler, never()).eligibilityApproved(partnerOrganisationInDB, user);
    }

    @Test
    public void saveEligibilityWhenEligibilityApprovedAndStatusIsSet() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveEligibilityMocking(partnerOrganisationInDB, user, EligibilityState.REVIEW);

        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, EligibilityState.APPROVED, EligibilityRagStatus.GREEN);

        assertTrue(result.isSuccess());

        assertSaveEligibilityResults(projectFinanceInDB, EligibilityRagStatus.GREEN);

        // Ensure the workflow is called with the correct target and participant
        verify(eligibilityWorkflowHandler).eligibilityApproved(partnerOrganisationInDB, user);
    }

    private ProjectFinance setUpSaveEligibilityMocking(PartnerOrganisation partnerOrganisationInDB, User user, EligibilityState eligibilityStateInDB) {

        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        EligibilityProcess eligibilityProcess = new EligibilityProcess(user, partnerOrganisationInDB, eligibilityStateInDB);
        when(eligibilityWorkflowHandler.getProcess(partnerOrganisationInDB)).thenReturn(eligibilityProcess);
        when(eligibilityWorkflowHandler.getState(partnerOrganisationInDB)).thenReturn(eligibilityStateInDB);

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(projectFinanceInDB));

        return projectFinanceInDB;
    }

    private void assertSaveEligibilityResults(ProjectFinance projectFinanceInDB, EligibilityRagStatus expectedEligibilityRagStatus) {

        assertEquals(expectedEligibilityRagStatus, projectFinanceInDB.getEligibilityStatus());

        verify(projectFinanceRepository).save(projectFinanceInDB);
    }

    @Test
    public void getCreditReportSuccess() {

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        projectFinanceInDB.setCreditReportConfirmed(true);
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(projectFinanceInDB));
        ServiceResult<Boolean> result = service.getCreditReport(projectId, organisationId);

        assertTrue(result.isSuccess());
        assertEquals(true, result.getSuccess());

        verify(projectFinanceRepository).findByProjectIdAndOrganisationId(projectId, organisationId);
    }

    @Test
    public void saveCreditSuccess() {

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ViabilityProcess viabilityProcess = new ViabilityProcess((User) null, partnerOrganisationInDB, ViabilityState.REVIEW);
        when(viabilityWorkflowHandler.getProcess(partnerOrganisationInDB)).thenReturn(viabilityProcess);

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(projectFinanceInDB));

        ServiceResult<Void> result = service.saveCreditReport(projectId, organisationId, true);

        assertTrue(result.isSuccess());

        assertTrue(projectFinanceInDB.getCreditReportConfirmed());
        verify(projectFinanceRepository).save(projectFinanceInDB);

    }

    @Test
    public void saveCreditFailsBecauseViabilityIsAlreadyApproved() {

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ViabilityProcess viabilityProcess = new ViabilityProcess((User) null, partnerOrganisationInDB, ViabilityState.APPROVED);
        when(viabilityWorkflowHandler.getProcess(partnerOrganisationInDB)).thenReturn(viabilityProcess);

        ServiceResult<Void> result = service.saveCreditReport(projectId, organisationId, true);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(VIABILITY_HAS_ALREADY_BEEN_APPROVED));
    }

    @Test
    public void getViabilityWhenPartnerOrganisationDoesNotExist() {

        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(null);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        ServiceResult<ViabilityResource> result = service.getViability(projectOrganisationCompositeId);

        assertTrue(result.isFailure());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), result.getErrors().get(0).getErrorKey());

    }

    @Test
    public void getViabilityForKtpAsKB() {

        setUpGetViabilityMocking(ViabilityState.NOT_APPLICABLE, ViabilityRagStatus.UNSET, null, null, true, true);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<ViabilityResource> result = service.getViability(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());
        assertGetViabilityResults(result.getSuccess(), ViabilityState.NOT_APPLICABLE, ViabilityRagStatus.UNSET,
                null, null, null);
    }

    @Test
    public void getViabilityForKtpAsNonKBOrg() {

        setUpGetViabilityMocking(ViabilityState.APPROVED, ViabilityRagStatus.GREEN, null, null, true, false);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<ViabilityResource> result = service.getViability(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());
        assertGetViabilityResults(result.getSuccess(), ViabilityState.APPROVED, ViabilityRagStatus.GREEN,
                null, null, null);
    }


    @Test
    public void getViabilityWhenViabilityStateIsReviewInDB() {

        setUpGetViabilityMocking(ViabilityState.REVIEW, ViabilityRagStatus.RED, null, null, false, false);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<ViabilityResource> result = service.getViability(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        ViabilityResource returnedViabilityResource = result.getSuccess();

        assertGetViabilityResults(returnedViabilityResource, ViabilityState.REVIEW, ViabilityRagStatus.RED,
                null, null, null);
    }

    @Test
    public void getViabilityWhenViabilityStateIsNotApplicableInDB() {

        setUpGetViabilityMocking(ViabilityState.NOT_APPLICABLE, ViabilityRagStatus.AMBER, null, null, false, false);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<ViabilityResource> result = service.getViability(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        ViabilityResource returnedViabilityResource = result.getSuccess();

        assertGetViabilityResults(returnedViabilityResource, ViabilityState.NOT_APPLICABLE, ViabilityRagStatus.AMBER,
                null, null, null);
    }

    @Test
    public void getViabilityWhenViabilityStateIsApproved() {

        Long userId = 7L;

        User user = newUser()
                .withId(userId)
                .withFirstName("Lee")
                .withLastName("Bowman")
                .build();

        setUpGetViabilityMocking(ViabilityState.APPROVED, ViabilityRagStatus.GREEN, user, LocalDate.now(), false, false);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<ViabilityResource> result = service.getViability(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        ViabilityResource returnedViabilityResource = result.getSuccess();

        assertGetViabilityResults(returnedViabilityResource, ViabilityState.APPROVED, ViabilityRagStatus.GREEN,
                "Lee", "Bowman", LocalDate.now());
    }

    private void setUpGetViabilityMocking(ViabilityState viabilityStateInDB, ViabilityRagStatus viabilityRagStatusInDB,
                                          User viabilityApprovalUser, LocalDate viabilityApprovalDate, boolean isKtp, boolean asKB) {

        Competition competition = newCompetition().withFundingType(isKtp ? FundingType.KTP : FundingType.LOAN).build();
        Application application = newApplication().withCompetition(competition).build();
        Project project = newProject().withApplication(application).build();
        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation()
                .withProject(project)
                .withOrganisation(newOrganisation()
                        .withOrganisationType(asKB ? KNOWLEDGE_BASE : BUSINESS)
                        .build())
                .build();
        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ViabilityProcess viabilityProcess = new ViabilityProcess(viabilityApprovalUser, partnerOrganisationInDB, viabilityStateInDB);
        if (viabilityApprovalDate != null) {
            viabilityProcess.setLastModified(viabilityApprovalDate.atStartOfDay(ZoneId.systemDefault()));
        }

        when(viabilityWorkflowHandler.getProcess(partnerOrganisationInDB)).thenReturn(viabilityProcess);

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        projectFinanceInDB.setViabilityStatus(viabilityRagStatusInDB);
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(projectFinanceInDB));

    }

    private void assertGetViabilityResults(ViabilityResource returnedViabilityResource, ViabilityState expectedViability, ViabilityRagStatus expectedViabilityRagStatus,
                                           String expectedViabilityApprovalUserFirstName, String expectedViabilityApprovalUserLastName,
                                           LocalDate expectedViabilityApprovalDate) {

        assertEquals(expectedViability, returnedViabilityResource.getViability());
        assertEquals(expectedViabilityRagStatus, returnedViabilityResource.getViabilityRagStatus());

        assertEquals(expectedViabilityApprovalUserFirstName, returnedViabilityResource.getViabilityApprovalUserFirstName());
        assertEquals(expectedViabilityApprovalUserLastName, returnedViabilityResource.getViabilityApprovalUserLastName());
        if (expectedViabilityApprovalDate != null) {
            assertEquals(expectedViabilityApprovalDate, returnedViabilityResource.getViabilityApprovalDate());
        }
    }

    @Test
    public void getEligibilityWhenPartnerOrganisationDoesNotExist() {

        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(null);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        ServiceResult<EligibilityResource> result = service.getEligibility(projectOrganisationCompositeId);

        assertTrue(result.isFailure());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), result.getErrors().get(0).getErrorKey());

    }

    @Test
    public void getEligibilityWhenEligibilityIsReviewInDB() {

        setGetEligibilityMocking(EligibilityState.REVIEW, EligibilityRagStatus.RED, null, null, false, false);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<EligibilityResource> result = service.getEligibility(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        EligibilityResource returnedEligibilityResource = result.getSuccess();

        assertGetEligibilityResults(returnedEligibilityResource, EligibilityState.REVIEW, EligibilityRagStatus.RED,
                null, null, null);

    }

    @Test
    public void getEligibilityWhenEligibilityIsNotApplicableInDB() {

        setGetEligibilityMocking(EligibilityState.NOT_APPLICABLE, EligibilityRagStatus.AMBER, null, null, false, false);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<EligibilityResource> result = service.getEligibility(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        EligibilityResource returnedEligibilityResource = result.getSuccess();

        assertGetEligibilityResults(returnedEligibilityResource, EligibilityState.NOT_APPLICABLE, EligibilityRagStatus.AMBER,
                null, null, null);

    }

    @Test
    public void getEligibilityWhenEligibilityIsApprovedInDB() {

        Long userId = 7L;

        User user = newUser()
                .withId(userId)
                .withFirstName("Lee")
                .withLastName("Bowman")
                .build();

        setGetEligibilityMocking(EligibilityState.APPROVED, EligibilityRagStatus.GREEN, user, LocalDate.now(), false, false);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<EligibilityResource> result = service.getEligibility(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        EligibilityResource returnedEligibilityResource = result.getSuccess();

        assertGetEligibilityResults(returnedEligibilityResource, EligibilityState.APPROVED, EligibilityRagStatus.GREEN,
                "Lee", "Bowman", LocalDate.now());

    }

    @Test
    public void getEligibilityForKtpAsNonKB() {
        setGetEligibilityMocking(EligibilityState.NOT_APPLICABLE, EligibilityRagStatus.UNSET, null, null, true, false);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<EligibilityResource> result = service.getEligibility(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        assertGetEligibilityResults(result.getSuccess(), EligibilityState.NOT_APPLICABLE, EligibilityRagStatus.UNSET,
                null, null, null);
    }

    @Test
    public void getEligibilityForKtpAsKB() {
        setGetEligibilityMocking(EligibilityState.APPROVED, EligibilityRagStatus.GREEN, null, null, true, true);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<EligibilityResource> result = service.getEligibility(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        assertGetEligibilityResults(result.getSuccess(), EligibilityState.APPROVED, EligibilityRagStatus.GREEN,
                null, null, null);
    }

    @Test
    public void approvePaymentMilestoneState() {
        Section section = newSection().withSectionType(PAYMENT_MILESTONES).build();
        Competition competition = newCompetition()
                .withSections(Arrays.asList(section))
                .withFundingType(PROCUREMENT).build();
        Application application = newApplication().withCompetition(competition).build();
        Project project = newProject().withApplication(application).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation()
                .withProject(project)
                .withOrganisation(newOrganisation()
                        .withOrganisationType(BUSINESS)
                        .build())
                .build();

        User user = newUser().withId(1l).build();
        setLoggedInUser(newUserResource().withId(user.getId()).build());

        PaymentMilestoneProcess paymentMilestoneProcess = new PaymentMilestoneProcess(user, partnerOrganisationInDB, PaymentMilestoneState.REVIEW);

        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);
        when(paymentMilestoneWorkflowHandler.getProcess(partnerOrganisationInDB)).thenReturn(paymentMilestoneProcess);
        when(paymentMilestoneWorkflowHandler.paymentMilestoneApproved(partnerOrganisationInDB, user)).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        ServiceResult<Void> result = service.approvePaymentMilestoneState(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void resetPaymentMilestoneState() {
        Section section = newSection().withSectionType(PAYMENT_MILESTONES).build();
        Competition competition = newCompetition()
                .withSections(Arrays.asList(section))
                .withFundingType(PROCUREMENT).build();

        Application application = newApplication().withCompetition(competition).build();
        Project project = newProject().withApplication(application).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation()
                .withProject(project)
                .withOrganisation(newOrganisation()
                        .withOrganisationType(BUSINESS)
                        .build())
                .build();
        GOLProcess currentGOLProcess = new GOLProcess((ProjectUser) null, project, PENDING);

        User user = newUser().withId(1l).build();
        setLoggedInUser(newUserResource().withId(user.getId()).build());

        PaymentMilestoneProcess paymentMilestoneProcess = new PaymentMilestoneProcess(user, partnerOrganisationInDB, PaymentMilestoneState.REVIEW);

        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);
        when(paymentMilestoneWorkflowHandler.getProcess(partnerOrganisationInDB)).thenReturn(paymentMilestoneProcess);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(paymentMilestoneWorkflowHandler.paymentMilestoneReset(partnerOrganisationInDB, user, "reason")).thenReturn(true);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(grantOfferLetterProcessRepository.findOneByTargetId(projectId)).thenReturn(currentGOLProcess);

        ServiceResult<Void> result = service.resetPaymentMilestoneState(projectOrganisationCompositeId, "reason");

        assertTrue(result.isSuccess());
    }

    @Test
    public void getPaymentMilestone() {
        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).build();
        Project project = newProject().withApplication(application).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation()
                .withProject(project)
                .withOrganisation(newOrganisation()
                        .withOrganisationType(BUSINESS)
                        .build())
                .build();

        User user = newUser().withId(1l).build();
        setLoggedInUser(newUserResource().withId(user.getId()).build());

        PaymentMilestoneProcess paymentMilestoneProcess = new PaymentMilestoneProcess(user, partnerOrganisationInDB, PaymentMilestoneState.REVIEW);

        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);
        when(paymentMilestoneWorkflowHandler.getProcess(partnerOrganisationInDB)).thenReturn(paymentMilestoneProcess);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        ServiceResult<PaymentMilestoneResource> result = service.getPaymentMilestone(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());
    }

    private void setGetEligibilityMocking(EligibilityState eligibilityStateInDB, EligibilityRagStatus eligibilityRagStatusInDB,
                                          User eligibilityApprovalUser, LocalDate eligibilityApprovalDate, boolean isKtp, boolean asKB) {
        Competition competition = newCompetition().withFundingType(isKtp ? FundingType.KTP : FundingType.LOAN).build();
        Application application = newApplication().withCompetition(competition).build();
        Project project = newProject().withApplication(application).build();
        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation()
                .withProject(project)
                .withOrganisation(newOrganisation()
                        .withOrganisationType(asKB ? KNOWLEDGE_BASE : BUSINESS)
                        .build())
                .build();
        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        EligibilityProcess eligibilityProcess = new EligibilityProcess(eligibilityApprovalUser, partnerOrganisationInDB, eligibilityStateInDB);
        if (eligibilityApprovalDate != null) {
            eligibilityProcess.setLastModified(eligibilityApprovalDate.atStartOfDay(ZoneId.systemDefault()));
        }

        when(eligibilityWorkflowHandler.getProcess(partnerOrganisationInDB)).thenReturn(eligibilityProcess);

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        projectFinanceInDB.setEligibilityStatus(eligibilityRagStatusInDB);

        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(projectFinanceInDB));

    }

    private void assertGetEligibilityResults(EligibilityResource returnedEligibilityResource, EligibilityState expectedEligibility,
                                             EligibilityRagStatus expectedEligibilityRagStatus,
                                             String expectedEligibilityApprovalUserFirstName, String expectedEligibilityApprovalUserLastName,
                                             LocalDate expectedEligibilityApprovalDate) {

        assertEquals(expectedEligibility, returnedEligibilityResource.getEligibility());
        assertEquals(expectedEligibilityRagStatus, returnedEligibilityResource.getEligibilityRagStatus());

        assertEquals(expectedEligibilityApprovalUserFirstName, returnedEligibilityResource.getEligibilityApprovalUserFirstName());
        assertEquals(expectedEligibilityApprovalUserLastName, returnedEligibilityResource.getEligibilityApprovalUserLastName());
        if (expectedEligibilityApprovalDate != null) {
            assertEquals(expectedEligibilityApprovalDate, returnedEligibilityResource.getEligibilityApprovalDate());
        }

    }
}