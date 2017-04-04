package org.innovateuk.ifs.project.finance.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.util.PrioritySorting;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.finance.domain.CostCategory;
import org.innovateuk.ifs.project.finance.domain.FinanceCheck;
import org.innovateuk.ifs.project.finance.domain.FinanceCheckProcess;
import org.innovateuk.ifs.project.finance.domain.SpendProfile;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.resource.State;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaim;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostCategoryBuilder.newGrantClaimCostCategory;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostBuilder.newOtherFunding;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostCategoryBuilder.newOtherFundingCostCategory;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.resource.category.LabourCostCategory.WORKING_DAYS_PER_YEAR;
import static org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;
import static org.innovateuk.ifs.project.builder.CostBuilder.newCost;
import static org.innovateuk.ifs.project.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.builder.CostGroupBuilder.newCostGroup;
import static org.innovateuk.ifs.project.builder.CostGroupResourceBuilder.newCostGroupResource;
import static org.innovateuk.ifs.project.builder.CostResourceBuilder.newCostResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.builder.SpendProfileBuilder.newSpendProfile;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckBuilder.newFinanceCheck;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckProcessBuilder.newFinanceCheckProcess;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP_FINANCE_CHECKS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class FinanceCheckServiceImplTest extends BaseServiceUnitTest<FinanceCheckServiceImpl> {

    @Test
    public void testGetByProjectAndOrganisationNotFound() {
        // Set up
        Long projectId = 1L;
        Long organisationId = 2L;
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
        Long projectId = 1L;
        Long organisationId = 2L;
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
        assertEquals(financeCheck.getId(), result.getSuccessObject().getId());
        assertEquals(financeCheck.getOrganisation().getId(), result.getSuccessObject().getOrganisation());
        assertEquals(financeCheck.getProject().getId(), result.getSuccessObject().getProject());
        assertEquals(financeCheck.getCostGroup().getId(), result.getSuccessObject().getCostGroup().getId());
        assertEquals(financeCheck.getCostGroup().getCosts().size(), result.getSuccessObject().getCostGroup().getCosts().size());
        assertEquals(financeCheck.getCostGroup().getCosts().get(0).getCostCategory().getId(), result.getSuccessObject().getCostGroup().getCosts().get(0).getCostCategory().getId());
    }

    @Test
    public void testSaveFinanceCheckValidationFail(){
        Long projectId = 1L;
        Long organisationId = 2L;

        Organisation organisation = newOrganisation().withId(organisationId).withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().withOrganisation(organisation).build();

        FinanceCheckResource financeCheckResource = newFinanceCheckResource().
                withProject(projectId).
                withOrganisation(organisationId).
                withCostGroup(newCostGroupResource().
                        withCosts(newCostResource().
                                withValue(new BigDecimal("1.01"), null, new BigDecimal("-1"), new BigDecimal("1.00")).build(4)).
                        build()).
                build();

        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisation);

        ServiceResult result = service.validate(financeCheckResource);

        assertTrue(result.isFailure());
        assertEquals(3, result.getErrors().size());
        assertEquals(3, result.getErrors().size());
        assertTrue(result.getErrors().contains(new Error(FINANCE_CHECKS_COST_LESS_THAN_ZERO, HttpStatus.BAD_REQUEST)));
        assertTrue(result.getErrors().contains(new Error(FINANCE_CHECKS_CONTAINS_FRACTIONS_IN_COST,  HttpStatus.BAD_REQUEST)));
        assertTrue(result.getErrors().contains(new Error(FINANCE_CHECKS_COST_NULL, HttpStatus.BAD_REQUEST)));
    }

    @Test
    public void testSaveFinanceCheckValidationPass(){
        Long projectId = 1L;
        Long organisationId = 2L;

        Organisation organisation = newOrganisation().withId(organisationId).withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().withOrganisation(organisation).build();

        FinanceCheckResource financeCheckResource = newFinanceCheckResource().
                withProject(projectId).
                withOrganisation(organisationId).
                withCostGroup(newCostGroupResource().
                        withCosts(newCostResource().
                                withValue(new BigDecimal("1.00"), BigDecimal.ZERO, BigDecimal.ONE).build(3)).
                        build()).
                build();

        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisation);

        ServiceResult result = service.validate(financeCheckResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testSaveFinanceCheckValidationPassOnUseOfDecimalsForAcademicPartners(){
        Long projectId = 1L;
        Long organisationId = 2L;

        Organisation organisation = newOrganisation().withId(organisationId).withOrganisationType(OrganisationTypeEnum.RESEARCH).build();
        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().withOrganisation(organisation).build();

        FinanceCheckResource financeCheckResource = newFinanceCheckResource().
                withProject(projectId).
                withOrganisation(organisationId).
                withCostGroup(newCostGroupResource().
                        withCosts(newCostResource().
                                withValue(new BigDecimal("1.10"), BigDecimal.ZERO, BigDecimal.ONE).build(3)).
                        build()).
                build();

        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisation);

        ServiceResult result = service.validate(financeCheckResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetFinanceCheckSummary(){
        Long projectId = 123L;
        Long applicationId = 456L;

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
        List<ProjectFinanceResource> projectFinanceResourceList = newProjectFinanceResource().build(3);
        ProjectTeamStatusResource projectTeamStatus = newProjectTeamStatusResource().build();

        FinanceCheckProcess process = newFinanceCheckProcess().withModifiedDate(new GregorianCalendar()).build();
        ActivityState pendingState = new ActivityState(PROJECT_SETUP_FINANCE_CHECKS, State.PENDING);
        process.setActivityState(pendingState);
        ProjectUserResource projectUser = newProjectUserResource().build();
        UserResource user = newUserResource().build();
        ViabilityResource viability1 = new ViabilityResource(Viability.APPROVED, ViabilityRagStatus.AMBER);
        ViabilityResource viability2 = new ViabilityResource(Viability.NOT_APPLICABLE, ViabilityRagStatus.UNSET);
        ViabilityResource viability3 = new ViabilityResource(Viability.REVIEW, ViabilityRagStatus.UNSET);
        EligibilityResource eligibility1 = new EligibilityResource(Eligibility.APPROVED, EligibilityRagStatus.AMBER);
        EligibilityResource eligibility2 = new EligibilityResource(Eligibility.REVIEW, EligibilityRagStatus.UNSET);
        EligibilityResource eligibility3 = new EligibilityResource(Eligibility.REVIEW, EligibilityRagStatus.UNSET);

        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(partnerOrganisationRepositoryMock.findByProjectId(projectId)).thenReturn(partnerOrganisations);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, partnerOrganisations.get(0).getOrganisation().getId())).thenReturn(spendProfile);
        when(projectFinanceRowServiceMock.financeChecksTotals(project.getId())).thenReturn(serviceSuccess(projectFinanceResourceList));
        when(projectServiceMock.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(serviceSuccess(projectTeamStatus));
        when(financeCheckProcessRepository.findOneByTargetId(partnerOrganisations.get(0).getId())).thenReturn(process);
        when(financeCheckProcessRepository.findOneByTargetId(partnerOrganisations.get(1).getId())).thenReturn(process);
        when(financeCheckProcessRepository.findOneByTargetId(partnerOrganisations.get(2).getId())).thenReturn(process);
        when(projectUserMapperMock.mapToResource(process.getParticipant())).thenReturn(projectUser);
        when(userMapperMock.mapToResource(process.getInternalParticipant())).thenReturn(user);

        when(projectFinanceServiceMock.getViability(new ProjectOrganisationCompositeId(projectId, organisations[0].getId()))).thenReturn(serviceSuccess(viability1));
        when(projectFinanceServiceMock.getViability(new ProjectOrganisationCompositeId(projectId, organisations[1].getId()))).thenReturn(serviceSuccess(viability2));
        when(projectFinanceServiceMock.getViability(new ProjectOrganisationCompositeId(projectId, organisations[2].getId()))).thenReturn(serviceSuccess(viability3));
        when(projectFinanceServiceMock.getEligibility(new ProjectOrganisationCompositeId(projectId, organisations[0].getId()))).thenReturn(serviceSuccess(eligibility1));
        when(projectFinanceServiceMock.getEligibility(new ProjectOrganisationCompositeId(projectId, organisations[1].getId()))).thenReturn(serviceSuccess(eligibility2));
        when(projectFinanceServiceMock.getEligibility(new ProjectOrganisationCompositeId(projectId, organisations[2].getId()))).thenReturn(serviceSuccess(eligibility3));

        ProjectFinanceResource[] projectFinanceResources = newProjectFinanceResource().withId(234L, 345L, 456L).withOrganisation(organisations[0].getId(), organisations[1].getId(), organisations[2].getId()).buildArray(3, ProjectFinanceResource.class);
        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisations[0].getId())).thenReturn(ServiceResult.serviceSuccess(projectFinanceResources[0]));
        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisations[1].getId())).thenReturn(ServiceResult.serviceSuccess(projectFinanceResources[1]));
        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisations[2].getId())).thenReturn(ServiceResult.serviceSuccess(projectFinanceResources[2]));

        QueryResource queryResource1 = new QueryResource(12L, 23L, new ArrayList<>(), FinanceChecksSectionType.ELIGIBILITY, "Title" , true, ZonedDateTime.now());
        QueryResource queryResource2 = new QueryResource(12L, 23L, new ArrayList<>(), FinanceChecksSectionType.ELIGIBILITY, "Title" , false, ZonedDateTime.now());
        when(projectFinanceQueriesService.findAll(234L)).thenReturn(serviceSuccess(Arrays.asList(queryResource1)));
        when(projectFinanceQueriesService.findAll(345L)).thenReturn(serviceSuccess(new ArrayList<>()));
        when(projectFinanceQueriesService.findAll(456L)).thenReturn(serviceSuccess(Arrays.asList(queryResource2)));
        when(financeRowServiceMock.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceSuccess(Double.valueOf(3.0)));
        ServiceResult<FinanceCheckSummaryResource> result = service.getFinanceCheckSummary(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckSummaryResource summary = result.getSuccessObject();
        List<FinanceCheckPartnerStatusResource> partnerStatuses = summary.getPartnerStatusResources();
        assertEquals(3, partnerStatuses.size());

        assertTrue(organisationsOrderedWithLeadOnTopAndPartnersAlphabetically(partnerOrganisations,
                simpleMap(summary.getPartnerStatusResources(), FinanceCheckPartnerStatusResource::getName)));

        FinanceCheckPartnerStatusResource organisation2Results = partnerStatuses.get(0);
        assertEquals(Viability.NOT_APPLICABLE, organisation2Results.getViability());
        assertEquals(ViabilityRagStatus.UNSET, organisation2Results.getViabilityRagStatus());
        assertFalse(organisation2Results.isAwaitingResponse());

        FinanceCheckPartnerStatusResource organisation1Results = partnerStatuses.get(1);
        assertEquals(Viability.APPROVED, organisation1Results.getViability());
        assertEquals(viability1.getViabilityRagStatus(), organisation1Results.getViabilityRagStatus());
        assertTrue(organisation1Results.isAwaitingResponse());

        FinanceCheckPartnerStatusResource organisation3Results = partnerStatuses.get(2);
        assertEquals(Viability.REVIEW, organisation3Results.getViability());
        assertEquals(viability3.getViabilityRagStatus(), organisation3Results.getViabilityRagStatus());
        assertFalse(organisation3Results.isAwaitingResponse());
    }

    private <T> boolean organisationsOrderedWithLeadOnTopAndPartnersAlphabetically(List<PartnerOrganisation> beforeOrdered, List<String> organisationsNames) {
        PartnerOrganisation leadPartner = simpleFindFirst(beforeOrdered, PartnerOrganisation::isLeadOrganisation).get();
        List<PartnerOrganisation> orderedPartnerOrganisations = new PrioritySorting<>(beforeOrdered, leadPartner, po -> po.getOrganisation().getName()).unwrap();
        return organisationsNames.equals(simpleMap(orderedPartnerOrganisations, po -> po.getOrganisation().getName()));
    }

    @Test
    public void testGetFinanceCheckEligibility(){

        Long projectId = 123L;
        Long applicationId = 456L;
        Long organisationId = 789L;

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId).withName("Organisation1").build();


        Map<FinanceRowType, FinanceRowCostCategory> projectFinances = createProjectFinance();

        Map<FinanceRowType, FinanceRowCostCategory> applicationFinances = asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withGrossAnnualSalary(new BigDecimal("1.0"), BigDecimal.ZERO).
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

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource().withFinanceOrganisationDetails(applicationFinances).withGrantClaimPercentage(25).build();

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().
                withProject(projectId).
                withOrganisation(organisation.getId()).
                withFinanceOrganisationDetails(projectFinances).
                build();


        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisationId)).thenReturn(organisation);
        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceSuccess(projectFinanceResource));
        when(financeRowServiceMock.financeDetails(applicationId, organisationId)).thenReturn(serviceSuccess(applicationFinanceResource));

        ServiceResult<FinanceCheckEligibilityResource> result = service.getFinanceCheckEligibilityDetails(projectId, organisationId);
        assertTrue(result.isSuccess());

        FinanceCheckEligibilityResource eligibility = result.getSuccessObject();

        assertTrue(eligibility.getDurationInMonths() == 5L);
        assertTrue(new BigDecimal("5000033.33").compareTo(eligibility.getTotalCost()) == 0);
        assertTrue(new BigDecimal("25").compareTo(eligibility.getPercentageGrant()) == 0);
        assertTrue((new BigDecimal("5000033.33").multiply(new BigDecimal("0.25"))).compareTo(eligibility.getFundingSought()) == 0);
        assertTrue(new BigDecimal("1000").compareTo(eligibility.getOtherPublicSectorFunding()) == 0);
        assertTrue((new BigDecimal("4999033.33").subtract(new BigDecimal("5000033.33").multiply(new BigDecimal("0.25")))).compareTo(eligibility.getContributionToProject()) == 0);
    }

    @Test
    public void testGetFinanceCheckEligibilityNoProjectFinances(){

        Long projectId = 123L;
        Long applicationId = 456L;
        Long organisationId = 789L;

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisationId)).thenReturn(organisation);
        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(GENERAL_NOT_FOUND));

        ServiceResult<FinanceCheckEligibilityResource> result = service.getFinanceCheckEligibilityDetails(projectId, organisationId);
        assertTrue(result.isFailure());

    }

    @Test
    public void testQueryActionRequired() {

        Long projectId = 123L;
        Long applicationId = 456L;
        Long organisationId = 789L;

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();
        QueryResource fakeQuery = new QueryResource(1L, 1L, Collections.emptyList(), FinanceChecksSectionType.ELIGIBILITY, "", true, ZonedDateTime.now());
        List<QueryResource> queries = Collections.singletonList(fakeQuery);

        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceSuccess(resource));
        when(projectFinanceQueriesService.findAll(resource.getId())).thenReturn(serviceSuccess(queries));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertTrue(result.getSuccessObject());
    }

    @Test
    public void testQueryNoActionRequired() {

        Long projectId = 123L;
        Long applicationId = 456L;
        Long organisationId = 789L;

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();
        QueryResource fakeQuery = new QueryResource(1L, 1L, Collections.emptyList(), FinanceChecksSectionType.ELIGIBILITY, "", false, ZonedDateTime.now());
        List<QueryResource> queries = Collections.singletonList(fakeQuery);

        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceSuccess(resource));
        when(projectFinanceQueriesService.findAll(resource.getId())).thenReturn(serviceSuccess(queries));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccessObject());
    }

    @Test
    public void testQueryWithNoProjectFinance() {

        Long projectId = 123L;
        Long applicationId = 456L;
        Long organisationId = 789L;

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();
        QueryResource fakeQuery = new QueryResource(1L, 1L, Collections.emptyList(), FinanceChecksSectionType.ELIGIBILITY, "", false, ZonedDateTime.now());
        List<QueryResource> queries = Collections.singletonList(fakeQuery);

        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(internalServerErrorError()));
        when(projectFinanceQueriesService.findAll(resource.getId())).thenReturn(serviceSuccess(queries));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccessObject());
    }

    @Test
    public void testQueryWithNoQueries() {

        Long projectId = 123L;
        Long applicationId = 456L;
        Long organisationId = 789L;

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();
        List<QueryResource> queries = Collections.emptyList();

        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(internalServerErrorError()));
        when(projectFinanceQueriesService.findAll(resource.getId())).thenReturn(serviceSuccess(queries));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccessObject());
    }

    @Test
    public void testQueryWithQueriesFailure() {

        Long projectId = 123L;
        Long applicationId = 456L;
        Long organisationId = 789L;

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();

        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(internalServerErrorError()));
        when(projectFinanceQueriesService.findAll(resource.getId())).thenReturn(serviceFailure(internalServerErrorError()));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccessObject());
    }

    @Test
    public void testQueryWithNullQueries() {

        Long projectId = 123L;
        Long applicationId = 456L;
        Long organisationId = 789L;

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId, organisationId + 1L).withName("Organisation1").build();
        ProjectFinanceResource resource = newProjectFinanceResource().build();

        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceFailure(internalServerErrorError()));
        when(projectFinanceQueriesService.findAll(resource.getId())).thenReturn(serviceSuccess(null));

        ServiceResult<Boolean> result = service.isQueryActionRequired(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccessObject());
    }

    @Test
    public void testGetFinanceCheckEligibilityNoApplicationFinances(){

        Long projectId = 123L;
        Long applicationId = 456L;
        Long organisationId = 789L;

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withDurationInMonths(5L).build();
        Project project = newProject().withId(projectId).withApplication(application).withDuration(6L).withName("Project1").build();

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).withId(organisationId).withName("Organisation1").build();

        Map<FinanceRowType, FinanceRowCostCategory> projectFinances = createProjectFinance();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().
                withProject(projectId).
                withOrganisation(organisation.getId()).
                withFinanceOrganisationDetails(projectFinances).
                build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisationId)).thenReturn(organisation);
        when(projectFinanceRowServiceMock.financeChecksDetails(projectId, organisationId)).thenReturn(serviceSuccess(projectFinanceResource));
        when(financeRowServiceMock.financeDetails(applicationId, organisationId)).thenReturn(serviceFailure(GENERAL_NOT_FOUND));

        ServiceResult<FinanceCheckEligibilityResource> result = service.getFinanceCheckEligibilityDetails(projectId, organisationId);
        assertTrue(result.isFailure());

    }

    @Test
    public void getFinanceCheckOverview() {
        Long projectId = 123L;
        Long applicationId = 456L;

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
                                withGrossAnnualSalary(new BigDecimal("1.0"), BigDecimal.ZERO).
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


        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(projectFinanceRowServiceMock.financeChecksTotals(projectId)).thenReturn(serviceSuccess(projectFinanceResource));
        when(financeRowServiceMock.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceSuccess(Double.valueOf(3.0)));

        ServiceResult<FinanceCheckOverviewResource> result = service.getFinanceCheckOverview(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckOverviewResource overview = result.getSuccessObject();
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
        Long projectId = 123L;
        Long applicationId = 456L;

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
                                withGrossAnnualSalary(new BigDecimal("1.0"), BigDecimal.ZERO).
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


        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(projectFinanceRowServiceMock.financeChecksTotals(projectId)).thenReturn(serviceSuccess(projectFinanceResource));
        when(financeRowServiceMock.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceFailure(GENERAL_FORBIDDEN));

        ServiceResult<FinanceCheckOverviewResource> result = service.getFinanceCheckOverview(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckOverviewResource overview = result.getSuccessObject();
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
        Long projectId = 123L;
        Long applicationId = 456L;

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
                                withGrossAnnualSalary(new BigDecimal("1.0"), BigDecimal.ZERO).
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


        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(projectFinanceRowServiceMock.financeChecksTotals(projectId)).thenReturn(serviceSuccess(projectFinanceResource));
        when(financeRowServiceMock.getResearchParticipationPercentageFromProject(projectId)).thenReturn(serviceSuccess(null));

        ServiceResult<FinanceCheckOverviewResource> result = service.getFinanceCheckOverview(projectId);
        assertTrue(result.isSuccess());

        FinanceCheckOverviewResource overview = result.getSuccessObject();
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

        FinanceCheckServiceImpl impl = new FinanceCheckServiceImpl();
        return impl;
    }

    private Map<FinanceRowType, FinanceRowCostCategory> createProjectFinance() {
        return asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(
                        newLabourCost().
                                withGrossAnnualSalary(new BigDecimal("10000000"), BigDecimal.ZERO).
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
                        newGrantClaim().
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
}
