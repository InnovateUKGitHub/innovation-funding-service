package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.builder.ApplicationBuilder;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationOrganisationAddress;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.repository.ApplicationOrganisationAddressRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.builder.QuestionBuilder;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Collections.*;
import static java.util.Optional.of;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.INTERNATIONAL;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationOrganisationAddressBuilder.newApplicationOrganisationAddress;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.IneligibleOutcomeBuilder.newIneligibleOutcome;
import static org.innovateuk.ifs.application.resource.ApplicationState.CREATED;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.application.resource.CompanyAge.PRE_START_UP;
import static org.innovateuk.ifs.application.resource.CompanyPrimaryFocus.CHEMICALS;
import static org.innovateuk.ifs.application.resource.CompetitionReferralSource.BUSINESS_CONTACT;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.OPEN;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus.FUNDED;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link ApplicationServiceImpl}
 */
public class ApplicationServiceImplTest extends BaseServiceUnitTest<ApplicationService> {

    @Override
    protected ApplicationService supplyServiceUnderTest() {
        return new ApplicationServiceImpl();
    }

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProcessRoleRepository processRoleRepository;

    @Mock
    private ApplicationMapper applicationMapper;

    @Mock
    private CompetitionMapper competitionMapper;

    @Mock
    private ApplicationWorkflowHandler applicationWorkflowHandler;

    @Mock
    private OrganisationAddressRepository organisationAddressRepository;

    @Mock
    private ApplicationOrganisationAddressRepository applicationOrganisationAddressRepository;

    @Mock
    private ApplicationNotificationService applicationNotificationService;

    private Competition competition;

    @Before
    public void setUp() throws Exception {
        Question question = QuestionBuilder.newQuestion().build();

        FormInputType formInputType = FormInputType.FILEUPLOAD;

        FormInput formInput = newFormInput().withType(formInputType).build();
        formInput.setId(123L);
        formInput.setQuestion(question);
        question.setFormInputs(singletonList(formInput));

        final Competition openCompetition = newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build();
        Application openApplication = newApplication().withCompetition(openCompetition).build();

        when(applicationRepository.findById(anyLong())).thenReturn(of(openApplication));

        Question multiAnswerQuestion = newQuestion().withMarksAsCompleteEnabled(Boolean.TRUE).withMultipleStatuses(Boolean.TRUE).withId(123L).build();
        Question leadAnswerQuestion = newQuestion().withMarksAsCompleteEnabled(Boolean.TRUE).withMultipleStatuses(Boolean.FALSE).withId(321L).build();

        OrganisationType orgType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        Organisation organisation1 = newOrganisation().withOrganisationType(orgType).withId(234L).build();
        Organisation organisation2 = newOrganisation().withId(345L).build();
        Organisation organisation3 = newOrganisation().withId(456L).build();

        ProcessRole[] roles = newProcessRole().withRole(ProcessRoleType.LEADAPPLICANT, ProcessRoleType.COLLABORATOR, ProcessRoleType.LEADAPPLICANT).withOrganisationId(234L, 345L, 456L).build(3).toArray(new ProcessRole[0]);
        Section section = newSection().withQuestions(Arrays.asList(multiAnswerQuestion, leadAnswerQuestion)).build();
        competition = newCompetition().withSections(singletonList(section)).withMaxResearchRatio(30).build();
        Application application = newApplication().withCompetition(competition).withProcessRoles(roles).build();

        when(applicationRepository.findById(application.getId())).thenReturn(of(application));
        when(organisationRepository.findById(234L)).thenReturn(of(organisation1));
        when(organisationRepository.findById(345L)).thenReturn(of(organisation2));
        when(organisationRepository.findById(456L)).thenReturn(of(organisation3));
    }

    @Test
    public void createApplicationByApplicationNameForUserIdAndCompetitionId() {

        Competition competition = newCompetition().build();
        User user = newUser().build();
        Long organisationId = 456L;
        Organisation organisation = newOrganisation().with(name("testOrganisation")).withId(organisationId).build();
        ProcessRole processRole = newProcessRole().withUser(user).withRole(ProcessRoleType.LEADAPPLICANT).withOrganisationId(organisation.getId()).build();
        ApplicationState applicationState = CREATED;

        CompetitionReferralSource competitionReferralSource = BUSINESS_CONTACT;
        CompanyAge companyAge = PRE_START_UP;
        CompanyPrimaryFocus companyPrimaryFocus = CHEMICALS;

        Application application = newApplication()
                .withId(1L)
                .withName("testApplication")
                .withApplicationState(applicationState)
                .withDurationInMonths(3L)
                .withCompetition(competition)
                .withCompetitionReferralSource(competitionReferralSource)
                .withCompanyAge(companyAge)
                .withCompetitionPrimaryFocus(companyPrimaryFocus)
                .build();

        ApplicationResource applicationResource = newApplicationResource().build();

        when(competitionRepository.findById(competition.getId())).thenReturn(of(competition));
        when(userRepository.findById(user.getId())).thenReturn(of(user));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        when(processRoleRepository.findByUser(user)).thenReturn(singletonList(processRole));
        when(organisationRepository.findDistinctByProcessRolesUser(user)).thenReturn(singletonList(organisation));
        when(applicationRepository.findById(application.getId())).thenReturn(of(application));

        Supplier<Application> applicationExpectations = () -> argThat(lambdaMatches(created -> {
            assertEquals("testApplication", created.getName());
            assertEquals(applicationState, created.getApplicationProcess().getProcessState());
            assertEquals(Long.valueOf(3), created.getDurationInMonths());
            assertEquals(competition.getId(), created.getCompetition().getId());
            assertNull(created.getStartDate());

            assertEquals(1, created.getProcessRoles().size());
            ProcessRole createdProcessRole = created.getProcessRoles().get(0);
            assertNull(createdProcessRole.getId());
            assertEquals(application.getId().longValue(), createdProcessRole.getApplicationId());
            assertEquals(organisation.getId(), createdProcessRole.getOrganisationId());
            assertEquals(ProcessRoleType.LEADAPPLICANT, createdProcessRole.getRole());
            assertEquals(user.getId(), createdProcessRole.getUser().getId());

            assertEquals(competitionReferralSource, created.getCompetitionReferralSource());
            assertEquals(companyAge, created.getCompanyAge());
            assertEquals(companyPrimaryFocus, created.getCompanyPrimaryFocus());

            return true;
        }));

        when(applicationMapper.mapToResource(applicationExpectations.get())).thenReturn(applicationResource);

        ApplicationResource created =
                service.createApplicationByApplicationNameForUserIdAndCompetitionId("testApplication",
                        competition.getId(), user.getId(), organisation.getId()).getSuccess();

        verify(applicationRepository, times(1)).save(isA(Application.class));
        assertEquals(applicationResource, created);
    }

    @Test
    public void reopenApplicationCompetitionAlwaysOpen(){
        // Setup
        Application application = newApplication().withCompetition(newCompetition().withAlwaysOpen(true).build()).build();
        when(applicationRepository.findById(application.getId())).thenReturn(of(application));
        // Method under test
        ServiceResult<Void> result = service.reopenApplication(application.getId());
        // Assertions
        assertTrue(result.isFailure());
        assertEquals(APPLICATION_CANNOT_BE_REOPENED.getErrorKey(), result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void reopenApplicationCompetitionIsClosed(){
        // Setup
        Application application = newApplication().withCompetition(newCompetition().withCompetitionStatus(CLOSED).build()).build();
        when(applicationRepository.findById(application.getId())).thenReturn(of(application));
        // Method under test
        ServiceResult<Void> result = service.reopenApplication(application.getId());
        // Assertions
        assertTrue(result.isFailure());
        assertEquals(COMPETITION_NOT_OPEN.getErrorKey(), result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void reopenApplicationFundingDecisionSet(){
        // Setup
        Application application = newApplication().withFundingDecision(FUNDED).withCompetition(newCompetition().withCompetitionStatus(OPEN).build()).build();
        when(applicationRepository.findById(application.getId())).thenReturn(of(application));
        // Method under test
        ServiceResult<Void> result = service.reopenApplication(application.getId());
        // Assertions
        assertTrue(result.isFailure());
        assertEquals(APPLICATION_CANNOT_BE_REOPENED.getErrorKey(), result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void reopenApplicationNotSubmitted(){
        // Setup
        Application application = newApplication().withApplicationState(CREATED).withCompetition(newCompetition().withCompetitionStatus(OPEN).build()).build();
        when(applicationRepository.findById(application.getId())).thenReturn(of(application));
        // Method under test
        ServiceResult<Void> result = service.reopenApplication(application.getId());
        // Assertions
        assertTrue(result.isFailure());
        assertEquals(APPLICATION_MUST_BE_SUBMITTED.getErrorKey(), result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void reopenApplicationSuccess(){
        // Setup
        Application application = newApplication().withApplicationState(SUBMITTED).withCompetition(newCompetition().withCompetitionStatus(OPEN).build()).build();
        when(applicationRepository.findById(application.getId())).thenReturn(of(application));
        when(applicationNotificationService.sendNotificationApplicationReopened(application.getId())).thenReturn(serviceSuccess());
        // Method under test
        ServiceResult<Void> result = service.reopenApplication(application.getId());
        // Assertions
        assertTrue(result.isSuccess());
    }


    @Test
    public void applicationServiceShouldReturnApplicationByUserId() {
        User testUser1 = new User(1L, "test", "User1", "email1@email.nl", "testToken123abc", "my-uid");
        User testUser2 = new User(2L, "test", "User2", "email2@email.nl", "testToken456def", "my-uid");

        Application testApplication1 = new Application(null, "testApplication1Name", null);
        testApplication1.setId(1L);
        Application testApplication2 = new Application(null, "testApplication2Name", null);
        testApplication2.setId(2L);
        Application testApplication3 = new Application(null, "testApplication3Name", null);
        testApplication3.setId(3L);

        ApplicationResource testApplication1Resource = newApplicationResource().with(id(1L)).withName("testApplication1Name").build();
        ApplicationResource testApplication2Resource = newApplicationResource().with(id(2L)).withName("testApplication2Name").build();
        ApplicationResource testApplication3Resource = newApplicationResource().with(id(3L)).withName("testApplication3Name").build();

        Organisation organisation1 = newOrganisation().withId(1L).withName("test organisation 1").build();
        Organisation organisation2 = newOrganisation().withId(2L).withName("test organisation 2").build();

        ProcessRole testProcessRole1 = newProcessRole().withId(0L).withUser(testUser1).withApplication(testApplication1).withRole(ProcessRoleType.COLLABORATOR).withOrganisationId(organisation1.getId()).build();
        ProcessRole testProcessRole2 = newProcessRole().withId(1L).withUser(testUser1).withApplication(testApplication2).withRole(ProcessRoleType.COLLABORATOR).withOrganisationId(organisation1.getId()).build();
        ProcessRole testProcessRole3 = newProcessRole().withId(2L).withUser(testUser2).withApplication(testApplication2).withRole(ProcessRoleType.COLLABORATOR).withOrganisationId(organisation2.getId()).build();
        ProcessRole testProcessRole4 = newProcessRole().withId(3L).withUser(testUser2).withApplication(testApplication3).withRole(ProcessRoleType.COLLABORATOR).withOrganisationId(organisation2.getId()).build();

        when(userRepository.findById(1L)).thenReturn(of(testUser1));
        when(userRepository.findById(2L)).thenReturn(of(testUser2));

        when(applicationRepository.findById(testApplication1.getId())).thenReturn(of(testApplication1));
        when(applicationRepository.findById(testApplication2.getId())).thenReturn(of(testApplication2));
        when(applicationRepository.findById(testApplication3.getId())).thenReturn(of(testApplication3));

        when(processRoleRepository.findByUser(testUser1)).thenReturn(new ArrayList<ProcessRole>() {{
            add(testProcessRole1);
            add(testProcessRole2);
        }});

        when(processRoleRepository.findByUser(testUser2)).thenReturn(new ArrayList<ProcessRole>() {{
            add(testProcessRole3);
            add(testProcessRole4);
        }});

        when(applicationMapper.mapToResource(testApplication1)).thenReturn(testApplication1Resource);
        when(applicationMapper.mapToResource(testApplication2)).thenReturn(testApplication2Resource);
        when(applicationMapper.mapToResource(testApplication3)).thenReturn(testApplication3Resource);

        List<ApplicationResource> applicationsForUser1 = service.findByUserId(testUser1.getId()).getSuccess();
        assertEquals(2, applicationsForUser1.size());
        assertEquals(testApplication1Resource.getId(), applicationsForUser1.get(0).getId());
        assertEquals(testApplication2Resource.getId(), applicationsForUser1.get(1).getId());

        List<ApplicationResource> applicationsForUser2 = service.findByUserId(testUser2.getId()).getSuccess();
        assertEquals(2, applicationsForUser1.size());
        assertEquals(testApplication2Resource.getId(), applicationsForUser2.get(0).getId());
        assertEquals(testApplication3Resource.getId(), applicationsForUser2.get(1).getId());
    }

    @Test
    public void wildcardSearchByIdWithResultsOnSinglePage() {

        String searchString = "12";
        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource().build();

        Pageable pageable = setUpPageable(Role.SUPPORT, searchString, applicationResource, 5);

        ServiceResult<ApplicationPageResource> result = service.wildcardSearchById(searchString, pageable);

        assertWildcardSearchById(result, applicationResource, 5, 1, 5);

    }

    @Test
    public void wildcardSearchByIdWithResultsAcrossMultiplePages() {

        String searchString = "12";
        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource().build();

        Pageable pageable = setUpPageable(Role.COMP_ADMIN, searchString, applicationResource, 2);

        ServiceResult<ApplicationPageResource> result = service.wildcardSearchById(searchString, pageable);

        assertWildcardSearchById(result, applicationResource, 2, 3, 5);

    }

    @Test
    public void SearchApplicationAsInnovationLead() {

        String searchString = "12";
        ApplicationResource applicationResource = newApplicationResource().build();

        Pageable pageable = setUpPageable(INNOVATION_LEAD, searchString, applicationResource, 2);

        ServiceResult<ApplicationPageResource> result = service.wildcardSearchById(searchString, pageable);

        assertWildcardSearchById(result, applicationResource, 2, 3, 5);
    }

    @Test
    public void SearchApplicationAsStakeholder() {
        String searchString = "12";
        ApplicationResource applicationResource = newApplicationResource().build();

        Pageable pageable = setUpPageable(Role.STAKEHOLDER, searchString, applicationResource, 2);

        ServiceResult<ApplicationPageResource> result = service.wildcardSearchById(searchString, pageable);

        assertWildcardSearchById(result, applicationResource, 2, 3, 5);
    }

    @Test
    public void SearchApplicationAsAuditor() {
        String searchString = "12";
        ApplicationResource applicationResource = newApplicationResource().build();

        Pageable pageable = setUpPageable(Role.AUDITOR, searchString, applicationResource, 2);

        ServiceResult<ApplicationPageResource> result = service.wildcardSearchById(searchString, pageable);

        assertWildcardSearchById(result, applicationResource, 2, 3, 5);
    }

    private Pageable setUpPageable(Role role, String searchString, ApplicationResource applicationResource,
                                   int pageSize) {

        UserResource userResource = newUserResource().withRoleGlobal(role).build();
        User user = newUser().withId(userResource.getId()).withRoles(singleton(role)).build();

        setLoggedInUser(userResource);
        when(userRepository.findById(user.getId())).thenReturn(of(user));

        List<Application> applications = ApplicationBuilder.newApplication().build(5);

        Pageable pageable = PageRequest.of(0, pageSize);
        Page<Application> pagedResult = new PageImpl<>(applications, pageable, applications.size());

        when(applicationRepository.searchByIdLike(searchString, pageable)).thenReturn(pagedResult);
        when(applicationRepository.searchApplicationsByUserIdAndInnovationLeadRole(user.getId(), searchString, pageable)).thenReturn(pagedResult);
        when(applicationRepository.searchApplicationsByUserIdAndStakeholderRole(user.getId(), searchString, pageable)).thenReturn(pagedResult);
        when(applicationRepository.searchApplicationsByLikeAndExcludePreSubmissionStatuses(searchString, pageable)).thenReturn(pagedResult);
        when(applicationMapper.mapToResource(any(Application.class))).thenReturn(applicationResource);

        return pageable;
    }

    private void assertWildcardSearchById(ServiceResult<ApplicationPageResource> result, ApplicationResource applicationResource,
                                          int pageSize, int totalPages, int contentSize) {
        assertTrue(result.isSuccess());

        ApplicationPageResource resultObject = result.getSuccess();

        assertEquals(pageSize, resultObject.getSize());
        assertEquals(totalPages, resultObject.getTotalPages());
        assertEquals(contentSize, resultObject.getContent().size());
        assertEquals(applicationResource, resultObject.getContent().get(0));
    }

    @Test
    public void applicationControllerCanCreateApplication() {
        Long competitionId = 1L;
        long organisationId = 2L;
        Long userId = 3L;
        Competition competition = newCompetition().with(id(1L)).build();
        Organisation organisation = newOrganisation().with(id(organisationId)).build();
        User user = newUser().with(id(userId)).build();
        ApplicationState applicationState = CREATED;

        String applicationName = "testApplication";

        Application application = newApplication().
                withId(1L).
                withName(applicationName).
                withApplicationState(applicationState).
                withCompetition(competition).
                build();

        ApplicationResource newApplication = newApplicationResource().build();

        when(competitionRepository.findById(competition.getId())).thenReturn(of(competition));
        when(userRepository.findById(userId)).thenReturn(of(user));
        when(processRoleRepository.findByUser(user)).thenReturn(singletonList(
                newProcessRole().withUser(user).withOrganisationId(organisation.getId()).build()
        ));
        when(organisationRepository.findDistinctByProcessRolesUser(user)).thenReturn(singletonList(organisation));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        when(applicationRepository.findById(application.getId())).thenReturn(of(application));

        Supplier<Application> applicationExpectations = () -> argThat(lambdaMatches(created -> {
            assertEquals(applicationName, created.getName());
            assertEquals(applicationState, created.getApplicationProcess().getProcessState());
            assertEquals(competitionId, created.getCompetition().getId());
            return true;
        }));

        when(applicationMapper.mapToResource(applicationExpectations.get())).thenReturn(newApplication);

        ApplicationResource created = service.createApplicationByApplicationNameForUserIdAndCompetitionId(applicationName, competitionId, userId, organisationId).getSuccess();
        assertEquals(newApplication, created);
    }


    @Test
    public void setApplicationFundingEmailDateTime() {

        Long applicationId = 1L;
        ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);
        ApplicationResource newApplication = newApplicationResource().build();

        Supplier<Application> applicationExpectations = () -> argThat(lambdaMatches(created -> {
            assertEquals(tomorrow, created.getManageFundingEmailDate());
            return true;
        }));
        when(applicationMapper.mapToResource(applicationExpectations.get())).thenReturn(newApplication);

        ServiceResult<ApplicationResource> result = service.setApplicationFundingEmailDateTime(applicationId, tomorrow);
        assertTrue(result.isSuccess());
    }

    @Test
    public void setApplicationFundingEmailDateTime_alwaysOpen() {

        competition.setAlwaysOpen(true);
        Long applicationId = 1L;
        ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);
        ApplicationResource newApplication = newApplicationResource().build();

        Supplier<Application> applicationExpectations = () -> argThat(lambdaMatches(created -> {
            assertEquals(tomorrow, created.getManageFundingEmailDate());
            assertNotNull(created.getFeedbackReleased());
            return true;
        }));
        when(applicationMapper.mapToResource(applicationExpectations.get())).thenReturn(newApplication);

        ServiceResult<ApplicationResource> result = service.setApplicationFundingEmailDateTime(applicationId, tomorrow);

        competition.setAlwaysOpen(false);
        assertTrue(result.isSuccess());

    }

    @Test
    public void setApplicationFundingEmailDateTime_Failure() {

        Long applicationId = 1L;
        ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);
        ApplicationResource newApplication = newApplicationResource().build();

        Supplier<Application> applicationExpectations = () -> argThat(lambdaMatches(created -> {
            assertEquals(tomorrow, created.getManageFundingEmailDate());
            return true;
        }));
        when(applicationMapper.mapToResource(applicationExpectations.get())).thenReturn(newApplication);

        ServiceResult<ApplicationResource> result = service.setApplicationFundingEmailDateTime(applicationId, tomorrow);
        assertTrue(result.isSuccess());
    }

    @Test
    public void markAsIneligible() {
        long applicationId = 1L;
        String reason = "reason";

        Application application = newApplication()
                .withApplicationState(ApplicationState.SUBMITTED)
                .withId(applicationId)
                .build();

        IneligibleOutcome ineligibleOutcome = newIneligibleOutcome()
                .withReason(reason)
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(of(application));
        when(applicationWorkflowHandler.markIneligible(application, ineligibleOutcome)).thenReturn(true);
        when(applicationRepository.save(application)).thenReturn(application);

        ServiceResult<Void> result = service.markAsIneligible(applicationId, ineligibleOutcome);

        assertTrue(result.isSuccess());

        verify(applicationRepository).findById(applicationId);
        verify(applicationWorkflowHandler).markIneligible(application, ineligibleOutcome);
    }

    @Test
    public void markAsIneligible_applicationNotSubmitted() {
        long applicationId = 1L;
        String reason = "reason";

        Application application = newApplication()
                .withApplicationState(ApplicationState.OPENED)
                .withId(applicationId)
                .build();

        IneligibleOutcome ineligibleOutcome = newIneligibleOutcome()
                .withReason(reason)
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(of(application));
        when(applicationWorkflowHandler.markIneligible(application, ineligibleOutcome)).thenReturn(false);

        ServiceResult<Void> result = service.markAsIneligible(applicationId, ineligibleOutcome);

        assertTrue(result.isFailure());
        assertEquals(APPLICATION_MUST_BE_SUBMITTED.getErrorKey(), result.getErrors().get(0).getErrorKey());

        verify(applicationRepository).findById(applicationId);
        verify(applicationWorkflowHandler).markIneligible(application, ineligibleOutcome);
    }

    @Test
    public void showApplicationTeam() {
        User user = newUser().withRoles(singleton(Role.COMP_ADMIN)).build();
        when(userRepository.findById(234L)).thenReturn(of(user));

        ServiceResult<Boolean> serviceResult = service.showApplicationTeam(123L, 234L);

        assertTrue(serviceResult.isSuccess());
        assertTrue(serviceResult.getSuccess());
    }

    @Test
    public void showApplicationTeamWhenStakeholder() {
        User user = newUser().withRoles(singleton(Role.STAKEHOLDER)).build();
        when(userRepository.findById(234L)).thenReturn(of(user));

        ServiceResult<Boolean> serviceResult = service.showApplicationTeam(123L, 234L);

        assertTrue(serviceResult.isSuccess());
        assertTrue(serviceResult.getSuccess());
    }
    @Test
    public void showApplicationTeamNoUser() {

        when(userRepository.findById(234L)).thenReturn(Optional.empty());
        ServiceResult<Boolean> serviceResult = service.showApplicationTeam(123L, 234L);

        assertTrue(serviceResult.isFailure());
        assertEquals(serviceResult.getErrors().get(0).getErrorKey(), GENERAL_NOT_FOUND.getErrorKey());
    }

    @Test
    public void findLatestEmailFundingDateByCompetitionId() {
        long competitionId = 1L;

        ZonedDateTime expectedDateTime = ZonedDateTime.of(2018, 8, 1, 1, 0, 0, 0, ZoneId.systemDefault());

        Application application = newApplication()
                .withManageFundingEmailDate(expectedDateTime)
                .build();

        when(applicationRepository.findTopByCompetitionIdOrderByManageFundingEmailDateDesc(competitionId))
                .thenReturn(of(application));

        ServiceResult<ZonedDateTime> result = service
                .findLatestEmailFundingDateByCompetitionId(competitionId);

        assertTrue(result.isSuccess());
        assertEquals(expectedDateTime, result.getSuccess());
    }

    @Test
    public void getCompetitionByApplicationId() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        Competition competition = newCompetition().build();
        Application application = newApplication()
                .withCompetition(competition)
                .build();

        when(applicationRepository.findById(application.getId())).thenReturn(of(application));
        when(competitionMapper.mapToResource(competition)).thenReturn(competitionResource);

        CompetitionResource actual = service.getCompetitionByApplicationId(application.getId()).getSuccess();

        assertEquals(competitionResource, actual);

        verify(applicationRepository, only()).findById(application.getId());
        verify(competitionMapper, only()).mapToResource(competition);
    }

    @Test
    public void linkAddressesToOrganisation_FirstApplicationAddress() {
        Organisation organisation = newOrganisation()
                .withInternational(true)
                .build();
        Application application = newApplication().build();
        OrganisationAddress organisationAddress = newOrganisationAddress()
                .withApplicationAddresses(emptyList())
                .build();
        when(applicationRepository.findById(application.getId())).thenReturn(of(application));
        when(organisationRepository.findById(organisation.getId())).thenReturn(of(organisation));
        when(organisationAddressRepository.findFirstByOrganisationIdAndAddressTypeIdOrderByModifiedOnDesc(organisation.getId(), INTERNATIONAL.getId())).thenReturn(of(organisationAddress));

        ServiceResult<Void> result = service.linkAddressesToOrganisation(organisation.getId(), application.getId());

        assertTrue(result.isSuccess());

        ApplicationOrganisationAddress applicationOrganisationAddress = new ApplicationOrganisationAddress(organisationAddress, application);
        verify(applicationOrganisationAddressRepository).save(applicationOrganisationAddress);
    }

    @Test
    public void linkAddressesToOrganisation_AlreadyLinkedToApplication() {
        Organisation organisation = newOrganisation()
                .withInternational(true)
                .build();
        Application application = newApplication().build();
        OrganisationAddress organisationAddress = newOrganisationAddress()
                .withApplicationAddresses(newApplicationOrganisationAddress().build(1))
                .withAddress(newAddress().build())
                .build();
        when(applicationRepository.findById(application.getId())).thenReturn(of(application));
        when(organisationRepository.findById(organisation.getId())).thenReturn(of(organisation));
        when(organisationAddressRepository.findFirstByOrganisationIdAndAddressTypeIdOrderByModifiedOnDesc(organisation.getId(), INTERNATIONAL.getId())).thenReturn(of(organisationAddress));
        when(organisationAddressRepository.save(any())).thenAnswer((invocation) -> invocation.getArgument(0));

        ServiceResult<Void> result = service.linkAddressesToOrganisation(organisation.getId(), application.getId());

        assertTrue(result.isSuccess());

        verify(applicationOrganisationAddressRepository).save(createLambdaMatcher(applicationOrganisationAddress -> {
            //Assert these are new objects to be copied into new row.
            assertNotSame(applicationOrganisationAddress.getOrganisationAddress(), organisationAddress);
            assertNotSame(applicationOrganisationAddress.getOrganisationAddress().getAddress(), organisationAddress.getAddress());
        }));
    }
    @Test
    public void showApplicationTeamForAuditor() {
        User user = newUser().withRoles(singleton(Role.AUDITOR)).build();
        when(userRepository.findById(234L)).thenReturn(of(user));

        ServiceResult<Boolean> serviceResult = service.showApplicationTeam(123L, 234L);

        assertTrue(serviceResult.isSuccess());
        assertTrue(serviceResult.getSuccess());
    }
}