package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.transactional.PartnerOrganisationService;
import org.innovateuk.ifs.project.core.util.ProjectUsersHelper;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.TimeUnit;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.domain.*;
import org.innovateuk.ifs.project.financechecks.repository.CostCategoryRepository;
import org.innovateuk.ifs.project.financechecks.repository.CostCategoryTypeRepository;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.spendprofile.configuration.workflow.SpendProfileWorkflowHandler;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfileNotifications;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileCSVResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.innovateuk.ifs.project.spendprofile.validator.SpendProfileValidationUtil;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.finance.resource.TimeUnit.MONTH;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryGroupBuilder.newCostCategoryGroup;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryTypeBuilder.newCostCategoryType;
import static org.innovateuk.ifs.project.spendprofile.builder.SpendProfileBuilder.newSpendProfile;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SpendProfileServiceImplTest extends BaseServiceUnitTest<SpendProfileServiceImpl> {

    private static final String webBaseUrl = "https://ifs-local-dev/dashboard";
    private Long projectId = 123L;
    private Long organisationId = 456L;
    @Mock
    private SpendProfileCostCategorySummaryStrategy spendProfileCostCategorySummaryStrategy;
    @Mock
    private NotificationService notificationServiceMock;
    @Mock
    private SpendProfileValidationUtil validationUtil;
    @Mock
    private Error mockedError;
    @Mock
    private ViabilityWorkflowHandler viabilityWorkflowHandler;
    @Mock
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;
    @Mock
    private SpendProfileWorkflowHandler spendProfileWorkflowHandler;
    @Mock
    private SpendProfileRepository spendProfileRepository;
    @Mock
    private ProjectUsersHelper projectUsersHelper;
    @Mock
    private PartnerOrganisationService partnerOrganisationService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private OrganisationRepository organisationRepository;
    @Mock
    private CostCategoryRepository costCategoryRepository;
    @Mock
    private CostCategoryTypeRepository costCategoryTypeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SystemNotificationSource systemNotificationSource;


    @Test
    public void generateSpendProfile() {

        GenerateSpendProfileData generateSpendProfileData = new GenerateSpendProfileData().build();

        Project project = generateSpendProfileData.getProject();
        Organisation organisation1 = generateSpendProfileData.getOrganisation1();
        Organisation organisation2 = generateSpendProfileData.getOrganisation2();
        PartnerOrganisation partnerOrganisation1 = project.getPartnerOrganisations().get(0);
        PartnerOrganisation partnerOrganisation2 = project.getPartnerOrganisations().get(1);
        CostCategoryType costCategoryType1 = generateSpendProfileData.getCostCategoryType1();
        CostCategoryType costCategoryType2 = generateSpendProfileData.getCostCategoryType2();
        CostCategory type1Cat1 = generateSpendProfileData.type1Cat1;
        CostCategory type1Cat2 = generateSpendProfileData.type1Cat2;
        CostCategory type2Cat1 = generateSpendProfileData.type2Cat1;

        setupGenerateSpendProfilesExpectations(generateSpendProfileData, project, organisation1, organisation2);

        when(viabilityWorkflowHandler.getState(partnerOrganisation1)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandler.getState(partnerOrganisation2)).thenReturn(ViabilityState.APPROVED);
        when(eligibilityWorkflowHandler.getState(partnerOrganisation1)).thenReturn(EligibilityState.APPROVED);
        when(eligibilityWorkflowHandler.getState(partnerOrganisation2)).thenReturn(EligibilityState.APPROVED);
        when(spendProfileWorkflowHandler.isAlreadyGenerated(project)).thenReturn(false);
        when(spendProfileWorkflowHandler.spendProfileGenerated(eq(project), any())).thenReturn(true);

        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(),
                organisation1.getId())).thenReturn(Optional.empty());
        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(),
                organisation2.getId())).thenReturn(Optional.empty());

        User generatedBy = generateSpendProfileData.getUser();

        List<Cost> expectedOrganisation1EligibleCosts = asList(
                new Cost("100").withCategory(type1Cat1),
                new Cost("200").withCategory(type1Cat2));

        List<Cost> expectedOrganisation1SpendProfileFigures = asList(
                new Cost("34").withCategory(type1Cat1).withTimePeriod(0, MONTH, 1, MONTH),
                new Cost("33").withCategory(type1Cat1).withTimePeriod(1, MONTH, 1, MONTH),
                new Cost("33").withCategory(type1Cat1).withTimePeriod(2, MONTH, 1, MONTH),
                new Cost("68").withCategory(type1Cat2).withTimePeriod(0, MONTH, 1, MONTH),
                new Cost("66").withCategory(type1Cat2).withTimePeriod(1, MONTH, 1, MONTH),
                new Cost("66").withCategory(type1Cat2).withTimePeriod(2, MONTH, 1, MONTH));

        Calendar generatedDate = Calendar.getInstance();

        SpendProfile expectedOrganisation1Profile = new SpendProfile(organisation1, project, costCategoryType1,
                expectedOrganisation1EligibleCosts, expectedOrganisation1SpendProfileFigures, generatedBy, generatedDate, false);

        List<Cost> expectedOrganisation2EligibleCosts = singletonList(
                new Cost("301").withCategory(type2Cat1));

        List<Cost> expectedOrganisation2SpendProfileFigures = asList(
                new Cost("101").withCategory(type2Cat1).withTimePeriod(0, MONTH, 1, MONTH),
                new Cost("100").withCategory(type2Cat1).withTimePeriod(1, MONTH, 1, MONTH),
                new Cost("100").withCategory(type2Cat1).withTimePeriod(2, MONTH, 1, MONTH));

        SpendProfile expectedOrganisation2Profile = new SpendProfile(organisation2, project, costCategoryType2,
                expectedOrganisation2EligibleCosts, expectedOrganisation2SpendProfileFigures, generatedBy, generatedDate, false);

        when(spendProfileRepository.save(spendProfileExpectations(expectedOrganisation1Profile))).thenReturn(null);
        when(spendProfileRepository.save(spendProfileExpectations(expectedOrganisation2Profile))).thenReturn(null);

        User financeContactUser1 = newUser().withEmailAddress("z@abc.com").withFirstName("A").withLastName("Z").build();
        ProjectUser financeContact1 = newProjectUser().withUser(financeContactUser1).build();
        User financeContactUser2 = newUser().withEmailAddress("a@abc.com").withFirstName("A").withLastName("A").build();
        ProjectUser financeContact2 = newProjectUser().withUser(financeContactUser2).build();
        when(projectUsersHelper.getFinanceContact(project.getId(), organisation1.getId())).thenReturn(Optional.of(financeContact1));
        when(projectUsersHelper.getFinanceContact(project.getId(), organisation2.getId())).thenReturn(Optional.of(financeContact2));

        Map<String, Object> expectedNotificationArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/dashboard",
                "applicationId", project.getApplication().getId(),
                "competitionName", "Competition 1"
        );

        NotificationTarget to1 = new UserNotificationTarget("A Z", "z@abc.com");
        NotificationTarget to2 = new UserNotificationTarget("A A", "a@abc.com");

        Notification notification1 = new Notification(systemNotificationSource, to1, SpendProfileNotifications.FINANCE_CONTACT_SPEND_PROFILE_AVAILABLE, expectedNotificationArguments);
        Notification notification2 = new Notification(systemNotificationSource, to2, SpendProfileNotifications.FINANCE_CONTACT_SPEND_PROFILE_AVAILABLE, expectedNotificationArguments);

        when(notificationServiceMock.sendNotificationWithFlush(notification1, EMAIL)).thenReturn(serviceSuccess());
        when(notificationServiceMock.sendNotificationWithFlush(notification2, EMAIL)).thenReturn(serviceSuccess());

        ServiceResult<Void> generateResult = service.generateSpendProfile(projectId);
        assertTrue(generateResult.isSuccess());

        verify(spendProfileRepository).save(spendProfileExpectations(expectedOrganisation1Profile));
        verify(spendProfileRepository).save(spendProfileExpectations(expectedOrganisation2Profile));

        verify(notificationServiceMock).sendNotificationWithFlush(notification1, EMAIL);
        verify(notificationServiceMock).sendNotificationWithFlush(notification2, EMAIL);
    }

    @Test
    public void generateSpendProfileButNotAllViabilityApproved() {

        GenerateSpendProfileData generateSpendProfileData = new GenerateSpendProfileData().build();

        Project project = generateSpendProfileData.getProject();
        Organisation organisation1 = generateSpendProfileData.getOrganisation1();
        Organisation organisation2 = generateSpendProfileData.getOrganisation2();
        PartnerOrganisation partnerOrganisation1 = project.getPartnerOrganisations().get(0);
        PartnerOrganisation partnerOrganisation2 = project.getPartnerOrganisations().get(1);

        setupGenerateSpendProfilesExpectations(generateSpendProfileData, project, organisation1, organisation2);

        when(viabilityWorkflowHandler.getState(partnerOrganisation1)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandler.getState(partnerOrganisation2)).thenReturn(ViabilityState.REVIEW);

        ServiceResult<Void> generateResult = service.generateSpendProfile(projectId);
        assertTrue(generateResult.isFailure());
        assertTrue(generateResult.getFailure().is(SPEND_PROFILE_CANNOT_BE_GENERATED_UNTIL_ALL_VIABILITY_APPROVED));

        verify(spendProfileRepository, never()).save(isA(SpendProfile.class));
        verifyNoMoreInteractions(spendProfileRepository);
    }

    @Test
    public void generateSpendProfileWhenNotAllEligibilityApproved() {

        GenerateSpendProfileData generateSpendProfileData = new GenerateSpendProfileData().build();

        Project project = generateSpendProfileData.getProject();
        Organisation organisation1 = generateSpendProfileData.getOrganisation1();
        Organisation organisation2 = generateSpendProfileData.getOrganisation2();
        PartnerOrganisation partnerOrganisation1 = project.getPartnerOrganisations().get(0);
        PartnerOrganisation partnerOrganisation2 = project.getPartnerOrganisations().get(1);

        setupGenerateSpendProfilesExpectations(generateSpendProfileData, project, organisation1, organisation2);

        when(viabilityWorkflowHandler.getState(partnerOrganisation1)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandler.getState(partnerOrganisation2)).thenReturn(ViabilityState.APPROVED);
        when(eligibilityWorkflowHandler.getState(partnerOrganisation1)).thenReturn(EligibilityState.APPROVED);
        when(eligibilityWorkflowHandler.getState(partnerOrganisation2)).thenReturn(EligibilityState.REVIEW);

        ServiceResult<Void> generateResult = service.generateSpendProfile(projectId);
        assertTrue(generateResult.isFailure());
        assertTrue(generateResult.getFailure().is(SPEND_PROFILE_CANNOT_BE_GENERATED_UNTIL_ALL_ELIGIBILITY_APPROVED));

        verify(spendProfileRepository, never()).save(isA(SpendProfile.class));
        verifyNoMoreInteractions(spendProfileRepository);
    }

    @Test
    public void generateSpendProfileWhenSpendProfileAlreadyGenerated() {

        GenerateSpendProfileData generateSpendProfileData = new GenerateSpendProfileData().build();

        Project project = generateSpendProfileData.getProject();
        Organisation organisation1 = generateSpendProfileData.getOrganisation1();
        Organisation organisation2 = generateSpendProfileData.getOrganisation2();
        PartnerOrganisation partnerOrganisation1 = project.getPartnerOrganisations().get(0);
        PartnerOrganisation partnerOrganisation2 = project.getPartnerOrganisations().get(1);

        setupGenerateSpendProfilesExpectations(generateSpendProfileData, project, organisation1, organisation2);

        when(viabilityWorkflowHandler.getState(partnerOrganisation1)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandler.getState(partnerOrganisation2)).thenReturn(ViabilityState.APPROVED);
        when(eligibilityWorkflowHandler.getState(partnerOrganisation1)).thenReturn(EligibilityState.APPROVED);
        when(eligibilityWorkflowHandler.getState(partnerOrganisation2)).thenReturn(EligibilityState.APPROVED);
        when(spendProfileWorkflowHandler.isAlreadyGenerated(project)).thenReturn(true);

        SpendProfile spendProfileForOrganisation1 = new SpendProfile();
        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(),
                organisation1.getId())).thenReturn(Optional.of(spendProfileForOrganisation1));
        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(),
                organisation2.getId())).thenReturn(Optional.empty());

        ServiceResult<Void> generateResult = service.generateSpendProfile(projectId);
        assertTrue(generateResult.isFailure());
        assertTrue(generateResult.getFailure().is(SPEND_PROFILE_HAS_ALREADY_BEEN_GENERATED));

        verify(spendProfileRepository, never()).save(isA(SpendProfile.class));
    }

    @Test
    public void generateSpendProfileWhenSendingEmailFails() {

        GenerateSpendProfileData generateSpendProfileData = new GenerateSpendProfileData().build();
        Project project = generateSpendProfileData.getProject();
        Organisation organisation1 = generateSpendProfileData.getOrganisation1();
        Organisation organisation2 = generateSpendProfileData.getOrganisation2();
        PartnerOrganisation partnerOrganisation1 = project.getPartnerOrganisations().get(0);
        PartnerOrganisation partnerOrganisation2 = project.getPartnerOrganisations().get(1);

        setupGenerateSpendProfilesExpectations(generateSpendProfileData, project, organisation1, organisation2);

        when(viabilityWorkflowHandler.getState(partnerOrganisation1)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandler.getState(partnerOrganisation2)).thenReturn(ViabilityState.NOT_APPLICABLE);
        when(eligibilityWorkflowHandler.getState(partnerOrganisation1)).thenReturn(EligibilityState.APPROVED);
        when(eligibilityWorkflowHandler.getState(partnerOrganisation2)).thenReturn(EligibilityState.APPROVED);
        when(spendProfileWorkflowHandler.isAlreadyGenerated(project)).thenReturn(false);
        when(spendProfileWorkflowHandler.spendProfileGenerated(eq(project), any())).thenReturn(true);

        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(),
                organisation1.getId())).thenReturn(Optional.empty());
        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(),
                organisation2.getId())).thenReturn(Optional.empty());


        User financeContactUser1 = newUser().withEmailAddress("z@abc.com").withFirstName("A").withLastName("Z").build();
        ProjectUser financeContact1 = newProjectUser().withUser(financeContactUser1).build();
        User financeContactUser2 = newUser().withEmailAddress("a@abc.com").withFirstName("A").withLastName("A").build();
        ProjectUser financeContact2 = newProjectUser().withUser(financeContactUser2).build();
        when(projectUsersHelper.getFinanceContact(project.getId(), organisation1.getId())).thenReturn(Optional.of(financeContact1));
        when(projectUsersHelper.getFinanceContact(project.getId(), organisation2.getId())).thenReturn(Optional.of(financeContact2));

        Map<String, Object> expectedNotificationArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/dashboard",
                "competitionName", "Competition 1",
                "applicationId", project.getApplication().getId()
        );

        NotificationTarget to1 = new UserNotificationTarget("A Z", "z@abc.com");
        NotificationTarget to2 = new UserNotificationTarget("A A", "a@abc.com");

        Notification notification1 = new Notification(systemNotificationSource, to1, SpendProfileNotifications.FINANCE_CONTACT_SPEND_PROFILE_AVAILABLE, expectedNotificationArguments);
        Notification notification2 = new Notification(systemNotificationSource, to2, SpendProfileNotifications.FINANCE_CONTACT_SPEND_PROFILE_AVAILABLE, expectedNotificationArguments);

        when(notificationServiceMock.sendNotificationWithFlush(notification1, EMAIL)).thenReturn(serviceSuccess());
        when(notificationServiceMock.sendNotificationWithFlush(notification2, EMAIL)).thenReturn(serviceFailure(CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_SINGLE));

        ServiceResult<Void> generateResult = service.generateSpendProfile(projectId);
        assertTrue(generateResult.isFailure());
        assertTrue(generateResult.getFailure().is(CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_SINGLE));

        verify(spendProfileRepository, times(2)).save(isA(SpendProfile.class));

        verify(notificationServiceMock).sendNotificationWithFlush(notification1, EMAIL);
        verify(notificationServiceMock).sendNotificationWithFlush(notification2, EMAIL);
    }

    @Test
    public void generateSpendProfileSendEmailFailsDueToNoFinanceContact() {

        GenerateSpendProfileData generateSpendProfileData = new GenerateSpendProfileData().build();

        Project project = generateSpendProfileData.getProject();
        Organisation organisation1 = generateSpendProfileData.getOrganisation1();
        Organisation organisation2 = generateSpendProfileData.getOrganisation2();
        PartnerOrganisation partnerOrganisation1 = project.getPartnerOrganisations().get(0);
        PartnerOrganisation partnerOrganisation2 = project.getPartnerOrganisations().get(1);

        setupGenerateSpendProfilesExpectations(generateSpendProfileData, project, organisation1, organisation2);

        when(viabilityWorkflowHandler.getState(partnerOrganisation1)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandler.getState(partnerOrganisation2)).thenReturn(ViabilityState.NOT_APPLICABLE);
        when(eligibilityWorkflowHandler.getState(partnerOrganisation1)).thenReturn(EligibilityState.APPROVED);
        when(eligibilityWorkflowHandler.getState(partnerOrganisation2)).thenReturn(EligibilityState.APPROVED);
        when(spendProfileWorkflowHandler.isAlreadyGenerated(project)).thenReturn(false);
        when(spendProfileWorkflowHandler.spendProfileGenerated(eq(project), any())).thenReturn(true);

        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(),
                organisation1.getId())).thenReturn(Optional.empty());
        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(),
                organisation2.getId())).thenReturn(Optional.empty());

        ProjectUser financeContact2 = newProjectUser().withUser((User[]) null).build();
        when(projectUsersHelper.getFinanceContact(project.getId(), organisation1.getId())).thenReturn(Optional.empty());
        when(projectUsersHelper.getFinanceContact(project.getId(), organisation2.getId())).thenReturn(Optional.of(financeContact2));

        ServiceResult<Void> generateResult = service.generateSpendProfile(projectId);
        assertTrue(generateResult.isFailure());
        assertTrue(generateResult.getFailure().is(CommonFailureKeys.SPEND_PROFILE_FINANCE_CONTACT_NOT_PRESENT, CommonFailureKeys.SPEND_PROFILE_FINANCE_CONTACT_NOT_PRESENT));

        verify(spendProfileRepository, times(2)).save(isA(SpendProfile.class));
    }

    @Test
    public void generateSpendProfileNotReadyToGenerate() {

        GenerateSpendProfileData generateSpendProfileData = new GenerateSpendProfileData().build();

        Project project = generateSpendProfileData.getProject();
        Organisation organisation1 = generateSpendProfileData.getOrganisation1();
        Organisation organisation2 = generateSpendProfileData.getOrganisation2();
        PartnerOrganisation partnerOrganisation1 = project.getPartnerOrganisations().get(0);
        PartnerOrganisation partnerOrganisation2 = project.getPartnerOrganisations().get(1);

        setupGenerateSpendProfilesExpectations(generateSpendProfileData, project, organisation1, organisation2);

        when(viabilityWorkflowHandler.getState(partnerOrganisation1)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandler.getState(partnerOrganisation2)).thenReturn(ViabilityState.NOT_APPLICABLE);
        when(eligibilityWorkflowHandler.getState(partnerOrganisation1)).thenReturn(EligibilityState.APPROVED);
        when(eligibilityWorkflowHandler.getState(partnerOrganisation2)).thenReturn(EligibilityState.APPROVED);
        when(spendProfileWorkflowHandler.isAlreadyGenerated(project)).thenReturn(true);

        ServiceResult<Void> generateResult = service.generateSpendProfile(projectId);
        assertTrue(generateResult.isFailure());
        assertTrue(generateResult.getFailure().is(CommonFailureKeys.SPEND_PROFILE_HAS_ALREADY_BEEN_GENERATED));
    }

    private void setupGenerateSpendProfilesExpectations(GenerateSpendProfileData generateSpendProfileData, Project project, Organisation organisation1, Organisation organisation2) {
        CostCategoryType costCategoryType1 = generateSpendProfileData.getCostCategoryType1();
        CostCategoryType costCategoryType2 = generateSpendProfileData.getCostCategoryType2();
        CostCategory type1Cat1 = generateSpendProfileData.type1Cat1;
        CostCategory type1Cat2 = generateSpendProfileData.type1Cat2;
        CostCategory type2Cat1 = generateSpendProfileData.type2Cat1;

        List<PartnerOrganisationResource> partnerOrganisationResources =
                newPartnerOrganisationResource().withOrganisation(organisation1.getId(), organisation2.getId()).build(2);
        when(partnerOrganisationService.getProjectPartnerOrganisations(projectId)).thenReturn(serviceSuccess(partnerOrganisationResources));

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

    @Test
    public void generateSpendProfileForPartnerOrganisation() {

        Competition competition = newCompetition()
                .withName("Competition 1")
                .build();

        Application application = newApplication()
                .withName("Application 1")
                .withCompetition(competition)
                .build();

        Project project = newProject()
                .withId(projectId)
                .withDuration(3L)
                .withPartnerOrganisations(newPartnerOrganisation()
                .build(2))
                .withApplication(application)
                .build();

        Organisation organisation1 = newOrganisation().build();

        CostCategory costCategoryLabour = newCostCategory().withName(LABOUR.getName()).build();
        CostCategory costCategoryMaterials = newCostCategory().withName(MATERIALS.getName()).build();

        CostCategoryType costCategoryType1 =
                newCostCategoryType()
                        .withName("Type 1")
                        .withCostCategoryGroup(
                                newCostCategoryGroup()
                                        .withDescription("Group 1")
                                        .withCostCategories(asList(costCategoryLabour, costCategoryMaterials))
                                        .build())
                        .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(organisationRepository.findById(organisation1.getId())).thenReturn(Optional.of(organisation1));
        when(costCategoryRepository.findById(costCategoryLabour.getId())).thenReturn(Optional.of(costCategoryLabour));
        when(costCategoryRepository.findById(costCategoryMaterials.getId())).thenReturn(Optional.of(costCategoryMaterials));
        when(costCategoryTypeRepository.findById(costCategoryType1.getId())).thenReturn(Optional.of(costCategoryType1));
        when(spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(project.getId(), organisation1.getId())).thenReturn(serviceSuccess(
                new SpendProfileCostCategorySummaries(
                        asList(
                                new SpendProfileCostCategorySummary(costCategoryLabour, new BigDecimal("100.00"), project.getDurationInMonths()),
                                new SpendProfileCostCategorySummary(costCategoryMaterials, new BigDecimal("200.00"), project.getDurationInMonths())),
                        costCategoryType1)));

        List<Cost> expectedOrganisation1EligibleCosts = asList(
                new Cost("100").withCategory(costCategoryLabour),
                new Cost("200").withCategory(costCategoryMaterials));

        List<Cost> expectedOrganisation1SpendProfileFigures = asList(
                new Cost("34").withCategory(costCategoryLabour).withTimePeriod(0, MONTH, 1, MONTH),
                new Cost("33").withCategory(costCategoryLabour).withTimePeriod(1, MONTH, 1, MONTH),
                new Cost("33").withCategory(costCategoryLabour).withTimePeriod(2, MONTH, 1, MONTH),
                new Cost("68").withCategory(costCategoryMaterials).withTimePeriod(0, MONTH, 1, MONTH),
                new Cost("66").withCategory(costCategoryMaterials).withTimePeriod(1, MONTH, 1, MONTH),
                new Cost("66").withCategory(costCategoryMaterials).withTimePeriod(2, MONTH, 1, MONTH));

        Long userId = 7L;
        User generatedBy = newUser().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(generatedBy));

        Calendar generatedDate = Calendar.getInstance();

        SpendProfile expectedOrganisation1Profile = new SpendProfile(organisation1, project, costCategoryType1,
                expectedOrganisation1EligibleCosts, expectedOrganisation1SpendProfileFigures, generatedBy, generatedDate, false);

        when(spendProfileRepository.save(spendProfileExpectations(expectedOrganisation1Profile))).thenReturn(null);


        User financeContactUser1 = newUser().withEmailAddress("z@abc.com").withFirstName("A").withLastName("Z").build();
        ProjectUser financeContact1 = newProjectUser().withUser(financeContactUser1).build();
        when(projectUsersHelper.getFinanceContact(project.getId(), organisation1.getId())).thenReturn(Optional.of(financeContact1));

        Map<String, Object> expectedNotificationArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/dashboard",
                "applicationId", project.getApplication().getId(),
                "competitionName", "Competition 1"
        );

        NotificationTarget to1 = new UserNotificationTarget("A Z", "z@abc.com");

        Notification notification1 = new Notification(systemNotificationSource, to1, SpendProfileNotifications.FINANCE_CONTACT_SPEND_PROFILE_AVAILABLE, expectedNotificationArguments);

        when(notificationServiceMock.sendNotificationWithFlush(notification1, EMAIL)).thenReturn(serviceSuccess());

        ServiceResult<Void> generateResult = service.generateSpendProfileForPartnerOrganisation(projectId, organisation1.getId(), userId);
        assertTrue(generateResult.isSuccess());

        verify(spendProfileRepository).save(spendProfileExpectations(expectedOrganisation1Profile));
        verify(notificationServiceMock).sendNotificationWithFlush(notification1, EMAIL);
        verifyNoMoreInteractions(spendProfileRepository);
    }

    @Test
    public void generateSpendProfileCSV() {
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
        testCostCategory.setName("Category One");
        testCostCategory.setLabel("Group Name");

        OrganisationType organisationType = newOrganisationType().withOrganisationType(BUSINESS).build();
        Organisation organisation1 = newOrganisation().withId(organisationId).withOrganisationType(organisationType).withName("TEST").build();
        when(organisationRepository.findById(organisation1.getId())).thenReturn(Optional.of(organisation1));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(spendProfileInDB));
        when(costCategoryRepository.findById(anyLong())).thenReturn(Optional.of(testCostCategory));
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ServiceResult<SpendProfileCSVResource> serviceResult = service.getSpendProfileCSV(projectOrganisationCompositeId);

        assertTrue(serviceResult.getSuccess().getFileName().startsWith("TEST_Spend_Profile_" + dateFormat.format(date)));
        assertTrue(serviceResult.getSuccess().getCsvData().contains("Category One"));
        assertEquals(6, Arrays.stream(serviceResult.getSuccess().getCsvData().split("\n")).filter(s -> s.contains("Category One")
                && !s.contains("Month") && !s.contains("TOTAL")).count());
    }

    @Test
    public void generateSpendProfileCSVWithCategoryGroupLabelEmpty() {
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

        OrganisationType organisationType = newOrganisationType().withOrganisationType(BUSINESS).build();
        Organisation organisation1 = newOrganisation().withId(organisationId).withOrganisationType(organisationType).withName("TEST").build();
        when(organisationRepository.findById(organisation1.getId())).thenReturn(Optional.of(organisation1));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(spendProfileInDB));
        when(costCategoryRepository.findById(anyLong())).thenReturn(Optional.of(testCostCategory));
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ServiceResult<SpendProfileCSVResource> serviceResult = service.getSpendProfileCSV(projectOrganisationCompositeId);

        assertTrue(serviceResult.getSuccess().getFileName().startsWith("TEST_Spend_Profile_" + dateFormat.format(date)));
        assertFalse(serviceResult.getSuccess().getCsvData().contains("Group Name"));
        assertEquals(Arrays.asList(serviceResult.getSuccess().getCsvData().split("\n")).stream().filter(s -> s.contains("Group Name")
                && !s.contains("Month") && !s.contains("TOTAL")).count(), 0);
    }

    @Test
    public void getSpendProfileStatusByProjectIdApproved() {
        Project project = newProject().build();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(spendProfileWorkflowHandler.getApproval(project)).thenReturn(ApprovalType.APPROVED);

        ServiceResult<ApprovalType> result = service.getSpendProfileStatusByProjectId(projectId);
        assertTrue(result.isSuccess());
        assertEquals(ApprovalType.APPROVED, result.getSuccess());
    }

    @Test
    public void getSpendProfileStatusByProjectIdRejected() {
        Project project = newProject().build();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(spendProfileWorkflowHandler.getApproval(project)).thenReturn(ApprovalType.REJECTED);

        ServiceResult<ApprovalType> result = service.getSpendProfileStatusByProjectId(projectId);
        assertTrue(result.isSuccess());
        assertEquals(ApprovalType.REJECTED, result.getSuccess());
    }

    @Test
    public void getSpendProfileStatusByProjectIdUnset() {
        List<SpendProfile> spendProfileList = newSpendProfile().build(3);
        when(spendProfileRepository.findByProjectId(projectId)).thenReturn(spendProfileList);

        ServiceResult<ApprovalType> result = service.getSpendProfileStatusByProjectId(projectId);
        assertTrue(result.isSuccess());
        assertEquals(ApprovalType.UNSET, result.getSuccess());
    }

    @Test
    public void approveSpendProfile() {
        List<SpendProfile> spendProfileList = getSpendProfilesAndSetWhenSpendProfileRepositoryMock(projectId);
        Project project = newProject().withId(projectId).withDuration(3L).withTargetStartDate(LocalDate.of(2018, 3, 1)).withSpendProfileSubmittedDate(ZonedDateTime.now()).build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(spendProfileWorkflowHandler.isReadyToApprove(project)).thenReturn(true);

        Long userId = 1234L;
        User user = newUser().withId(userId).build();
        UserResource loggedInUser = newUserResource().withId(user.getId()).build();
        setLoggedInUser(loggedInUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(spendProfileWorkflowHandler.spendProfileApproved(project, user)).thenReturn(true);

        ServiceResult<Void> result = service.approveOrRejectSpendProfile(projectId, ApprovalType.APPROVED);

        assertTrue(result.isSuccess());
;
        verify(spendProfileRepository).saveAll(spendProfileList);
        verify(spendProfileWorkflowHandler).spendProfileApproved(project, user);
    }

    @Test
    public void rejectSpendProfile() {
        Long projectId = 4234L;
        List<SpendProfile> spendProfileList = getSpendProfilesAndSetWhenSpendProfileRepositoryMock(projectId);
        Project project = newProject().withId(projectId).withDuration(3L).withTargetStartDate(LocalDate.of(2018, 3, 1)).withSpendProfileSubmittedDate(ZonedDateTime.now()).build();

        when(spendProfileWorkflowHandler.isReadyToApprove(project)).thenReturn(true);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Long userId = 1234L;
        UserResource loggedInUser = newUserResource().withId(userId).build();
        User user = newUser().withId(loggedInUser.getId()).build();
        setLoggedInUser(loggedInUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(spendProfileWorkflowHandler.spendProfileRejected(project, user)).thenReturn(true);

        ServiceResult<Void> resultNew = service.approveOrRejectSpendProfile(projectId, ApprovalType.REJECTED);

        assertTrue(resultNew.isSuccess());
        assertTrue(project.getSpendProfileSubmittedDate() == null);

        verify(spendProfileRepository).saveAll(spendProfileList);
        verify(spendProfileWorkflowHandler).spendProfileRejected(project, user);
    }

    @Test
    public void approveSpendProfileProcessNotApproved() {
        List<SpendProfile> spendProfileList = getSpendProfilesAndSetWhenSpendProfileRepositoryMock(projectId);
        Project project = newProject().withId(projectId).withDuration(3L).withTargetStartDate(LocalDate.of(2018, 3, 1)).withSpendProfileSubmittedDate(ZonedDateTime.now()).build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(spendProfileWorkflowHandler.isReadyToApprove(project)).thenReturn(true);
        Long userId = 1234L;
        User user = newUser().withId(userId).build();
        UserResource loggedInUser = newUserResource().withId(user.getId()).build();
        setLoggedInUser(loggedInUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(spendProfileWorkflowHandler.spendProfileApproved(project, user)).thenReturn(false);

        ServiceResult<Void> result = service.approveOrRejectSpendProfile(projectId, ApprovalType.APPROVED);

        assertFalse(result.isSuccess());
        assertEquals(SPEND_PROFILE_CANNOT_BE_APPROVED.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());

        verify(spendProfileRepository).saveAll(spendProfileList);
        verify(spendProfileWorkflowHandler).spendProfileApproved(project,user);
    }

    @Test
    public void rejectSpendProfileNotReadyToApprove() {
        Project project = newProject().withId(projectId).withDuration(3L).withTargetStartDate(LocalDate.of(2018, 3, 1)).withSpendProfileSubmittedDate(ZonedDateTime.now()).build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(spendProfileWorkflowHandler.isReadyToApprove(project)).thenReturn(false);

        ServiceResult<Void> result = service.approveOrRejectSpendProfile(projectId, ApprovalType.REJECTED);

        assertFalse(result.isSuccess());
        assertEquals(SPEND_PROFILE_NOT_READY_TO_APPROVE.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void approveSpendProfileInvalidApprovalType() {
        Project project = newProject().withId(projectId).withDuration(3L).withTargetStartDate(LocalDate.of(2018, 3, 1)).withSpendProfileSubmittedDate(ZonedDateTime.now()).build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(spendProfileWorkflowHandler.isReadyToApprove(project)).thenReturn(true);

        ServiceResult<Void> result = service.approveOrRejectSpendProfile(projectId, ApprovalType.UNSET);

        assertFalse(result.isSuccess());
        assertEquals(SPEND_PROFILE_NOT_READY_TO_APPROVE.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void approveSpendProfileNotReadyToApprove() {
        Project project = newProject().withId(projectId).withDuration(3L).withTargetStartDate(LocalDate.of(2018, 3, 1)).withSpendProfileSubmittedDate(ZonedDateTime.now()).build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(spendProfileWorkflowHandler.isReadyToApprove(project)).thenReturn(false);

        ServiceResult<Void> result = service.approveOrRejectSpendProfile(projectId, ApprovalType.APPROVED);

        assertFalse(result.isSuccess());
        assertEquals(SPEND_PROFILE_NOT_READY_TO_APPROVE.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void rejectSpendProfileFails() {
        Long projectId = 4234L;
        Project project = newProject().withId(projectId).withDuration(3L).withTargetStartDate(LocalDate.of(2018, 3, 1)).withSpendProfileSubmittedDate(ZonedDateTime.now()).build();

        when(spendProfileWorkflowHandler.isReadyToApprove(project)).thenReturn(true);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Long userId = 1234L;
        UserResource loggedInUser = newUserResource().withId(userId).build();
        User user = newUser().withId(loggedInUser.getId()).build();
        setLoggedInUser(loggedInUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(spendProfileWorkflowHandler.spendProfileRejected(project, user)).thenReturn(false);

        ServiceResult<Void> result = service.approveOrRejectSpendProfile(projectId, ApprovalType.REJECTED);

        assertFalse(result.isSuccess());
        assertEquals(SPEND_PROFILE_CANNOT_BE_REJECTED.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void approveSpendProfileNoProject() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        ServiceResult<Void> result = service.approveOrRejectSpendProfile(projectId, ApprovalType.APPROVED);

        assertFalse(result.isSuccess());
        assertEquals(SPEND_PROFILE_NOT_READY_TO_APPROVE.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
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
                173L, new BigDecimal("100"),
                174L, new BigDecimal("180"),
                175L, new BigDecimal("55")));

        table.setMonthlyCostsPerCategoryMap(asMap(
                173L, asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("40")),
                174L, asList(new BigDecimal("70"), new BigDecimal("50"), new BigDecimal("60")),
                175L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0"))));
        List<Cost> spendProfileFigures = buildCostsForCategories(Arrays.asList(173L, 174L, 175L), 3);
        User generatedBy = newUser().build();
        Calendar generatedDate = Calendar.getInstance();

        SpendProfile spendProfileInDB = new SpendProfile(null, newProject().build(), null, Collections.emptyList(), spendProfileFigures, generatedBy, generatedDate, false);

        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(spendProfileInDB));
        when(validationUtil.validateSpendProfileTableResource(eq(table))).thenReturn(Optional.empty());


        // Before the call (ie before the SpendProfile is updated), ensure that the values are set to 1
        assertCostForCategoryForGivenMonth(spendProfileInDB, 173L, 0, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 173L, 1, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 173L, 2, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 174L, 0, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 174L, 1, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 174L, 2, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 175L, 0, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 175L, 1, BigDecimal.ONE);
        assertCostForCategoryForGivenMonth(spendProfileInDB, 175L, 2, BigDecimal.ONE);

        ServiceResult<Void> result = service.saveSpendProfile(projectOrganisationCompositeId, table);

        assertTrue(result.isSuccess());

        // Assert that the SpendProfile domain is correctly updated
        assertCostForCategoryForGivenMonth(spendProfileInDB, 173L, 0, new BigDecimal("30"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 173L, 1, new BigDecimal("30"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 173L, 2, new BigDecimal("40"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 174L, 0, new BigDecimal("70"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 174L, 1, new BigDecimal("50"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 174L, 2, new BigDecimal("60"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 175L, 0, new BigDecimal("50"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 175L, 1, new BigDecimal("5"));
        assertCostForCategoryForGivenMonth(spendProfileInDB, 175L, 2, new BigDecimal("0"));

        verify(spendProfileRepository).save(spendProfileInDB);
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

        OrganisationType organisationType = newOrganisationType().withOrganisationType(BUSINESS).build();
        Organisation organisation1 = newOrganisation().withId(organisationId).withOrganisationType(organisationType).withName("TEST").build();
        when(organisationRepository.findById(organisation1.getId())).thenReturn(Optional.of(organisation1));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectInDB));

        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(spendProfileInDB));

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

        OrganisationType organisationType = newOrganisationType().withOrganisationType(BUSINESS).build();
        Organisation organisation1 = newOrganisation().withId(organisationId).withOrganisationType(organisationType).withName("TEST").build();
        when(organisationRepository.findById(organisation1.getId())).thenReturn(Optional.of(organisation1));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectInDB));

        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(spendProfileInDB));

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


        OrganisationType organisationType = newOrganisationType().withOrganisationType(BUSINESS).build();
        Organisation organisation1 = newOrganisation().withId(organisationId).withOrganisationType(organisationType).withName("TEST").build();
        when(organisationRepository.findById(organisation1.getId())).thenReturn(Optional.of(organisation1));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectInDB));

        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(spendProfileInDB));

        ServiceResult<Void> result = service.markSpendProfileComplete(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void completeSpendProfilesReviewSuccess() {
        Project projectInDb = new Project();
        projectInDb.setSpendProfileSubmittedDate(null);
        SpendProfile spendProfileInDb = new SpendProfile();
        spendProfileInDb.setMarkedAsComplete(true);
        projectInDb.setSpendProfiles(asList(spendProfileInDb));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectInDb));
        assertThat(projectInDb.getSpendProfileSubmittedDate(), nullValue());
        when(spendProfileWorkflowHandler.submit(projectInDb)).thenReturn(true);

        ServiceResult<Void> result = service.completeSpendProfilesReview(projectId);

        assertTrue(result.isSuccess());
        assertThat(projectInDb.getSpendProfileSubmittedDate(), notNullValue());
    }

    @Test
    public void completeSpendProfilesReviewFailureWhenSpendProfileIncomplete() {
        Project projectInDb = new Project();
        projectInDb.setSpendProfileSubmittedDate(null);
        SpendProfile spendProfileInDb = new SpendProfile();
        spendProfileInDb.setMarkedAsComplete(false);
        projectInDb.setSpendProfiles(asList(spendProfileInDb));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectInDb));
        assertThat(projectInDb.getSpendProfileSubmittedDate(), nullValue());

        ServiceResult<Void> result = service.completeSpendProfilesReview(projectId);

        assertTrue(result.isFailure());
    }

    @Test
    public void completeSpendProfilesReviewFailureWhenAlreadySubmitted() {
        Project projectInDb = new Project();
        projectInDb.setSpendProfileSubmittedDate(ZonedDateTime.now());
        SpendProfile spendProfileInDb = new SpendProfile();
        spendProfileInDb.setMarkedAsComplete(true);
        projectInDb.setSpendProfiles(asList(spendProfileInDb));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectInDb));

        ServiceResult<Void> result = service.completeSpendProfilesReview(projectId);

        assertTrue(result.isFailure());
    }

    private SpendProfile createSpendProfile(Project projectInDB, Map<Long, BigDecimal> eligibleCostsMap, Map<Long, List<BigDecimal>> spendProfileCostsMap) {
        CostCategoryType costCategoryType = createCostCategoryType();

        List<Cost> eligibleCosts = buildEligibleCostsForCategories(eligibleCostsMap, costCategoryType.getCostCategoryGroup());

        List<Cost> spendProfileFigures = buildCostsForCategoriesWithGivenValues(spendProfileCostsMap, 3, costCategoryType.getCostCategoryGroup());

        User generatedBy = newUser().build();
        Calendar generatedDate = Calendar.getInstance();

        SpendProfile spendProfileInDB = new SpendProfile(null, projectInDB, costCategoryType, eligibleCosts, spendProfileFigures, generatedBy, generatedDate, true);

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
                .anyMatch(cost -> Objects.equals(cost.getCostCategory().getId(), category)
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
        List<SpendProfile> spendProfileList = newSpendProfile().build(2);
        when(spendProfileRepository.findByProjectId(projectId)).thenReturn(spendProfileList);

        return spendProfileList;
    }

    @Override
    protected SpendProfileServiceImpl supplyServiceUnderTest() {
        SpendProfileServiceImpl spendProfileService = new SpendProfileServiceImpl();
        ReflectionTestUtils.setField(spendProfileService, "webBaseUrl", webBaseUrl);
        return spendProfileService;
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
        private User user;

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

        public User getUser() {
            return user;
        }

        public GenerateSpendProfileData build() {

            UserResource loggedInUser = newUserResource().build();
            user = newUser().withId(loggedInUser.getId()).build();
            setLoggedInUser(loggedInUser);
            when(userRepository.findById(loggedInUser.getId())).thenReturn(Optional.of(user));

            organisation1 = newOrganisation().withOrganisationType(BUSINESS).build();
            organisation2 = newOrganisation().withOrganisationType(OrganisationTypeEnum.RTO).build();

            PartnerOrganisation partnerOrganisation1 = newPartnerOrganisation().withOrganisation(organisation1).build();
            PartnerOrganisation partnerOrganisation2 = newPartnerOrganisation().withOrganisation(organisation2).build();

            Competition competition = newCompetition()
                    .withName("Competition 1")
                    .build();

            Application application = newApplication()
                    .withName("Application 1")
                    .withCompetition(competition)
                    .build();

            project = newProject().
                    withId(projectId).
                    withDuration(3L).
                    withPartnerOrganisations(asList(partnerOrganisation1, partnerOrganisation2)).
                    withApplication(application).
                    build();

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
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
            when(organisationRepository.findById(organisation1.getId())).thenReturn(Optional.of(organisation1));
            when(organisationRepository.findById(organisation2.getId())).thenReturn(Optional.of(organisation2));
            when(costCategoryRepository.findById(type1Cat1.getId())).thenReturn(Optional.of(type1Cat1));
            when(costCategoryRepository.findById(type1Cat2.getId())).thenReturn(Optional.of(type1Cat2));
            when(costCategoryRepository.findById(type2Cat1.getId())).thenReturn(Optional.of(type2Cat1));
            when(costCategoryTypeRepository.findById(costCategoryType1.getId())).thenReturn(Optional.of(costCategoryType1));
            when(costCategoryTypeRepository.findById(costCategoryType2.getId())).thenReturn(Optional.of(costCategoryType2));
            return this;
        }
    }
}
