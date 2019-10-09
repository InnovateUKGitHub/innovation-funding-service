package org.innovateuk.ifs.project.financecheck.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.commons.competitionsetup.CompetitionSetupTransactionalService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
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
import static org.innovateuk.ifs.finance.builder.GrantClaimCostCategoryBuilder.newGrantClaimCostCategory;
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
    private FinanceCheckRepository financeCheckRepositoryMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private PartnerOrganisationRepository partnerOrganisationRepositoryMock;

    @Mock
    private SpendProfileRepository spendProfileRepositoryMock;

    @Mock
    private ProjectFinanceRowService projectFinanceRowServiceMock;

    @Mock
    private StatusService statusServiceMock;

    @Mock
    private EligibilityWorkflowHandler eligibilityWorkflowHandlerMock;

    @Mock
    private ViabilityWorkflowHandler viabilityWorkflowHandlerMock;

    @Mock
    private ProjectFinanceRepository projectFinanceRepositoryMock;

    @Mock
    private FinanceCheckQueriesService financeCheckQueriesServiceMock;

    @Mock
    private ApplicationFinanceService financeServiceMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private CompetitionSetupTransactionalService competitionSetupTransactionalServiceMock;

    @Mock
    private FormInputRepository formInputRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private FormInputResponseRepository formInputResponseRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Test
    public void testGetByProjectAndOrganisationNotFound() {
        // Set up
        ProjectOrganisationCompositeId compositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        when(financeCheckRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(null);
        // Method under test
        ServiceResult<FinanceCheckResource> result = service.getByProjectAndOrganisation(compositeId);
        // Assertions
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FinanceCheck.class, compositeId)));
    }

    @Test
    public void testGetByProjectAndOrganisation() {
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
        when(financeCheckRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(financeCheck);
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
    public void testGetFinanceCheckSummary(){
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
        List<ProjectFinanceResource> projectFinanceResourceList = newProjectFinanceResource().withGrantClaimPercentage(20).build(3);
        ProjectTeamStatusResource projectTeamStatus = newProjectTeamStatusResource().build();

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(partnerOrganisationRepositoryMock.findByProjectId(projectId)).thenReturn(partnerOrganisations);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(0).getOrganisation().getId())).thenReturn(spendProfile);
        when(projectFinanceRowServiceMock.financeChecksTotals(project.getId())).thenReturn(serviceSuccess(projectFinanceResourceList));
        when(statusServiceMock.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(serviceSuccess(projectTeamStatus));

        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisations[0].getId())).thenReturn(partnerOrganisations.get(0));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisations[1].getId())).thenReturn(partnerOrganisations.get(1));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisations[2].getId())).thenReturn(partnerOrganisations.get(2));

        when(eligibilityWorkflowHandlerMock.getProcess(partnerOrganisations.get(0))).thenReturn(new EligibilityProcess(projectFinanceUser, partnerOrganisations.get(0), EligibilityState.APPROVED));
        when(eligibilityWorkflowHandlerMock.getProcess(partnerOrganisations.get(1))).thenReturn(new EligibilityProcess(projectFinanceUser, partnerOrganisations.get(1), EligibilityState.REVIEW));
        when(eligibilityWorkflowHandlerMock.getProcess(partnerOrganisations.get(2))).thenReturn(new EligibilityProcess(projectFinanceUser, partnerOrganisations.get(2), EligibilityState.REVIEW));

        when(viabilityWorkflowHandlerMock.getProcess(partnerOrganisations.get(0))).thenReturn(new ViabilityProcess(projectFinanceUser, partnerOrganisations.get(0), ViabilityState.APPROVED));
        when(viabilityWorkflowHandlerMock.getProcess(partnerOrganisations.get(1))).thenReturn(new ViabilityProcess(projectFinanceUser, partnerOrganisations.get(1), ViabilityState.NOT_APPLICABLE));
        when(viabilityWorkflowHandlerMock.getProcess(partnerOrganisations.get(2))).thenReturn(new ViabilityProcess(projectFinanceUser, partnerOrganisations.get(2), ViabilityState.REVIEW));

        ProjectFinance projectFinanceInDB1 = new ProjectFinance();
        projectFinanceInDB1.setViabilityStatus(ViabilityRagStatus.AMBER);
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(0).getOrganisation().getId())).thenReturn(projectFinanceInDB1);
        ProjectFinance projectFinanceInDB2 = new ProjectFinance();
        projectFinanceInDB2.setViabilityStatus(ViabilityRagStatus.UNSET);
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(1).getOrganisation().getId())).thenReturn(projectFinanceInDB2);
        ProjectFinance projectFinanceInDB3 = new ProjectFinance();
        projectFinanceInDB3.setViabilityStatus(ViabilityRagStatus.UNSET);
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(2).getOrganisation().getId())).thenReturn(projectFinanceInDB3);

        ProjectFinanceResource[] projectFinanceResources = newProjectFinanceResource().withId(234L, 345L, 456L).withOrganisation(organisations[0].getId(), organisations[1].getId(), organisations[2].getId()).buildArray(3, ProjectFinanceResource.class);
        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisations[0].getId())).thenReturn(ServiceResult.serviceSuccess(projectFinanceResources[0]));
        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisations[1].getId())).thenReturn(ServiceResult.serviceSuccess(projectFinanceResources[1]));
        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisations[2].getId())).thenReturn(ServiceResult.serviceSuccess(projectFinanceResources[2]));

        QueryResource queryResource1 = new QueryResource(12L, 23L, new ArrayList<>(), FinanceChecksSectionType.ELIGIBILITY, "Title" , true, ZonedDateTime.now(), null, null);
        QueryResource queryResource2 = new QueryResource(12L, 23L, new ArrayList<>(), FinanceChecksSectionType.ELIGIBILITY, "Title" , false, ZonedDateTime.now(), null, null);
        when(financeCheckQueriesServiceMock.findAll(234L)).thenReturn(serviceSuccess(Arrays.asList(queryResource1)));
        when(financeCheckQueriesServiceMock.findAll(345L)).thenReturn(serviceSuccess(new ArrayList<>()));
        when(financeCheckQueriesServiceMock.findAll(456L)).thenReturn(serviceSuccess(Arrays.asList(queryResource2)));
        when(financeServiceMock.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceSuccess(Double.valueOf(3.0)));
        ServiceResult<FinanceCheckSummaryResource> result = service.getFinanceCheckSummary(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckSummaryResource summary = result.getSuccess();
        List<FinanceCheckPartnerStatusResource> partnerStatuses = summary.getPartnerStatusResources();
        assertEquals(3, partnerStatuses.size());

        assertTrue(organisationsOrderedWithLeadOnTopAndPartnersAlphabetically(partnerOrganisations,
                simpleMap(summary.getPartnerStatusResources(), FinanceCheckPartnerStatusResource::getName)));

        FinanceCheckPartnerStatusResource organisation1Results = partnerStatuses.get(0);
        assertEquals(Viability.NOT_APPLICABLE, organisation1Results.getViability());
        assertEquals(ViabilityRagStatus.UNSET, organisation1Results.getViabilityRagStatus());
        assertFalse(organisation1Results.isAwaitingResponse());

        FinanceCheckPartnerStatusResource organisation2Results = partnerStatuses.get(1);
        assertEquals(Viability.APPROVED, organisation2Results.getViability());
        assertEquals(ViabilityRagStatus.AMBER, organisation2Results.getViabilityRagStatus());
        assertTrue(organisation2Results.isAwaitingResponse());

        FinanceCheckPartnerStatusResource organisation3Results = partnerStatuses.get(2);
        assertEquals(Viability.REVIEW, organisation3Results.getViability());
        assertEquals(ViabilityRagStatus.UNSET, organisation3Results.getViabilityRagStatus());
        assertFalse(organisation3Results.isAwaitingResponse());
    }

    private <T> boolean organisationsOrderedWithLeadOnTopAndPartnersAlphabetically(List<PartnerOrganisation> beforeOrdered, List<String> organisationsNames) {
        PartnerOrganisation leadPartner = simpleFindFirst(beforeOrdered, PartnerOrganisation::isLeadOrganisation).get();
        List<PartnerOrganisation> orderedPartnerOrganisations = new PrioritySorting<>(beforeOrdered, leadPartner, po -> po.getOrganisation().getName()).unwrap();
        return organisationsNames.equals(simpleMap(orderedPartnerOrganisations, po -> po.getOrganisation().getName()));
    }

    @Test
    public void testGetFinanceCheckEligibility(){
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


        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(organisationRepositoryMock.findById(organisationId)).thenReturn(Optional.of(organisation));
        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceSuccess(projectFinanceResource));

        ServiceResult<FinanceCheckEligibilityResource> result = service.getFinanceCheckEligibilityDetails(projectId, organisationId);
        assertTrue(result.isSuccess());

        FinanceCheckEligibilityResource eligibility = result.getSuccess();

        assertEquals((long) eligibility.getDurationInMonths(), 5L);
        assertEquals(projectFinanceResource.getTotal(), eligibility.getTotalCost());
        assertEquals(BigDecimal.valueOf(projectFinanceResource.getGrantClaimPercentage()), eligibility.getPercentageGrant());
        assertEquals(projectFinanceResource.getTotalFundingSought(), eligibility.getFundingSought());
        assertEquals(projectFinanceResource.getTotalOtherFunding(), eligibility.getOtherPublicSectorFunding());
        assertEquals(projectFinanceResource.getTotalContribution(), eligibility.getContributionToProject());
    }

    @Test
    public void testGetFinanceCheckEligibilityNoProjectFinances(){
        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(organisationRepositoryMock.findById(organisationId)).thenReturn(Optional.of(organisation));
        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(GENERAL_NOT_FOUND));

        ServiceResult<FinanceCheckEligibilityResource> result = service.getFinanceCheckEligibilityDetails(projectId, organisationId);
        assertTrue(result.isFailure());

    }

    @Test
    public void testQueryActionRequired() {

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();
        QueryResource fakeQuery = new QueryResource(1L, 1L, Collections.emptyList(), FinanceChecksSectionType.ELIGIBILITY, "", true, ZonedDateTime.now(), null, null);
        List<QueryResource> queries = Collections.singletonList(fakeQuery);

        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceSuccess(resource));
        when(financeCheckQueriesServiceMock.findAll(resource.getId())).thenReturn(serviceSuccess(queries));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertTrue(result.getSuccess());
    }

    @Test
    public void testQueryNoActionRequired() {

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();
        QueryResource fakeQuery = new QueryResource(1L, 1L, Collections.emptyList(), FinanceChecksSectionType.ELIGIBILITY, "", false, ZonedDateTime.now(), null, null);
        List<QueryResource> queries = Collections.singletonList(fakeQuery);

        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceSuccess(resource));
        when(financeCheckQueriesServiceMock.findAll(resource.getId())).thenReturn(serviceSuccess(queries));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccess());
    }

    @Test
    public void testQueryWithNoProjectFinance() {

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();
        QueryResource fakeQuery = new QueryResource(1L, 1L, Collections.emptyList(), FinanceChecksSectionType.ELIGIBILITY, "", false, ZonedDateTime.now(), null, null);
        List<QueryResource> queries = Collections.singletonList(fakeQuery);

        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(internalServerErrorError()));
        when(financeCheckQueriesServiceMock.findAll(resource.getId())).thenReturn(serviceSuccess(queries));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccess());
    }

    @Test
    public void testQueryWithNoQueries() {
        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();
        List<QueryResource> queries = Collections.emptyList();

        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(internalServerErrorError()));
        when(financeCheckQueriesServiceMock.findAll(resource.getId())).thenReturn(serviceSuccess(queries));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccess());
    }

    @Test
    public void testQueryWithQueriesFailure() {

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();

        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(internalServerErrorError()));
        when(financeCheckQueriesServiceMock.findAll(resource.getId())).thenReturn(serviceFailure(internalServerErrorError()));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccess());
    }

    @Test
    public void testQueryWithNullQueries() {

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();

        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(internalServerErrorError()));
        when(financeCheckQueriesServiceMock.findAll(resource.getId())).thenReturn(serviceSuccess(null));

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


        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(projectFinanceRowServiceMock.financeChecksTotals(projectId)).thenReturn(serviceSuccess(projectFinanceResource));
        when(financeServiceMock.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceSuccess(Double.valueOf(3.0)));

        ServiceResult<FinanceCheckOverviewResource> result = service.getFinanceCheckOverview(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckOverviewResource overview = result.getSuccess();
        assertEquals(projectId, overview.getProjectId());
        assertEquals(6, overview.getDurationInMonths());
        assertEquals(new BigDecimal("10000067"), overview.getTotalProjectCost());
        assertEquals(new BigDecimal("2998020"), overview.getGrantAppliedFor());
        assertEquals(new BigDecimal("2000"), overview.getOtherPublicSectorFunding());
        assertEquals(new BigDecimal("30"), overview.getTotalPercentageGrant());
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


        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(projectFinanceRowServiceMock.financeChecksTotals(projectId)).thenReturn(serviceSuccess(projectFinanceResource));
        when(financeServiceMock.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceFailure(GENERAL_FORBIDDEN));

        ServiceResult<FinanceCheckOverviewResource> result = service.getFinanceCheckOverview(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckOverviewResource overview = result.getSuccess();
        assertEquals(projectId, overview.getProjectId());
        assertEquals(6, overview.getDurationInMonths());
        assertEquals(new BigDecimal("10000067"), overview.getTotalProjectCost());
        assertEquals(new BigDecimal("2998020"), overview.getGrantAppliedFor());
        assertEquals(new BigDecimal("2000"), overview.getOtherPublicSectorFunding());
        assertEquals(new BigDecimal("30"), overview.getTotalPercentageGrant());
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


        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(projectFinanceRowServiceMock.financeChecksTotals(projectId)).thenReturn(serviceSuccess(projectFinanceResource));
        when(financeServiceMock.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceSuccess(null));

        ServiceResult<FinanceCheckOverviewResource> result = service.getFinanceCheckOverview(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckOverviewResource overview = result.getSuccess();
        assertEquals(projectId, overview.getProjectId());
        assertEquals(6, overview.getDurationInMonths());
        assertEquals(new BigDecimal("10000067"), overview.getTotalProjectCost());
        assertEquals(new BigDecimal("2998020"), overview.getGrantAppliedFor());
        assertEquals(new BigDecimal("2000"), overview.getOtherPublicSectorFunding());
        assertEquals(new BigDecimal("30"), overview.getTotalPercentageGrant());
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
                FinanceRowType.FINANCE, newGrantClaimCostCategory().withCosts(
                        newGrantClaimPercentage().
                                withGrantClaimPercentage(30).
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
        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(app));

        ProcessRole updatedBy = newProcessRole().withApplication(app).withOrganisationId(organisationId).build();
        FormInputResponse headcount = newFormInputResponse().withValue("1").withUpdatedBy(updatedBy).build();
        FormInputResponse turnover = newFormInputResponse().withValue("2").withUpdatedBy(updatedBy).build();

        FormInput staffCountFormInput = newFormInput().withType(STAFF_COUNT).withActive(!isIncludeGrowthTable).withId(staffCountFormInputId).build();
        FormInput organisationTurnoverFormInput = newFormInput().withType(ORGANISATION_TURNOVER).withActive(!isIncludeGrowthTable).withId(turnoverFormInputId).build();
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(ORGANISATION_TURNOVER))).thenReturn(noInput ? emptyList() : asList(organisationTurnoverFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(STAFF_COUNT))).thenReturn(noInput ? emptyList() : asList(staffCountFormInput));
        when(formInputResponseRepositoryMock.findByApplicationIdAndFormInputId(applicationId, turnoverFormInputId)).thenReturn(noResponse ? emptyList() : asList(turnover));
        when(formInputResponseRepositoryMock.findByApplicationIdAndFormInputId(applicationId, staffCountFormInputId)).thenReturn(noResponse ? emptyList() : asList(headcount));

        FormInput financialYearEnd = newFormInput().withType(FINANCIAL_YEAR_END).withActive(isIncludeGrowthTable).withId(turnoverFormInputId).build();
        List<FormInput> financialOverviewRows = newFormInput().withType(FINANCIAL_OVERVIEW_ROW).withActive(isIncludeGrowthTable).build(4);
        FormInput financialCount = newFormInput().withType(FormInputType.FINANCIAL_STAFF_COUNT).withActive(isIncludeGrowthTable).withId(staffCountFormInputId).build();
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_YEAR_END))).thenReturn(noInput ? emptyList() : asList(financialYearEnd));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_OVERVIEW_ROW))).thenReturn(financialOverviewRows);
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_STAFF_COUNT))).thenReturn(noInput ? emptyList() : asList(financialCount));
        when(formInputResponseRepositoryMock.findByApplicationIdAndFormInputId(applicationId, turnoverFormInputId)).thenReturn(noResponse ? emptyList() : asList(turnover));
        when(formInputResponseRepositoryMock.findByApplicationIdAndFormInputId(applicationId, staffCountFormInputId)).thenReturn(noResponse ? emptyList() : asList(headcount));
    }

    @Test
    public void testSaveViabilityWhenViabilityAlreadyApproved() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveViabilityMocking(user, partnerOrganisationInDB, ViabilityState.APPROVED);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, Viability.APPROVED, ViabilityRagStatus.AMBER);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(VIABILITY_HAS_ALREADY_BEEN_APPROVED));

        verify(projectFinanceRepositoryMock, never()).save(projectFinanceInDB);
        verify(viabilityWorkflowHandlerMock, never()).viabilityApproved(partnerOrganisationInDB, user);

    }

    @Test
    public void testSaveViabilityWhenViabilityRagStatusIsUnset() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveViabilityMocking(user, partnerOrganisationInDB, ViabilityState.REVIEW);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, Viability.APPROVED, ViabilityRagStatus.UNSET);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(VIABILITY_RAG_STATUS_MUST_BE_SET));

        verify(projectFinanceRepositoryMock, never()).save(projectFinanceInDB);
        verify(viabilityWorkflowHandlerMock, never()).viabilityApproved(partnerOrganisationInDB, user);

    }

    @Test
    public void testSaveViabilityWhenViabilityRagStatusIsUnsetButViabilityAlsoNotApproved() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveViabilityMocking(user, partnerOrganisationInDB, ViabilityState.REVIEW);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, Viability.REVIEW, ViabilityRagStatus.UNSET);

        assertTrue(result.isSuccess());

        assertSaveViabilityResults(projectFinanceInDB, ViabilityRagStatus.UNSET);

        verify(viabilityWorkflowHandlerMock, never()).viabilityApproved(partnerOrganisationInDB, user);
    }

    @Test
    public void testSaveViabilityWhenViabilityRagStatusIsSetButViabilityNotApproved() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveViabilityMocking(user, partnerOrganisationInDB, ViabilityState.REVIEW);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, Viability.REVIEW, ViabilityRagStatus.AMBER);

        assertTrue(result.isSuccess());

        assertSaveViabilityResults(projectFinanceInDB, ViabilityRagStatus.AMBER);

        verify(viabilityWorkflowHandlerMock, never()).viabilityApproved(partnerOrganisationInDB, user);
    }

    @Test
    public void testSaveViabilityWhenViabilityApproved() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveViabilityMocking(user, partnerOrganisationInDB, ViabilityState.REVIEW);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, Viability.APPROVED, ViabilityRagStatus.AMBER);

        assertTrue(result.isSuccess());

        assertSaveViabilityResults(projectFinanceInDB, ViabilityRagStatus.AMBER);

        // Ensure the workflow is called with the correct target and participant
        verify(viabilityWorkflowHandlerMock).viabilityApproved(partnerOrganisationInDB, user);

    }

    private ProjectFinance setUpSaveViabilityMocking(User user, PartnerOrganisation partnerOrganisationInDB, ViabilityState viabilityStateInDB) {

        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ViabilityProcess viabilityProcess = new ViabilityProcess(user, partnerOrganisationInDB, viabilityStateInDB);
        when(viabilityWorkflowHandlerMock.getProcess(partnerOrganisationInDB)).thenReturn(viabilityProcess);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        return projectFinanceInDB;

    }

    private void assertSaveViabilityResults(ProjectFinance projectFinanceInDB, ViabilityRagStatus expectedViabilityRagStatus) {

        assertEquals(expectedViabilityRagStatus, projectFinanceInDB.getViabilityStatus());

        verify(projectFinanceRepositoryMock).save(projectFinanceInDB);
    }

    @Test
    public void testSaveEligibilityWhenEligibilityAlreadyApproved() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();
        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveEligibilityMocking(partnerOrganisationInDB, user, EligibilityState.APPROVED);

        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, EligibilityState.APPROVED, EligibilityRagStatus.AMBER);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(ELIGIBILITY_HAS_ALREADY_BEEN_APPROVED));

        verify(projectFinanceRepositoryMock, never()).save(projectFinanceInDB);
        verify(eligibilityWorkflowHandlerMock, never()).eligibilityApproved(partnerOrganisationInDB, user);
    }

    @Test
    public void testSaveEligibilityWhenEligibilityApprovedButStatusIsUnset() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();
        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveEligibilityMocking(partnerOrganisationInDB, user, EligibilityState.REVIEW);

        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, EligibilityState.APPROVED, EligibilityRagStatus.UNSET);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(ELIGIBILITY_RAG_STATUS_MUST_BE_SET));

        verify(projectFinanceRepositoryMock, never()).save(projectFinanceInDB);
        verify(eligibilityWorkflowHandlerMock, never()).eligibilityApproved(partnerOrganisationInDB, user);

    }

    @Test
    public void testSaveEligibilityWhenEligibilityNotApprovedAndStatusIsUnset() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveEligibilityMocking(partnerOrganisationInDB, user, EligibilityState.REVIEW);

        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, EligibilityState.REVIEW, EligibilityRagStatus.UNSET);

        assertTrue(result.isSuccess());

        assertSaveEligibilityResults(projectFinanceInDB, EligibilityRagStatus.UNSET);

        verify(eligibilityWorkflowHandlerMock, never()).eligibilityApproved(partnerOrganisationInDB, user);
    }

    @Test
    public void testSaveEligibilityWhenEligibilityNotApprovedAndStatusIsSet() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveEligibilityMocking(partnerOrganisationInDB, user, EligibilityState.REVIEW);

        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, EligibilityState.REVIEW, EligibilityRagStatus.AMBER);

        assertTrue(result.isSuccess());

        assertSaveEligibilityResults(projectFinanceInDB, EligibilityRagStatus.AMBER);

        verify(eligibilityWorkflowHandlerMock, never()).eligibilityApproved(partnerOrganisationInDB, user);

    }

    @Test
    public void testSaveEligibilityWhenEligibilityApprovedAndStatusIsSet() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        ProjectFinance projectFinanceInDB = setUpSaveEligibilityMocking(partnerOrganisationInDB, user, EligibilityState.REVIEW);

        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, EligibilityState.APPROVED, EligibilityRagStatus.GREEN);

        assertTrue(result.isSuccess());

        assertSaveEligibilityResults(projectFinanceInDB, EligibilityRagStatus.GREEN);

        // Ensure the workflow is called with the correct target and participant
        verify(eligibilityWorkflowHandlerMock).eligibilityApproved(partnerOrganisationInDB, user);
    }

    private ProjectFinance setUpSaveEligibilityMocking(PartnerOrganisation partnerOrganisationInDB, User user, EligibilityState eligibilityStateInDB) {

        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        EligibilityProcess eligibilityProcess = new EligibilityProcess(user, partnerOrganisationInDB, eligibilityStateInDB);
        when(eligibilityWorkflowHandlerMock.getProcess(partnerOrganisationInDB)).thenReturn(eligibilityProcess);

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        return projectFinanceInDB;
    }

    private void assertSaveEligibilityResults(ProjectFinance projectFinanceInDB, EligibilityRagStatus expectedEligibilityRagStatus) {

        assertEquals(expectedEligibilityRagStatus, projectFinanceInDB.getEligibilityStatus());

        verify(projectFinanceRepositoryMock).save(projectFinanceInDB);
    }

    @Test
    public void testGetCreditReportSuccess() {

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        projectFinanceInDB.setCreditReportConfirmed(true);
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);
        ServiceResult<Boolean> result = service.getCreditReport(projectId, organisationId);

        assertTrue(result.isSuccess());
        assertEquals(true, result.getSuccess());

        verify(projectFinanceRepositoryMock).findByProjectIdAndOrganisationId(projectId, organisationId);
    }

    @Test
    public void testSaveCreditSuccess() {

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ViabilityProcess viabilityProcess = new ViabilityProcess((User) null, partnerOrganisationInDB, ViabilityState.REVIEW);
        when(viabilityWorkflowHandlerMock.getProcess(partnerOrganisationInDB)).thenReturn(viabilityProcess);

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        ServiceResult<Void> result = service.saveCreditReport(projectId, organisationId, true);

        assertTrue(result.isSuccess());

        assertEquals(true, projectFinanceInDB.getCreditReportConfirmed());
        verify(projectFinanceRepositoryMock).save(projectFinanceInDB);

    }

    @Test
    public void testSaveCreditFailsBecauseViabilityIsAlreadyApproved() {

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ViabilityProcess viabilityProcess = new ViabilityProcess((User) null, partnerOrganisationInDB, ViabilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getProcess(partnerOrganisationInDB)).thenReturn(viabilityProcess);

        ServiceResult<Void> result = service.saveCreditReport(projectId, organisationId, true);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(VIABILITY_HAS_ALREADY_BEEN_APPROVED));
    }

    @Test
    public void testGetViabilityWhenPartnerOrganisationDoesNotExist() {

        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(null);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        ServiceResult<ViabilityResource> result = service.getViability(projectOrganisationCompositeId);

        assertTrue(result.isFailure());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), result.getErrors().get(0).getErrorKey());

    }

    @Test
    public void testGetViabilityWhenViabilityStateIsReviewInDB() {

        setUpGetViabilityMocking(ViabilityState.REVIEW, ViabilityRagStatus.RED, null, null);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<ViabilityResource> result = service.getViability(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        ViabilityResource returnedViabilityResource = result.getSuccess();

        assertGetViabilityResults(returnedViabilityResource, Viability.REVIEW, ViabilityRagStatus.RED,
                null, null, null);
    }

    @Test
    public void testGetViabilityWhenViabilityStateIsNotApplicableInDB() {

        setUpGetViabilityMocking(ViabilityState.NOT_APPLICABLE, ViabilityRagStatus.AMBER, null, null);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<ViabilityResource> result = service.getViability(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        ViabilityResource returnedViabilityResource = result.getSuccess();

        assertGetViabilityResults(returnedViabilityResource, Viability.NOT_APPLICABLE, ViabilityRagStatus.AMBER,
                null, null, null);
    }

    @Test
    public void testGetViabilityWhenViabilityStateIsApproved() {

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

        assertGetViabilityResults(returnedViabilityResource, Viability.APPROVED, ViabilityRagStatus.GREEN,
                "Lee", "Bowman", LocalDate.now());
    }

    private void setUpGetViabilityMocking(ViabilityState viabilityStateInDB, ViabilityRagStatus viabilityRagStatusInDB,
                                          User viabilityApprovalUser, LocalDate viabilityApprovalDate) {

        PartnerOrganisation partnerOrganisationInDB = PartnerOrganisationBuilder.newPartnerOrganisation().build();
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        ViabilityProcess viabilityProcess = new ViabilityProcess(viabilityApprovalUser, partnerOrganisationInDB, viabilityStateInDB);
        if (viabilityApprovalDate != null) {
            viabilityProcess.setLastModified(viabilityApprovalDate.atStartOfDay(ZoneId.systemDefault()));
        }

        when(viabilityWorkflowHandlerMock.getProcess(partnerOrganisationInDB)).thenReturn(viabilityProcess);

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        projectFinanceInDB.setViabilityStatus(viabilityRagStatusInDB);
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

    }

    private void assertGetViabilityResults(ViabilityResource returnedViabilityResource, Viability expectedViability, ViabilityRagStatus expectedViabilityRagStatus,
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
    public void testGetEligibilityWhenPartnerOrganisationDoesNotExist() {

        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(null);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        ServiceResult<EligibilityResource> result = service.getEligibility(projectOrganisationCompositeId);

        assertTrue(result.isFailure());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), result.getErrors().get(0).getErrorKey());

    }

    @Test
    public void testGetEligibilityWhenEligibilityIsReviewInDB() {

        setGetEligibilityMocking(EligibilityState.REVIEW, EligibilityRagStatus.RED, null, null);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<EligibilityResource> result = service.getEligibility(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        EligibilityResource returnedEligibilityResource = result.getSuccess();

        assertGetEligibilityResults(returnedEligibilityResource, EligibilityState.REVIEW, EligibilityRagStatus.RED,
                null, null, null);

    }

    @Test
    public void testGetEligibilityWhenEligibilityIsNotApplicableInDB() {

        setGetEligibilityMocking(EligibilityState.NOT_APPLICABLE, EligibilityRagStatus.AMBER, null, null);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<EligibilityResource> result = service.getEligibility(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        EligibilityResource returnedEligibilityResource = result.getSuccess();

        assertGetEligibilityResults(returnedEligibilityResource, EligibilityState.NOT_APPLICABLE, EligibilityRagStatus.AMBER,
                null, null, null);

    }

    @Test
    public void testGetEligibilityWhenEligibilityIsApprovedInDB() {

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
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDB);

        EligibilityProcess eligibilityProcess = new EligibilityProcess(eligibilityApprovalUser, partnerOrganisationInDB, eligibilityStateInDB);
        if (eligibilityApprovalDate != null) {
            eligibilityProcess.setLastModified(eligibilityApprovalDate.atStartOfDay(ZoneId.systemDefault()));
        }

        when(eligibilityWorkflowHandlerMock.getProcess(partnerOrganisationInDB)).thenReturn(eligibilityProcess);

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        projectFinanceInDB.setEligibilityStatus(eligibilityRagStatusInDB);

        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

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