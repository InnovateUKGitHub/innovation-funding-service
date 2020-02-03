package org.innovateuk.ifs.project.financecheck.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.financechecks.domain.EligibilityProcess;
import org.innovateuk.ifs.project.financechecks.domain.FinanceCheck;
import org.innovateuk.ifs.project.financechecks.domain.ViabilityProcess;
import org.innovateuk.ifs.project.financechecks.repository.FinanceCheckRepository;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckServiceImpl;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.queries.transactional.FinanceCheckQueriesService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.transactional.StatusService;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaimPercentage;
import static org.innovateuk.ifs.finance.builder.ExcludedCostCategoryBuilder.newExcludedCostCategory;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostBuilder.newOtherFunding;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostCategoryBuilder.newOtherFundingCostCategory;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.resource.category.LabourCostCategory.WORKING_DAYS_PER_YEAR;
import static org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.financecheck.builder.CostBuilder.newCost;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.financecheck.builder.CostGroupBuilder.newCostGroup;
import static org.innovateuk.ifs.project.financecheck.builder.FinanceCheckBuilder.newFinanceCheck;
import static org.innovateuk.ifs.project.spendprofile.builder.SpendProfileBuilder.newSpendProfile;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FinanceCheckServiceImplTest extends BaseServiceUnitTest<FinanceCheckServiceImpl> {
    private Long applicationId = 123L;
    private Long organisationId = 234L;
    private Long competitionId = 456L;
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
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Mock
    private ProjectFinanceRepository projectFinanceRepository;

    @Mock
    private FinanceCheckQueriesService financeCheckQueriesService;

    @Mock
    private ApplicationFinanceService financeService;

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private FormInputRepository formInputRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private FormInputResponseRepository formInputResponseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationFinanceRepository applicationFinanceRepository;

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
        Competition competition = newCompetition().withMaxResearchRatio(2).build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).build();

        Organisation[] organisations = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS, OrganisationTypeEnum.RESEARCH, OrganisationTypeEnum.BUSINESS).
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

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
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

        ProjectFinance projectFinanceInDB1 = new ProjectFinance();
        projectFinanceInDB1.setViabilityStatus(ViabilityRagStatus.AMBER);
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(0).getOrganisation().getId())).thenReturn(projectFinanceInDB1);
        ProjectFinance projectFinanceInDB2 = new ProjectFinance();
        projectFinanceInDB2.setViabilityStatus(ViabilityRagStatus.UNSET);
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(1).getOrganisation().getId())).thenReturn(projectFinanceInDB2);
        ProjectFinance projectFinanceInDB3 = new ProjectFinance();
        projectFinanceInDB3.setViabilityStatus(ViabilityRagStatus.UNSET);
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(2).getOrganisation().getId())).thenReturn(projectFinanceInDB3);

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
        assertFalse(organisation1Results.isAwaitingResponse());

        FinanceCheckPartnerStatusResource organisation2Results = partnerStatuses.get(1);
        assertEquals(ViabilityState.APPROVED, organisation2Results.getViability());
        assertEquals(ViabilityRagStatus.AMBER, organisation2Results.getViabilityRagStatus());
        assertTrue(organisation2Results.isAwaitingResponse());

        FinanceCheckPartnerStatusResource organisation3Results = partnerStatuses.get(2);
        assertEquals(ViabilityState.REVIEW, organisation3Results.getViability());
        assertEquals(ViabilityRagStatus.UNSET, organisation3Results.getViabilityRagStatus());
        assertFalse(organisation3Results.isAwaitingResponse());
    }

    private <T> boolean organisationsOrderedWithLeadOnTopAndPartnersAlphabetically(List<PartnerOrganisation> beforeOrdered, List<String> organisationsNames) {
        PartnerOrganisation leadPartner = simpleFindFirst(beforeOrdered, PartnerOrganisation::isLeadOrganisation).get();
        List<PartnerOrganisation> orderedPartnerOrganisations = new PrioritySorting<>(beforeOrdered, leadPartner, po -> po.getOrganisation().getName()).unwrap();
        return organisationsNames.equals(simpleMap(orderedPartnerOrganisations, po -> po.getOrganisation().getName()));
    }

    @Test
    public void getFinanceCheckEligibility(){
        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId).withName("Organisation1").build();

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

        assertEquals((long) eligibility.getDurationInMonths(), 5L);
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
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();

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
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
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
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
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
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
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
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
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
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
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
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
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
                withOrganisationType(OrganisationTypeEnum.BUSINESS, OrganisationTypeEnum.RESEARCH, OrganisationTypeEnum.BUSINESS).
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
        when(projectFinanceService.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceSuccess(3.0));

        ServiceResult<FinanceCheckOverviewResource> result = service.getFinanceCheckOverview(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckOverviewResource overview = result.getSuccess();
        assertEquals(projectId, overview.getProjectId());
        assertEquals(6, overview.getDurationInMonths());
        assertEquals(new BigDecimal("10000067"), overview.getTotalProjectCost());
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
                withOrganisationType(OrganisationTypeEnum.BUSINESS, OrganisationTypeEnum.RESEARCH, OrganisationTypeEnum.BUSINESS).
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
        when(projectFinanceService.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceFailure(GENERAL_FORBIDDEN));

        ServiceResult<FinanceCheckOverviewResource> result = service.getFinanceCheckOverview(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckOverviewResource overview = result.getSuccess();
        assertEquals(projectId, overview.getProjectId());
        assertEquals(6, overview.getDurationInMonths());
        assertEquals(new BigDecimal("10000067"), overview.getTotalProjectCost());
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
                withOrganisationType(OrganisationTypeEnum.BUSINESS, OrganisationTypeEnum.RESEARCH, OrganisationTypeEnum.BUSINESS).
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
        when(projectFinanceService.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceSuccess(null));

        ServiceResult<FinanceCheckOverviewResource> result = service.getFinanceCheckOverview(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckOverviewResource overview = result.getSuccess();
        assertEquals(projectId, overview.getProjectId());
        assertEquals(6, overview.getDurationInMonths());
        assertEquals(new BigDecimal("10000067"), overview.getTotalProjectCost());
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

    private void setupFinancialAndNonFinancialTestData(boolean isIncludeGrowthTable, boolean noResponse, boolean noInput) {
        Long turnoverFormInputId = 678L;
        Long staffCountFormInputId = 987L;
        Competition comp = new Competition();
        comp.setId(competitionId);
        Application app = new Application();
        app.setId(applicationId);
        app.setCompetition(comp);
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(app));

        ProcessRole updatedBy = newProcessRole().withApplication(app).withOrganisationId(organisationId).build();
        FormInputResponse headcount = newFormInputResponse().withValue("1").withUpdatedBy(updatedBy).build();
        FormInputResponse turnover = newFormInputResponse().withValue("2").withUpdatedBy(updatedBy).build();

        FormInput staffCountFormInput = newFormInput().withType(STAFF_COUNT).withActive(!isIncludeGrowthTable).withId(staffCountFormInputId).build();
        FormInput organisationTurnoverFormInput = newFormInput().withType(ORGANISATION_TURNOVER).withActive(!isIncludeGrowthTable).withId(turnoverFormInputId).build();
        when(formInputRepository.findByCompetitionIdAndTypeIn(competitionId, asList(ORGANISATION_TURNOVER))).thenReturn(noInput ? emptyList() : asList(organisationTurnoverFormInput));
        when(formInputRepository.findByCompetitionIdAndTypeIn(competitionId, asList(STAFF_COUNT))).thenReturn(noInput ? emptyList() : asList(staffCountFormInput));
        when(formInputResponseRepository.findByApplicationIdAndFormInputId(applicationId, turnoverFormInputId)).thenReturn(noResponse ? emptyList() : asList(turnover));
        when(formInputResponseRepository.findByApplicationIdAndFormInputId(applicationId, staffCountFormInputId)).thenReturn(noResponse ? emptyList() : asList(headcount));

        FormInput financialYearEnd = newFormInput().withType(FINANCIAL_YEAR_END).withActive(isIncludeGrowthTable).withId(turnoverFormInputId).build();
        List<FormInput> financialOverviewRows = newFormInput().withType(FINANCIAL_OVERVIEW_ROW).withActive(isIncludeGrowthTable).build(4);
        FormInput financialCount = newFormInput().withType(FormInputType.FINANCIAL_STAFF_COUNT).withActive(isIncludeGrowthTable).withId(staffCountFormInputId).build();
        when(formInputRepository.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_YEAR_END))).thenReturn(noInput ? emptyList() : asList(financialYearEnd));
        when(formInputRepository.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_OVERVIEW_ROW))).thenReturn(financialOverviewRows);
        when(formInputRepository.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_STAFF_COUNT))).thenReturn(noInput ? emptyList() : asList(financialCount));
        when(formInputResponseRepository.findByApplicationIdAndFormInputId(applicationId, turnoverFormInputId)).thenReturn(noResponse ? emptyList() : asList(turnover));
        when(formInputResponseRepository.findByApplicationIdAndFormInputId(applicationId, staffCountFormInputId)).thenReturn(noResponse ? emptyList() : asList(headcount));
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
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

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
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

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
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);
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
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

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
    public void getViabilityWhenViabilityStateIsReviewInDB() {

        setUpGetViabilityMocking(ViabilityState.REVIEW, ViabilityRagStatus.RED, null, null);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<ViabilityResource> result = service.getViability(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        ViabilityResource returnedViabilityResource = result.getSuccess();

        assertGetViabilityResults(returnedViabilityResource, ViabilityState.REVIEW, ViabilityRagStatus.RED,
                null, null, null);
    }

    @Test
    public void getViabilityWhenViabilityStateIsNotApplicableInDB() {

        setUpGetViabilityMocking(ViabilityState.NOT_APPLICABLE, ViabilityRagStatus.AMBER, null, null);

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

        setUpGetViabilityMocking(ViabilityState.APPROVED, ViabilityRagStatus.GREEN, user, LocalDate.now());

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<ViabilityResource> result = service.getViability(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        ViabilityResource returnedViabilityResource = result.getSuccess();

        assertGetViabilityResults(returnedViabilityResource, ViabilityState.APPROVED, ViabilityRagStatus.GREEN,
                "Lee", "Bowman", LocalDate.now());
    }

    private void setUpGetViabilityMocking(ViabilityState viabilityStateInDB, ViabilityRagStatus viabilityRagStatusInDB,
                                          User viabilityApprovalUser, LocalDate viabilityApprovalDate) {

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ViabilityProcess viabilityProcess = new ViabilityProcess(viabilityApprovalUser, partnerOrganisationInDB, viabilityStateInDB);
        if (viabilityApprovalDate != null) {
            viabilityProcess.setLastModified(viabilityApprovalDate.atStartOfDay(ZoneId.systemDefault()));
        }

        when(viabilityWorkflowHandler.getProcess(partnerOrganisationInDB)).thenReturn(viabilityProcess);

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        projectFinanceInDB.setViabilityStatus(viabilityRagStatusInDB);
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

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

        setGetEligibilityMocking(EligibilityState.REVIEW, EligibilityRagStatus.RED, null, null);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<EligibilityResource> result = service.getEligibility(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        EligibilityResource returnedEligibilityResource = result.getSuccess();

        assertGetEligibilityResults(returnedEligibilityResource, EligibilityState.REVIEW, EligibilityRagStatus.RED,
                null, null, null);

    }

    @Test
    public void getEligibilityWhenEligibilityIsNotApplicableInDB() {

        setGetEligibilityMocking(EligibilityState.NOT_APPLICABLE, EligibilityRagStatus.AMBER, null, null);

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

        setGetEligibilityMocking(EligibilityState.APPROVED, EligibilityRagStatus.GREEN, user, LocalDate.now());

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<EligibilityResource> result = service.getEligibility(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        EligibilityResource returnedEligibilityResource = result.getSuccess();

        assertGetEligibilityResults(returnedEligibilityResource, EligibilityState.APPROVED, EligibilityRagStatus.GREEN,
                "Lee", "Bowman", LocalDate.now());

    }

    private void setGetEligibilityMocking(EligibilityState eligibilityStateInDB, EligibilityRagStatus eligibilityRagStatusInDB,
                                          User eligibilityApprovalUser, LocalDate eligibilityApprovalDate) {

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        EligibilityProcess eligibilityProcess = new EligibilityProcess(eligibilityApprovalUser, partnerOrganisationInDB, eligibilityStateInDB);
        if (eligibilityApprovalDate != null) {
            eligibilityProcess.setLastModified(eligibilityApprovalDate.atStartOfDay(ZoneId.systemDefault()));
        }

        when(eligibilityWorkflowHandler.getProcess(partnerOrganisationInDB)).thenReturn(eligibilityProcess);

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        projectFinanceInDB.setEligibilityStatus(eligibilityRagStatusInDB);

        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

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