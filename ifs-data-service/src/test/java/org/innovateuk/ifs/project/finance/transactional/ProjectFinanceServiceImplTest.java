package org.innovateuk.ifs.project.finance.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.project.builder.ProjectBuilder;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.finance.domain.*;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.validator.util.ValidationUtil;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.project.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static org.innovateuk.ifs.project.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.builder.SpendProfileBuilder.newSpendProfile;
import static org.innovateuk.ifs.project.finance.resource.TimeUnit.MONTH;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectFinanceServiceImplTest extends BaseServiceUnitTest<ProjectFinanceServiceImpl> {

    private Long projectId = 123L;

    private Long organisationId = 456L;

    @Mock
    private SpendProfileCostCategorySummaryStrategy spendProfileCostCategorySummaryStrategy;

    @Mock
    private ValidationUtil validationUtil;

    @Mock
    private Error mockedError;

    @Test
    public void testGenerateSpendProfile() {

        Project project = newProject().
                withId(projectId).
                withDuration(3L).
                withPartnerOrganisations(newPartnerOrganisation().build(2)).
                build();

        UserResource loggedInUser = newUserResource().build();
        setLoggedInUser(loggedInUser);

        Organisation organisation1 = newOrganisation().build();
        Organisation organisation2 = newOrganisation().build();

        // First cost category type and everything that goes with it.
        CostCategory type1Cat1 = newCostCategory().withName(LABOUR.getName()).build();
        CostCategory type1Cat2 = newCostCategory().withName(MATERIALS.getName()).build();

        CostCategoryType costCategoryType1 =
                newCostCategoryType()
                        .withName("Type 1")
                        .withCostCategoryGroup(
                                newCostCategoryGroup()
                                        .withDescription("Group 1")
                                        .withCostCategories(asList(type1Cat1, type1Cat2))
                                        .build())
                        .build();

        // Second cost category type and everything that goes with it.
        CostCategory type2Cat1 = newCostCategory().withName(ACADEMIC.getName()).build();

        CostCategoryType costCategoryType2 = newCostCategoryType()
                .withName("Type 2")
                .withCostCategoryGroup(
                        newCostCategoryGroup()
                                .withDescription("Group 2")
                                .withCostCategories(asList(type2Cat1))
                                .build())
                .build();

        // set basic repository lookup expectations
        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisation1.getId())).thenReturn(organisation1);
        when(organisationRepositoryMock.findOne(organisation2.getId())).thenReturn(organisation2);
        when(costCategoryRepositoryMock.findOne(type1Cat1.getId())).thenReturn(type1Cat1);
        when(costCategoryRepositoryMock.findOne(type1Cat2.getId())).thenReturn(type1Cat2);
        when(costCategoryRepositoryMock.findOne(type2Cat1.getId())).thenReturn(type2Cat1);
        when(costCategoryTypeRepositoryMock.findOne(costCategoryType1.getId())).thenReturn(costCategoryType1);
        when(costCategoryTypeRepositoryMock.findOne(costCategoryType2.getId())).thenReturn(costCategoryType2);
        when(financeCheckProcessRepository.findOneByTargetId(project.getPartnerOrganisations().get(0).getId())).thenReturn(
                new FinanceCheckProcess((User) null, null, new ActivityState(null, State.ACCEPTED)));
        when(financeCheckProcessRepository.findOneByTargetId(project.getPartnerOrganisations().get(1).getId())).thenReturn(
                new FinanceCheckProcess((User) null, null, new ActivityState(null, State.ACCEPTED)));

        // setup expectations for getting project users to infer the partner organisations
        List<ProjectUserResource> projectUsers =
                newProjectUserResource().withOrganisation(organisation1.getId(), organisation2.getId()).build(2);
        when(projectServiceMock.getProjectUsers(projectId)).thenReturn(serviceSuccess(projectUsers));

        // setup expectations for finding finance figures per Cost Category from which to generate the spend profile
        when(spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(project.getId(), organisation1.getId())).thenReturn(serviceSuccess(
                new SpendProfileCostCategorySummaries(
                        asList(
                                new SpendProfileCostCategorySummary(type1Cat1, new BigDecimal("100.00"), project.getDurationInMonths()),
                                new SpendProfileCostCategorySummary(type1Cat2, new BigDecimal("200.00"), project.getDurationInMonths())),
                        costCategoryType1)));

        when(spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(project.getId(), organisation2.getId())).thenReturn(serviceSuccess(
                new SpendProfileCostCategorySummaries(
                        singletonList(new SpendProfileCostCategorySummary(type2Cat1, new BigDecimal("300.66"), project.getDurationInMonths())),
                        costCategoryType2)));

        when(projectFinanceRepositoryMock.findByProjectId(project.getId())).thenReturn(
                newProjectFinance().
                        withViability(Viability.APPROVED, Viability.APPROVED).
                        withOrganisation(organisation1, organisation2).
                        build(2));

        List<Cost> expectedOrganisation1EligibleCosts = asList(
                new Cost("100").withCategory(type1Cat1),
                new Cost("200").withCategory(type1Cat2));

        List<Cost> expectedOrganisation1SpendProfileFigures = asList(
                new Cost("34").withCategory(type1Cat1).withTimePeriod(0, MONTH, 1, MONTH),
                new Cost("33").withCategory(type1Cat1).withTimePeriod(1, MONTH, 1, MONTH),
                new Cost("33").withCategory(type1Cat1).withTimePeriod(2, MONTH, 1, MONTH),
                new Cost("66").withCategory(type1Cat2).withTimePeriod(0, MONTH, 1, MONTH),
                new Cost("67").withCategory(type1Cat2).withTimePeriod(1, MONTH, 1, MONTH),
                new Cost("67").withCategory(type1Cat2).withTimePeriod(2, MONTH, 1, MONTH));

        User generatedBy = newUser().build();
        when(userRepositoryMock.findOne(loggedInUser.getId())).thenReturn(generatedBy);

        Calendar generatedDate = Calendar.getInstance();

        SpendProfile expectedOrganisation1Profile = new SpendProfile(organisation1, project, costCategoryType1,
                expectedOrganisation1EligibleCosts, expectedOrganisation1SpendProfileFigures, generatedBy, generatedDate, false, ApprovalType.UNSET);

        List<Cost> expectedOrganisation2EligibleCosts = singletonList(
                new Cost("301").withCategory(type2Cat1));

        List<Cost> expectedOrganisation2SpendProfileFigures = asList(
                new Cost("101").withCategory(type2Cat1).withTimePeriod(0, MONTH, 1, MONTH),
                new Cost("100").withCategory(type2Cat1).withTimePeriod(1, MONTH, 1, MONTH),
                new Cost("100").withCategory(type2Cat1).withTimePeriod(2, MONTH, 1, MONTH));

        SpendProfile expectedOrganisation2Profile = new SpendProfile(organisation2, project, costCategoryType2,
                expectedOrganisation2EligibleCosts, expectedOrganisation2SpendProfileFigures, generatedBy, generatedDate, false, ApprovalType.UNSET);

        when(spendProfileRepositoryMock.save(spendProfileExpectations(expectedOrganisation1Profile))).thenReturn(null);
        when(spendProfileRepositoryMock.save(spendProfileExpectations(expectedOrganisation2Profile))).thenReturn(null);

        ServiceResult<Void> generateResult = service.generateSpendProfile(projectId);
        assertTrue(generateResult.isSuccess());

        verify(spendProfileRepositoryMock).save(spendProfileExpectations(expectedOrganisation1Profile));
        verify(spendProfileRepositoryMock).save(spendProfileExpectations(expectedOrganisation2Profile));
        verifyNoMoreInteractions(spendProfileRepositoryMock);
    }

    @Test
    public void testGenerateSpendProfileButNotAllFinanceChecksCompleted() {

        GenerateSpendProfileData generateSpendProfileData = new GenerateSpendProfileData().build();

        Project project = generateSpendProfileData.getProject();
        Organisation organisation1 = generateSpendProfileData.getOrganisation1();
        Organisation organisation2 = generateSpendProfileData.getOrganisation2();
        CostCategoryType costCategoryType1 = generateSpendProfileData.getCostCategoryType1();
        CostCategoryType costCategoryType2 = generateSpendProfileData.getCostCategoryType2();
        CostCategory type1Cat1 = generateSpendProfileData.type1Cat1;
        CostCategory type1Cat2 = generateSpendProfileData.type1Cat2;
        CostCategory type2Cat1 = generateSpendProfileData.type2Cat1;

        when(financeCheckProcessRepository.findOneByTargetId(project.getPartnerOrganisations().get(0).getId())).thenReturn(
                new FinanceCheckProcess((User) null, null, new ActivityState(null, State.READY_TO_SUBMIT)));
        when(financeCheckProcessRepository.findOneByTargetId(project.getPartnerOrganisations().get(1).getId())).thenReturn(
                new FinanceCheckProcess((User) null, null, new ActivityState(null, State.ACCEPTED)));

        // setup expectations for getting project users to infer the partner organisations
        List<ProjectUserResource> projectUsers =
                newProjectUserResource().withOrganisation(organisation1.getId(), organisation2.getId()).build(2);
        when(projectServiceMock.getProjectUsers(projectId)).thenReturn(serviceSuccess(projectUsers));

        // setup expectations for finding finance figures per Cost Category from which to generate the spend profile
        when(spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(project.getId(), organisation1.getId())).thenReturn(serviceSuccess(
                new SpendProfileCostCategorySummaries(
                        asList(
                                new SpendProfileCostCategorySummary(type1Cat1, new BigDecimal("100.00"), project.getDurationInMonths()),
                                new SpendProfileCostCategorySummary(type1Cat2, new BigDecimal("200.00"), project.getDurationInMonths())),
                        costCategoryType1)));

        when(spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(project.getId(), organisation2.getId())).thenReturn(serviceSuccess(
                new SpendProfileCostCategorySummaries(
                        singletonList(new SpendProfileCostCategorySummary(type2Cat1, new BigDecimal("300.66"), project.getDurationInMonths())),
                        costCategoryType2)));

        ServiceResult<Void> generateResult = service.generateSpendProfile(projectId);
        assertTrue(generateResult.isFailure());
        assertTrue(generateResult.getFailure().is(SPEND_PROFILE_CANNOT_BE_GENERATED_UNTIL_ALL_FINANCE_CHECKS_APPROVED_OR_NOT_APPLICABLE));

        verify(spendProfileRepositoryMock, never()).save(isA(SpendProfile.class));
        verifyNoMoreInteractions(spendProfileRepositoryMock);
    }

    @Test
    public void testGenerateSpendProfileButNotAllViabilityApproved() {

        GenerateSpendProfileData generateSpendProfileData = new GenerateSpendProfileData().build();

        Project project = generateSpendProfileData.getProject();
        Organisation organisation1 = generateSpendProfileData.getOrganisation1();
        Organisation organisation2 = generateSpendProfileData.getOrganisation2();

        setupGenerateSpendProfilesExpectations(generateSpendProfileData, project, organisation1, organisation2);

        when(projectFinanceRepositoryMock.findByProjectId(project.getId())).thenReturn(
                newProjectFinance().
                        withViability(Viability.APPROVED, Viability.REVIEW).
                        withOrganisation(organisation1, organisation2).
                        build(2));

        ServiceResult<Void> generateResult = service.generateSpendProfile(projectId);
        assertTrue(generateResult.isFailure());
        assertTrue(generateResult.getFailure().is(SPEND_PROFILE_CANNOT_BE_GENERATED_UNTIL_ALL_VIABILITY_APPROVED));

        verify(spendProfileRepositoryMock, never()).save(isA(SpendProfile.class));
        verifyNoMoreInteractions(spendProfileRepositoryMock);
    }

    @Test
    public void testGenerateSpendProfileWhenAllViabilityApprovedButAcademicViabilityNotApplicable() {

        GenerateSpendProfileData generateSpendProfileData = new GenerateSpendProfileData().build();

        Project project = generateSpendProfileData.getProject();
        Organisation organisation1 = generateSpendProfileData.getOrganisation1();
        Organisation organisation2 = generateSpendProfileData.getOrganisation2();

        setupGenerateSpendProfilesExpectations(generateSpendProfileData, project, organisation1, organisation2);

        when(projectFinanceRepositoryMock.findByProjectId(project.getId())).thenReturn(
                newProjectFinance().
                        withViability(Viability.APPROVED, Viability.NOT_APPLICABLE).
                        withOrganisation(organisation1, organisation2).
                        build(2));

        ServiceResult<Void> generateResult = service.generateSpendProfile(projectId);
        assertTrue(generateResult.isSuccess());

        verify(spendProfileRepositoryMock, times(2)).save(isA(SpendProfile.class));
        verifyNoMoreInteractions(spendProfileRepositoryMock);
    }

    @Test
    public void testGenerateSpendProfileCSV() {
        Project project = newProject().withId(projectId).withDuration(3L).withTargetStartDate(LocalDate.of(2018, 3, 1)).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        SpendProfile spendProfileInDB = createSpendProfile(project,
                // eligible costs
                asMap(
                        1L, new BigDecimal("100"),
                        2L, new BigDecimal("180"),
                        3L, new BigDecimal("55")),

                // Spend Profile costs
                asMap(
                        1L, asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("50")),
                        2L, asList(new BigDecimal("70"), new BigDecimal("50"), new BigDecimal("60")),
                        3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0")))
        );
        CostCategory testCostCategory = new CostCategory();
        testCostCategory.setId(1L);
        testCostCategory.setName("One");
        testCostCategory.setLabel("Group Name");

        OrganisationType organisationType = new OrganisationType();
        organisationType.setId(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId());
        Organisation organisation1 = newOrganisation().withId(organisationId).withOrganisationType(organisationType).withName("TEST").build();
        when(organisationRepositoryMock.findOne(organisation1.getId())).thenReturn(organisation1);
        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(spendProfileInDB));
        when(costCategoryRepositoryMock.findOne(anyLong())).thenReturn(testCostCategory);
        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ServiceResult<SpendProfileCSVResource> serviceResult = service.getSpendProfileCSV(projectOrganisationCompositeId);

        assertTrue(serviceResult.getSuccessObject().getFileName().startsWith("TEST_Spend_Profile_"+dateFormat.format(date)));
        assertTrue(serviceResult.getSuccessObject().getCsvData().contains("Group Name"));
        assertEquals(Arrays.asList(serviceResult.getSuccessObject().getCsvData().split("\n")).stream().filter(s -> s.contains("Group Name")
                && !s.contains("Month") && !s.contains("TOTAL")).count(), 3);
    }

    @Test
    public void testGenerateSpendProfileCSVWithCategoryGroupLabelEmpty() {
        Project project = newProject().withId(projectId).withDuration(3L).withTargetStartDate(LocalDate.of(2018, 3, 1)).build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        SpendProfile spendProfileInDB = createSpendProfile(project,
                // eligible costs
                asMap(
                        1L, new BigDecimal("100"),
                        2L, new BigDecimal("180"),
                        3L, new BigDecimal("55")),

                // Spend Profile costs
                asMap(
                        1L, asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("50")),
                        2L, asList(new BigDecimal("70"), new BigDecimal("50"), new BigDecimal("60")),
                        3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0")))
        );
        CostCategory testCostCategory = new CostCategory();
        testCostCategory.setId(1L);
        testCostCategory.setName("One");

        OrganisationType organisationType = new OrganisationType();
        organisationType.setId(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId());
        Organisation organisation1 = newOrganisation().withId(organisationId).withOrganisationType(organisationType).withName("TEST").build();
        when(organisationRepositoryMock.findOne(organisation1.getId())).thenReturn(organisation1);
        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(spendProfileInDB));
        when(costCategoryRepositoryMock.findOne(anyLong())).thenReturn(testCostCategory);
        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ServiceResult<SpendProfileCSVResource> serviceResult = service.getSpendProfileCSV(projectOrganisationCompositeId);

        assertTrue(serviceResult.getSuccessObject().getFileName().startsWith("TEST_Spend_Profile_"+dateFormat.format(date)));
        assertFalse(serviceResult.getSuccessObject().getCsvData().contains("Group Name"));
        assertEquals(Arrays.asList(serviceResult.getSuccessObject().getCsvData().split("\n")).stream().filter(s -> s.contains("Group Name")
                && !s.contains("Month") && !s.contains("TOTAL")).count(), 0);
    }

    @Test
    public void getSpendProfileStatusByProjectIdApproved() {
        List<SpendProfile> spendProfileList = newSpendProfile().withApproval(ApprovalType.APPROVED, ApprovalType.APPROVED, ApprovalType.APPROVED).build(3);
        when(spendProfileRepositoryMock.findByProjectId(projectId)).thenReturn(spendProfileList);

        ServiceResult<ApprovalType> result = service.getSpendProfileStatusByProjectId(projectId);
        assertTrue(result.isSuccess());
        assertEquals(ApprovalType.APPROVED, result.getSuccessObject());
    }

    @Test
    public void getSpendProfileStatusByProjectIdRejected() {
        List<SpendProfile> spendProfileList = newSpendProfile().withApproval(ApprovalType.REJECTED, ApprovalType.APPROVED, ApprovalType.UNSET).build(3);
        when(spendProfileRepositoryMock.findByProjectId(projectId)).thenReturn(spendProfileList);

        ServiceResult<ApprovalType> result = service.getSpendProfileStatusByProjectId(projectId);
        assertTrue(result.isSuccess());
        assertEquals(ApprovalType.REJECTED, result.getSuccessObject());
    }

    @Test
    public void getSpendProfileStatusByProjectIdUnset() {
        List<SpendProfile> spendProfileList = newSpendProfile().withApproval(ApprovalType.UNSET, ApprovalType.UNSET, ApprovalType.UNSET).build(3);
        when(spendProfileRepositoryMock.findByProjectId(projectId)).thenReturn(spendProfileList);

        ServiceResult<ApprovalType> result = service.getSpendProfileStatusByProjectId(projectId);
        assertTrue(result.isSuccess());
        assertEquals(ApprovalType.UNSET, result.getSuccessObject());
    }

    @Test
    public void approveSpendProfile() {
        List<SpendProfile> spendProfileList = getSpendProfilesAndSetWhenSpendProfileRepositoryMock(projectId);

        when(projectGrantOfferServiceMock.generateGrantOfferLetterIfReady(projectId)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.approveOrRejectSpendProfile(projectId, ApprovalType.APPROVED);

        assertTrue(result.isSuccess());
        spendProfileList.forEach(spendProfile ->
                assertEquals(ApprovalType.APPROVED, spendProfile.getApproval())
        );
        verify(spendProfileRepositoryMock).save(spendProfileList);
    }

    @Test
    public void rejectSpendProfile() {
        Long projectId = 4234L;
        List<SpendProfile> spendProfileList = getSpendProfilesAndSetWhenSpendProfileRepositoryMock(projectId);
        Project project = newProject().withId(projectId).withDuration(3L).withTargetStartDate(LocalDate.of(2018, 3, 1)).withSpendProfileSubmittedDate(LocalDateTime.now()).build();


        when(projectGrantOfferServiceMock.generateGrantOfferLetterIfReady(projectId)).thenReturn(serviceSuccess());
        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);

        ServiceResult<Void> resultNew = service.approveOrRejectSpendProfile(projectId, ApprovalType.REJECTED);

        assertTrue(resultNew.isSuccess());
        spendProfileList.forEach(spendProfile ->
                assertEquals(ApprovalType.REJECTED, spendProfile.getApproval())
        );
        assertTrue(project.getSpendProfileSubmittedDate() == null);

        verify(spendProfileRepositoryMock).save(spendProfileList);
    }

    @Test
    public void approveSpendProfileGenerateGolFails() {
        Long projectId = 49544L;
        List<SpendProfile> spendProfileList = getSpendProfilesAndSetWhenSpendProfileRepositoryMock(projectId);

        when(projectGrantOfferServiceMock.generateGrantOfferLetterIfReady(projectId)).thenReturn(serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_FAILURE));

        ServiceResult<Void> result = service.approveOrRejectSpendProfile(projectId, ApprovalType.APPROVED);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_FAILURE));
        spendProfileList.forEach(spendProfile ->
                assertEquals(ApprovalType.APPROVED, spendProfile.getApproval())
        );
        verify(spendProfileRepositoryMock).save(spendProfileList);
    }


    @Test
    public void saveSpendProfileWhenValidationFails() {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        SpendProfileTableResource table = new SpendProfileTableResource();

        // the validation is tested in the validator related unit tests
        table.setMonthlyCostsPerCategoryMap(Collections.emptyMap());

        ValidationMessages validationMessages = new ValidationMessages();
        validationMessages.setErrors(Collections.singletonList(mockedError));

        when(validationUtil.validateSpendProfileTableResource(eq(table))).thenReturn(Optional.of(validationMessages));

        ServiceResult<Void> result = service.saveSpendProfile(projectOrganisationCompositeId, table);

        assertFalse(result.isSuccess());
    }

    @Test
    public void saveSpendProfileEnsureSpendProfileDomainIsCorrectlyUpdated() {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        SpendProfileTableResource table = new SpendProfileTableResource();

        table.setEligibleCostPerCategoryMap(asMap(
                1L, new BigDecimal("100"),
                2L, new BigDecimal("180"),
                3L, new BigDecimal("55")));

        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("70"), new BigDecimal("50"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0"))));
        List<Cost> spendProfileFigures = buildCostsForCategories(Arrays.asList(1L, 2L, 3L), 3);
        User generatedBy = newUser().build();
        Calendar generatedDate = Calendar.getInstance();

        SpendProfile spendProfileInDB = new SpendProfile(null, newProject().build(), null, Collections.emptyList(), spendProfileFigures, generatedBy, generatedDate, false, ApprovalType.UNSET);

        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(spendProfileInDB));
        when(validationUtil.validateSpendProfileTableResource(eq(table))).thenReturn(Optional.empty());


        // Before the call (ie before the SpendProfile is updated), ensure that the values are set to 1
        assertCostForCategoryForGivenMonth(spendProfileInDB, 1L, 0, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 1L, 1, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 1L, 2, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 2L, 0, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 2L, 1, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 2L, 2, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 3L, 0, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 3L, 1, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 3L, 2, BigDecimal.ONE);

        ServiceResult<Void> result = service.saveSpendProfile(projectOrganisationCompositeId, table);

        assertTrue(result.isSuccess());

        // Assert that the SpendProfile domain is correctly updated
        assertCostForCategoryForGivenMonth(spendProfileInDB, 1L, 0, new BigDecimal("30"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 1L, 1, new BigDecimal("30"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 1L, 2, new BigDecimal("40"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 2L, 0, new BigDecimal("70"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 2L, 1, new BigDecimal("50"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 2L, 2, new BigDecimal("60"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 3L, 0, new BigDecimal("50"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 3L, 1, new BigDecimal("5"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 3L, 2, new BigDecimal("0"));

        verify(spendProfileRepositoryMock).save(spendProfileInDB);

    }

    @Test
    public void markSpendProfileIncomplete() {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        Project projectInDB = ProjectBuilder.newProject()
                .withDuration(3L)
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withId(projectId)
                .build();

        SpendProfile spendProfileInDB = createSpendProfile(projectInDB,
                // eligible costs
                asMap(
                        1L, new BigDecimal("100"),
                        2L, new BigDecimal("180"),
                        3L, new BigDecimal("55")),

                // Spend Profile costs
                asMap(
                        1L, asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("50")),
                        2L, asList(new BigDecimal("70"), new BigDecimal("50"), new BigDecimal("60")),
                        3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0")))
        );

        spendProfileInDB.setMarkedAsComplete(true);

        OrganisationType organisationType = new OrganisationType();
        organisationType.setId(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId());
        Organisation organisation1 = newOrganisation().withId(organisationId).withOrganisationType(organisationType).withName("TEST").build();
        when(organisationRepositoryMock.findOne(organisation1.getId())).thenReturn(organisation1);
        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(spendProfileInDB));

        ServiceResult<Void> result = service.markSpendProfileIncomplete(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());
        assertFalse(spendProfileInDB.isMarkedAsComplete());
    }

    @Test
    public void markSpendProfileWhenActualTotalsGreaterThanEligibleCosts() {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        Project projectInDB = ProjectBuilder.newProject()
                .withDuration(3L)
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .build();

        SpendProfile spendProfileInDB = createSpendProfile(projectInDB,
                // eligible costs
                asMap(
                        1L, new BigDecimal("100"),
                        2L, new BigDecimal("180"),
                        3L, new BigDecimal("55")),

                // Spend Profile costs
                asMap(
                        1L, asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("50")),
                        2L, asList(new BigDecimal("70"), new BigDecimal("50"), new BigDecimal("60")),
                        3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0")))
        );

        OrganisationType organisationType = new OrganisationType();
        organisationType.setId(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId());
        Organisation organisation1 = newOrganisation().withId(organisationId).withOrganisationType(organisationType).withName("TEST").build();
        when(organisationRepositoryMock.findOne(organisation1.getId())).thenReturn(organisation1);
        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(spendProfileInDB));

        ServiceResult<Void> result = service.markSpendProfileComplete(projectOrganisationCompositeId);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE));

    }

    @Test
    public void markSpendProfileSuccessWhenActualTotalsAreLessThanEligibleCosts() {

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        Project projectInDB = ProjectBuilder.newProject()
                .withDuration(3L)
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .build();

        SpendProfile spendProfileInDB = createSpendProfile(projectInDB,
                // eligible costs
                asMap(
                        1L, new BigDecimal("100"),
                        2L, new BigDecimal("180"),
                        3L, new BigDecimal("55")),

                // Spend Profile costs
                asMap(
                        1L, asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("40")),
                        2L, asList(new BigDecimal("70"), new BigDecimal("10"), new BigDecimal("60")),
                        3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0")))
        );


        OrganisationType organisationType = new OrganisationType();
        organisationType.setId(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId());
        Organisation organisation1 = newOrganisation().withId(organisationId).withOrganisationType(organisationType).withName("TEST").build();
        when(organisationRepositoryMock.findOne(organisation1.getId())).thenReturn(organisation1);
        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(spendProfileInDB));

        ServiceResult<Void> result = service.markSpendProfileComplete(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

    }


    @Test
    public void testCompleteSpendProfilesReviewSuccess() {
        Project projectInDb = new Project();
        projectInDb.setSpendProfileSubmittedDate(null);
        SpendProfile spendProfileInDb = new SpendProfile();
        spendProfileInDb.setMarkedAsComplete(true);
        projectInDb.setSpendProfiles(asList(spendProfileInDb));
        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDb);
        assertThat(projectInDb.getSpendProfileSubmittedDate(), nullValue());

        ServiceResult<Void> result = service.completeSpendProfilesReview(projectId);

        assertTrue(result.isSuccess());
        assertThat(projectInDb.getSpendProfileSubmittedDate(), notNullValue());
    }


    @Test
    public void testCompleteSpendProfilesReviewFailureWhenSpendProfileIncomplete() {
        Project projectInDb = new Project();
        projectInDb.setSpendProfileSubmittedDate(null);
        SpendProfile spendProfileInDb = new SpendProfile();
        spendProfileInDb.setMarkedAsComplete(false);
        projectInDb.setSpendProfiles(asList(spendProfileInDb));
        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDb);
        assertThat(projectInDb.getSpendProfileSubmittedDate(), nullValue());

        ServiceResult<Void> result = service.completeSpendProfilesReview(projectId);

        assertTrue(result.isFailure());
    }

    @Test
    public void testCompleteSpendProfilesReviewFailureWhenAlreadySubmitted() {
        Project projectInDb = new Project();
        projectInDb.setSpendProfileSubmittedDate(LocalDateTime.now());
        SpendProfile spendProfileInDb = new SpendProfile();
        spendProfileInDb.setMarkedAsComplete(true);
        projectInDb.setSpendProfiles(asList(spendProfileInDb));
        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDb);

        ServiceResult<Void> result = service.completeSpendProfilesReview(projectId);

        assertTrue(result.isFailure());
    }

    @Test
    public void testGetViabilitySuccess() {

        Long userId = 7L;

        User user = newUser()
                .withId(userId)
                .withFirstName("Lee")
                .withLastName("Bowman")
                .build();

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        projectFinanceInDB.setViability(Viability.APPROVED);
        projectFinanceInDB.setViabilityStatus(ViabilityStatus.GREEN);
        projectFinanceInDB.setViabilityApprovalUser(user);
        projectFinanceInDB.setViabilityApprovalDate(LocalDate.now());
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<ViabilityResource> result = service.getViability(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        ViabilityResource returnedViabilityResource = result.getSuccessObject();

        assertEquals(Viability.APPROVED, returnedViabilityResource.getViability());
        assertEquals(ViabilityStatus.GREEN, returnedViabilityResource.getViabilityStatus());

        assertEquals("Lee", returnedViabilityResource.getViabilityApprovalUserFirstName());
        assertEquals("Bowman", returnedViabilityResource.getViabilityApprovalUserLastName());
        assertEquals(LocalDate.now(), returnedViabilityResource.getViabilityApprovalDate());

    }

    @Test
    public void testSaveViabilityWhenViabilityAlreadyApproved() {

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        projectFinanceInDB.setViability(Viability.APPROVED);
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, Viability.APPROVED, ViabilityStatus.AMBER);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(VIABILITY_HAS_ALREADY_BEEN_APPROVED));

        verify(projectFinanceRepositoryMock, never()).save(projectFinanceInDB);

    }

    @Test
    public void testSaveViabilityWhenViabilityStatusIsUnset() {

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, Viability.APPROVED, ViabilityStatus.UNSET);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(VIABILITY_RAG_STATUS_MUST_BE_SET));

        verify(projectFinanceRepositoryMock, never()).save(projectFinanceInDB);

    }

    @Test
    public void testSaveViabilityWhenViabilityStatusIsUnsetButViabilityAlsoNotApproved() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        ProjectFinance projectFinanceInDB = setUpSaveViabilityMocking(user);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, Viability.REVIEW, ViabilityStatus.UNSET);

        assertTrue(result.isSuccess());

        assertSaveViabilityResults(projectFinanceInDB, Viability.REVIEW, ViabilityStatus.UNSET, null, null);
    }

    @Test
    public void testSaveViabilityWhenViabilityStatusIsSetButViabilityNotApproved() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        ProjectFinance projectFinanceInDB = setUpSaveViabilityMocking(user);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, Viability.REVIEW, ViabilityStatus.AMBER);

        assertTrue(result.isSuccess());

        assertSaveViabilityResults(projectFinanceInDB, Viability.REVIEW, ViabilityStatus.AMBER, null, null);
    }

    @Test
    public void testSaveViabilitySuccess() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        ProjectFinance projectFinanceInDB = setUpSaveViabilityMocking(user);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveViability(projectOrganisationCompositeId, Viability.APPROVED, ViabilityStatus.AMBER);

        assertTrue(result.isSuccess());

        assertSaveViabilityResults(projectFinanceInDB, Viability.APPROVED, ViabilityStatus.AMBER, user, LocalDate.now());

    }

    private ProjectFinance setUpSaveViabilityMocking(User user) {

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        return projectFinanceInDB;

    }

    private void assertSaveViabilityResults(ProjectFinance projectFinanceInDB, Viability expectedViability, ViabilityStatus expectedViabilityStatus,
                                            User expectedApprovalUser, LocalDate expectedApprovalTime) {

        assertEquals(expectedViability, projectFinanceInDB.getViability());
        assertEquals(expectedViabilityStatus, projectFinanceInDB.getViabilityStatus());
        assertEquals(expectedApprovalUser, projectFinanceInDB.getViabilityApprovalUser());
        assertEquals(expectedApprovalTime, projectFinanceInDB.getViabilityApprovalDate());

        verify(projectFinanceRepositoryMock).save(projectFinanceInDB);
    }

    @Test
    public void testGetEligibilitySuccess() {

        Long userId = 7L;

        User user = newUser()
                .withId(userId)
                .withFirstName("Lee")
                .withLastName("Bowman")
                .build();

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        projectFinanceInDB.setEligibility(Eligibility.APPROVED);
        projectFinanceInDB.setEligibilityStatus(EligibilityStatus.GREEN);
        projectFinanceInDB.setEligibilityApprovalDate(LocalDate.now());
        projectFinanceInDB.setEligibilityApprovalUser(user);

        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<EligibilityResource> result = service.getEligibility(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());

        EligibilityResource returnedEligibilityResource = result.getSuccessObject();

        assertEquals(Eligibility.APPROVED, returnedEligibilityResource.getEligibility());
        assertEquals(EligibilityStatus.GREEN, returnedEligibilityResource.getEligibilityStatus());

        assertEquals("Lee", returnedEligibilityResource.getEligibilityApprovalUserFirstName());
        assertEquals("Bowman", returnedEligibilityResource.getEligibilityApprovalUserLastName());
        assertEquals(LocalDate.now(), returnedEligibilityResource.getEligibilityApprovalDate());

    }

    @Test
    public void testSaveEligibilityWhenEligibilityAlreadyApproved() {

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        projectFinanceInDB.setEligibility(Eligibility.APPROVED);
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, Eligibility.APPROVED, EligibilityStatus.AMBER);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(ELIGIBILITY_HAS_ALREADY_BEEN_APPROVED));

        verify(projectFinanceRepositoryMock, never()).save(projectFinanceInDB);
    }

    @Test
    public void testSaveEligibilityWhenEligibilityApprovedButStatusIsUnset() {

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, Eligibility.APPROVED, EligibilityStatus.UNSET);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(ELIGIBILITY_RAG_STATUS_MUST_BE_SET));

        verify(projectFinanceRepositoryMock, never()).save(projectFinanceInDB);
    }

    @Test
    public void testSaveEligibilityWhenEligibilityNotApprovedAndStatusIsUnset() {

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, Eligibility.REVIEW, EligibilityStatus.UNSET);

        assertTrue(result.isSuccess());

        assertSaveEligibilityResults(projectFinanceInDB, Eligibility.REVIEW, EligibilityStatus.UNSET, null, null);
    }

    @Test
    public void testSaveEligibilityWhenEligibilityNotApprovedAndStatusIsSet() {

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, Eligibility.REVIEW, EligibilityStatus.AMBER);

        assertTrue(result.isSuccess());

        assertSaveEligibilityResults(projectFinanceInDB, Eligibility.REVIEW, EligibilityStatus.AMBER, null, null);

    }

    @Test
    public void testSaveEligibilitySuccess() {

        Long userId = 7L;
        User user = newUser().withId(userId).build();

        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> result = service.saveEligibility(projectOrganisationCompositeId, Eligibility.APPROVED, EligibilityStatus.GREEN);

        assertTrue(result.isSuccess());

        assertSaveEligibilityResults(projectFinanceInDB, Eligibility.APPROVED, EligibilityStatus.GREEN, user, LocalDate.now());
    }

    private void assertSaveEligibilityResults(ProjectFinance projectFinanceInDB, Eligibility expectedEligibility, EligibilityStatus expectedEligibilityStatus,
                                            User expectedApprovalUser, LocalDate expectedApprovalDate) {

        assertEquals(expectedEligibility, projectFinanceInDB.getEligibility());
        assertEquals(expectedEligibilityStatus, projectFinanceInDB.getEligibilityStatus());
        assertEquals(expectedApprovalUser, projectFinanceInDB.getEligibilityApprovalUser());
        assertEquals(expectedApprovalDate, projectFinanceInDB.getEligibilityApprovalDate());

        verify(projectFinanceRepositoryMock).save(projectFinanceInDB);
    }

    @Test
    public void testGetCreditReportSuccess() {

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        projectFinanceInDB.setCreditReportConfirmed(true);
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);
        ServiceResult<Boolean> result = service.getCreditReport(projectId, organisationId);

        assertTrue(result.isSuccess());
        assertEquals(true, result.getSuccessObject());

        verify(projectFinanceRepositoryMock).findByProjectIdAndOrganisationId(projectId, organisationId);
    }

    @Test
    public void testSaveCreditSuccess() {

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        ServiceResult<Void> result = service.saveCreditReport(projectId, organisationId, true);

        assertTrue(result.isSuccess());

        assertEquals(true, projectFinanceInDB.getCreditReportConfirmed());
        verify(projectFinanceRepositoryMock).save(projectFinanceInDB);

    }

    @Test
    public void testSaveCreditFailsBecauseViabilityIsAlreadyApproved() {

        ProjectFinance projectFinanceInDB = new ProjectFinance();
        projectFinanceInDB.setViability(Viability.APPROVED);

        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(projectFinanceInDB);

        ServiceResult<Void> result = service.saveCreditReport(projectId, organisationId, true);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(VIABILITY_HAS_ALREADY_BEEN_APPROVED));
    }

    private SpendProfile createSpendProfile(Project projectInDB, Map<Long, BigDecimal> eligibleCostsMap, Map<Long, List<BigDecimal>> spendProfileCostsMap) {
        CostCategoryType costCategoryType = createCostCategoryType();

        List<Cost> eligibleCosts = buildEligibleCostsForCategories(eligibleCostsMap, costCategoryType.getCostCategoryGroup());

        List<Cost> spendProfileFigures = buildCostsForCategoriesWithGivenValues(spendProfileCostsMap, 3, costCategoryType.getCostCategoryGroup());

        User generatedBy = newUser().build();
        Calendar generatedDate = Calendar.getInstance();

        SpendProfile spendProfileInDB = new SpendProfile(null, projectInDB, costCategoryType, eligibleCosts, spendProfileFigures, generatedBy, generatedDate, true, ApprovalType.UNSET);

        return spendProfileInDB;

    }

    private CostCategoryType createCostCategoryType() {

        CostCategoryGroup costCategoryGroup = createCostCategoryGroup();

        CostCategoryType costCategoryType = new CostCategoryType("Cost Category Type for Categories Labour, Materials, Other costs", costCategoryGroup);

        return costCategoryType;

    }

    private CostCategoryGroup createCostCategoryGroup() {

        List<CostCategory> costCategories = createCostCategories(Arrays.asList(1L, 2L, 3L));

        CostCategoryGroup costCategoryGroup = new CostCategoryGroup("Cost Category Group for Categories Labour, Materials, Other costs", costCategories);

        return costCategoryGroup;
    }

    private List<CostCategory> createCostCategories(List<Long> categories) {

        List<CostCategory> costCategories = new ArrayList<>();

        categories.stream().forEach(category -> {
            CostCategory costCategory = new CostCategory();
            costCategory.setId(category);
            costCategories.add(costCategory);
        });

        return costCategories;
    }

    private List<Cost> buildEligibleCostsForCategories(Map<Long, BigDecimal> categoryCost, CostCategoryGroup costCategoryGroup) {

        List<Cost> eligibleCostForAllCategories = new ArrayList<>();

        categoryCost.forEach((category, value) -> {

            eligibleCostForAllCategories.add(createEligibleCost(category, value, costCategoryGroup));

        });

        return eligibleCostForAllCategories;

    }

    private Cost createEligibleCost(Long categoryId, BigDecimal value, CostCategoryGroup costCategoryGroup) {

        CostCategory costCategory = new CostCategory();
        costCategory.setId(categoryId);
        costCategory.setCostCategoryGroup(costCategoryGroup);

        Cost cost = new Cost();
        cost.setCostCategory(costCategory);
        cost.setValue(value);

        return cost;

    }

    private void assertCostForCategoryForGivenMonth(SpendProfile spendProfileInDB, Long category, Integer whichMonth, BigDecimal expectedValue) {

        boolean thisCostShouldExist = spendProfileInDB.getSpendProfileFigures().getCosts().stream()
                .anyMatch(cost -> cost.getCostCategory().getId() == category
                        && cost.getCostTimePeriod().getOffsetAmount().equals(whichMonth)
                        && cost.getValue().equals(expectedValue));
        Assert.assertTrue(thisCostShouldExist);
    }

    private List<Cost> buildCostsForCategories(List<Long> categories, int totalMonths) {

        List<Cost> costForAllCategories = new ArrayList<>();

        categories.forEach(category -> {

            // Intentionally insert in the reverse order of months to ensure that the sorting functionality actually works
            for (int index = totalMonths - 1; index >= 0; index--) {
                costForAllCategories.add(createCost(category, index, BigDecimal.ONE, null));
            }
        });

        return costForAllCategories;

    }

    private List<Cost> buildCostsForCategoriesWithGivenValues(Map<Long, List<BigDecimal>> categoryCosts, int totalMonths, CostCategoryGroup costCategoryGroup) {

        List<Cost> costForAllCategories = new ArrayList<>();

        categoryCosts.forEach((category, costs) -> {

            for (int index = 0; index < totalMonths; index++) {
                costForAllCategories.add(createCost(category, index, costs.get(index), costCategoryGroup));
            }
        });

        return costForAllCategories;

    }

    private Cost createCost(Long categoryId, Integer offsetAmount, BigDecimal value, CostCategoryGroup costCategoryGroup) {

        CostCategory costCategory = new CostCategory();
        costCategory.setId(categoryId);
        costCategory.setCostCategoryGroup(costCategoryGroup);

        //CostTimePeriod(Integer offsetAmount, TimeUnit offsetUnit, Integer durationAmount, TimeUnit durationUnit)
        CostTimePeriod costTimePeriod = new CostTimePeriod(offsetAmount, TimeUnit.MONTH, 1, TimeUnit.MONTH);

        Cost cost = new Cost();
        cost.setCostCategory(costCategory);
        cost.setCostTimePeriod(costTimePeriod);
        cost.setValue(value);

        return cost;

    }

    private SpendProfile spendProfileExpectations(SpendProfile expectedSpendProfile) {
        return createLambdaMatcher(spendProfile -> {

            assertEquals(expectedSpendProfile.getOrganisation(), spendProfile.getOrganisation());
            assertEquals(expectedSpendProfile.getProject(), spendProfile.getProject());
            assertEquals(expectedSpendProfile.getCostCategoryType(), spendProfile.getCostCategoryType());

            CostGroup expectedEligibles = expectedSpendProfile.getEligibleCosts();
            CostGroup actualEligibles = spendProfile.getEligibleCosts();
            assertCostGroupsEqual(expectedEligibles, actualEligibles);

            CostGroup expectedSpendFigures = expectedSpendProfile.getSpendProfileFigures();
            CostGroup actualSpendFigures = spendProfile.getSpendProfileFigures();
            assertCostGroupsEqual(expectedSpendFigures, actualSpendFigures);

            assertEquals(expectedSpendProfile.getGeneratedBy(), spendProfile.getGeneratedBy());
            assertTrue(spendProfile.getGeneratedDate().getTimeInMillis() - expectedSpendProfile.getGeneratedDate().getTimeInMillis() < 100);
        });
    }

    private void assertCostGroupsEqual(CostGroup expected, CostGroup actual) {
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getCosts().size(), actual.getCosts().size());
        expected.getCosts().forEach(expectedCost ->
                assertTrue(simpleFindFirst(actual.getCosts(), actualCost -> costsMatch(expectedCost, actualCost)).isPresent())
        );
    }

    private boolean costsMatch(Cost expectedCost, Cost actualCost) {
        try {
            CostGroup expectedCostGroup = expectedCost.getCostGroup();
            CostGroup actualCostGroup = actualCost.getCostGroup();
            assertEquals(expectedCostGroup != null, actualCostGroup != null);

            if (expectedCostGroup != null) {
                assertEquals(expectedCostGroup.getDescription(), actualCostGroup.getDescription());
            }

            assertEquals(expectedCost.getValue(), actualCost.getValue());
            assertEquals(expectedCost.getCostCategory(), actualCost.getCostCategory());

            CostTimePeriod expectedTimePeriod = expectedCost.getCostTimePeriod();
            CostTimePeriod actualTimePeriod = actualCost.getCostTimePeriod();
            assertEquals(expectedTimePeriod != null, actualTimePeriod != null);

            if (expectedTimePeriod != null) {
                assertEquals(expectedTimePeriod.getOffsetAmount(), actualTimePeriod.getOffsetAmount());
                assertEquals(expectedTimePeriod.getOffsetUnit(), actualTimePeriod.getOffsetUnit());
                assertEquals(expectedTimePeriod.getDurationAmount(), actualTimePeriod.getDurationAmount());
                assertEquals(expectedTimePeriod.getDurationUnit(), actualTimePeriod.getDurationUnit());
            }

            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    private List<SpendProfile> getSpendProfilesAndSetWhenSpendProfileRepositoryMock(Long projectId) {
        List<SpendProfile> spendProfileList = newSpendProfile().withApproval(ApprovalType.UNSET, ApprovalType.REJECTED).build(2);
        when(spendProfileRepositoryMock.findByProjectId(projectId)).thenReturn(spendProfileList);

        return spendProfileList;
    }

    private void setupGenerateSpendProfilesExpectations(GenerateSpendProfileData generateSpendProfileData, Project project, Organisation organisation1, Organisation organisation2) {
        CostCategoryType costCategoryType1 = generateSpendProfileData.getCostCategoryType1();
        CostCategoryType costCategoryType2 = generateSpendProfileData.getCostCategoryType2();
        CostCategory type1Cat1 = generateSpendProfileData.type1Cat1;
        CostCategory type1Cat2 = generateSpendProfileData.type1Cat2;
        CostCategory type2Cat1 = generateSpendProfileData.type2Cat1;

        when(financeCheckProcessRepository.findOneByTargetId(project.getPartnerOrganisations().get(0).getId())).thenReturn(
                new FinanceCheckProcess((User) null, null, new ActivityState(null, State.ACCEPTED)));
        when(financeCheckProcessRepository.findOneByTargetId(project.getPartnerOrganisations().get(1).getId())).thenReturn(
                new FinanceCheckProcess((User) null, null, new ActivityState(null, State.ACCEPTED)));

        // setup expectations for getting project users to infer the partner organisations
        List<ProjectUserResource> projectUsers =
                newProjectUserResource().withOrganisation(organisation1.getId(), organisation2.getId()).build(2);
        when(projectServiceMock.getProjectUsers(projectId)).thenReturn(serviceSuccess(projectUsers));

        // setup expectations for finding finance figures per Cost Category from which to generate the spend profile
        when(spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(project.getId(), organisation1.getId())).thenReturn(serviceSuccess(
                new SpendProfileCostCategorySummaries(
                        asList(
                                new SpendProfileCostCategorySummary(type1Cat1, new BigDecimal("100.00"), project.getDurationInMonths()),
                                new SpendProfileCostCategorySummary(type1Cat2, new BigDecimal("200.00"), project.getDurationInMonths())),
                        costCategoryType1)));

        when(spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(project.getId(), organisation2.getId())).thenReturn(serviceSuccess(
                new SpendProfileCostCategorySummaries(
                        singletonList(new SpendProfileCostCategorySummary(type2Cat1, new BigDecimal("300.66"), project.getDurationInMonths())),
                        costCategoryType2)));
    }

    @Override
    protected ProjectFinanceServiceImpl supplyServiceUnderTest() {
        return new ProjectFinanceServiceImpl();
    }

    private class GenerateSpendProfileData {

        private Project project;
        private Organisation organisation1;
        private Organisation organisation2;
        private CostCategoryType costCategoryType1;
        private CostCategoryType costCategoryType2;
        private CostCategory type1Cat1;
        private CostCategory type1Cat2;
        private CostCategory type2Cat1;


        public Project getProject() {
            return project;
        }

        public Organisation getOrganisation1() {
            return organisation1;
        }

        public Organisation getOrganisation2() {
            return organisation2;
        }

        public CostCategoryType getCostCategoryType1() {
            return costCategoryType1;
        }

        public CostCategoryType getCostCategoryType2() {
            return costCategoryType2;
        }

        public GenerateSpendProfileData build() {

            project = newProject().
                    withId(projectId).
                    withDuration(3L).
                    withPartnerOrganisations(newPartnerOrganisation().build(2)).
                    build();

            UserResource loggedInUser = newUserResource().build();
            User user = newUser().withId(loggedInUser.getId()).build();
            setLoggedInUser(loggedInUser);
            when(userRepositoryMock.findOne(loggedInUser.getId())).thenReturn(user);

            organisation1 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
            organisation2 = newOrganisation().withOrganisationType(OrganisationTypeEnum.CATAPULT).build();

            // First cost category type and everything that goes with it.
            type1Cat1 = newCostCategory().withName(LABOUR.getName()).build();
            type1Cat2 = newCostCategory().withName(MATERIALS.getName()).build();

            costCategoryType1 = newCostCategoryType()
                    .withName("Type 1")
                    .withCostCategoryGroup(
                            newCostCategoryGroup()
                                    .withDescription("Group 1")
                                    .withCostCategories(asList(type1Cat1, type1Cat2))
                                    .build())
                    .build();

            // Second cost category type and everything that goes with it.
            type2Cat1 = newCostCategory().withName(ACADEMIC.getName()).build();

            costCategoryType2 = newCostCategoryType()
                    .withName("Type 2")
                    .withCostCategoryGroup(
                            newCostCategoryGroup()
                                    .withDescription("Group 2")
                                    .withCostCategories(asList(type2Cat1))
                                    .build())
                    .build();

            // set basic repository lookup expectations
            when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
            when(organisationRepositoryMock.findOne(organisation1.getId())).thenReturn(organisation1);
            when(organisationRepositoryMock.findOne(organisation2.getId())).thenReturn(organisation2);
            when(costCategoryRepositoryMock.findOne(type1Cat1.getId())).thenReturn(type1Cat1);
            when(costCategoryRepositoryMock.findOne(type1Cat2.getId())).thenReturn(type1Cat2);
            when(costCategoryRepositoryMock.findOne(type2Cat1.getId())).thenReturn(type2Cat1);
            when(costCategoryTypeRepositoryMock.findOne(costCategoryType1.getId())).thenReturn(costCategoryType1);
            when(costCategoryTypeRepositoryMock.findOne(costCategoryType2.getId())).thenReturn(costCategoryType2);
            return this;
        }
    }
}
