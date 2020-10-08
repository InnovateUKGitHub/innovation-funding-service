package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.builder.ApplicationBuilder;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationOrganisationAddress;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.repository.ApplicationOrganisationAddressRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.CompanyAge;
import org.innovateuk.ifs.application.resource.CompanyPrimaryFocus;
import org.innovateuk.ifs.application.resource.CompetitionReferralSource;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
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
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.INTERNATIONAL;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationOrganisationAddressBuilder.newApplicationOrganisationAddress;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.application.builder.IneligibleOutcomeBuilder.newIneligibleOutcome;
import static org.innovateuk.ifs.application.resource.ApplicationState.CREATED;
import static org.innovateuk.ifs.application.resource.CompanyAge.PRE_START_UP;
import static org.innovateuk.ifs.application.resource.CompanyPrimaryFocus.CHEMICALS;
import static org.innovateuk.ifs.application.resource.CompetitionReferralSource.BUSINESS_CONTACT;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.APPLICATION_MUST_BE_SUBMITTED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ApplicationServiceImpl}
 */
public class ApplicationServiceImplTest extends BaseServiceUnitTest<ApplicationService> {
    @Override
    protected ApplicationService supplyServiceUnderTest() {
        return new ApplicationServiceImpl();
    }

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    private ApplicationMapper applicationMapperMock;

    @Mock
    private CompetitionMapper competitionMapperMock;

    @Mock
    private ApplicationWorkflowHandler applicationWorkflowHandlerMock;

    @Mock
    private OrganisationAddressRepository organisationAddressRepository;

    @Mock
    private ApplicationOrganisationAddressRepository applicationOrganisationAddressRepository;

    private FormInput formInput;
    private FormInputType formInputType;
    private Question question;
    private FileEntryResource fileEntryResource;
    private FormInputResponseFileEntryResource formInputResponseFileEntryResource;
    private List<FileEntry> existingFileEntry;
    private FormInputResponse existingFormInputResponse;
    private List<FormInputResponse> existingFormInputResponses;
    private FormInputResponse unlinkedFormInputFileEntry;
    private Long organisationId = 456L;

    private Question multiAnswerQuestion;
    private Question leadAnswerQuestion;

    private OrganisationType orgType;
    private Organisation org1;
    private Organisation org2;
    private Organisation org3;

    private ProcessRole[] roles;
    private Section section;
    private Competition comp;
    private Application app;

    private Application openApplication;

    @Before
    public void setUp() throws Exception {
        question = QuestionBuilder.newQuestion().build();

        formInputType = FormInputType.FILEUPLOAD;

        formInput = newFormInput().withType(formInputType).build();
        formInput.setId(123L);
        formInput.setQuestion(question);
        question.setFormInputs(singletonList(formInput));

        fileEntryResource = newFileEntryResource().with(id(999L)).build();
        formInputResponseFileEntryResource = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L, 111L);

        existingFileEntry = singletonList(newFileEntry().with(id(999L)).build());
        existingFormInputResponse = newFormInputResponse().withFileEntries(existingFileEntry).build();
        existingFormInputResponses = singletonList(existingFormInputResponse);
        unlinkedFormInputFileEntry = newFormInputResponse().with(id(existingFormInputResponse.getId())).withFileEntries(null).build();
        final Competition openCompetition = newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build();
        openApplication = newApplication().withCompetition(openCompetition).build();

        when(applicationRepositoryMock.findById(anyLong())).thenReturn(Optional.of(openApplication));

        multiAnswerQuestion = newQuestion().withMarksAsCompleteEnabled(Boolean.TRUE).withMultipleStatuses(Boolean.TRUE).withId(123L).build();
        leadAnswerQuestion = newQuestion().withMarksAsCompleteEnabled(Boolean.TRUE).withMultipleStatuses(Boolean.FALSE).withId(321L).build();

        orgType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        org1 = newOrganisation().withOrganisationType(orgType).withId(234L).build();
        org2 = newOrganisation().withId(345L).build();
        org3 = newOrganisation().withId(456L).build();

        roles = newProcessRole().withRole(Role.LEADAPPLICANT, Role.APPLICANT, Role.COLLABORATOR).withOrganisationId(234L, 345L, 456L).build(3).toArray(new ProcessRole[0]);
        section = newSection().withQuestions(Arrays.asList(multiAnswerQuestion, leadAnswerQuestion)).build();
        comp = newCompetition().withSections(singletonList(section)).withMaxResearchRatio(30).build();
        app = newApplication().withCompetition(comp).withProcessRoles(roles).build();

        when(applicationRepositoryMock.findById(app.getId())).thenReturn(Optional.of(app));
        when(organisationRepositoryMock.findById(234L)).thenReturn(Optional.of(org1));
        when(organisationRepositoryMock.findById(345L)).thenReturn(Optional.of(org2));
        when(organisationRepositoryMock.findById(456L)).thenReturn(Optional.of(org3));
    }


    @Test
    public void createApplicationByApplicationNameForUserIdAndCompetitionId() {

        Competition competition = newCompetition().build();
        User user = newUser().build();
        Organisation organisation = newOrganisation().with(name("testOrganisation")).withId(organisationId).build();
        ProcessRole processRole = newProcessRole().withUser(user).withRole(Role.LEADAPPLICANT).withOrganisationId(organisation.getId()).build();
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

        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(applicationRepositoryMock.save(any(Application.class))).thenReturn(application);
        when(processRoleRepositoryMock.findByUser(user)).thenReturn(singletonList(processRole));
        when(organisationRepositoryMock.findDistinctByProcessRolesUser(user)).thenReturn(singletonList(organisation));
        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));

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
            assertEquals(Role.LEADAPPLICANT, createdProcessRole.getRole());
            assertEquals(user.getId(), createdProcessRole.getUser().getId());

            assertEquals(competitionReferralSource, created.getCompetitionReferralSource());
            assertEquals(companyAge, created.getCompanyAge());
            assertEquals(companyPrimaryFocus, created.getCompanyPrimaryFocus());

            return true;
        }));

        when(applicationMapperMock.mapToResource(applicationExpectations.get())).thenReturn(applicationResource);

        ApplicationResource created =
                service.createApplicationByApplicationNameForUserIdAndCompetitionId("testApplication",
                        competition.getId(), user.getId(), organisation.getId()).getSuccess();

        verify(applicationRepositoryMock, times(1)).save(isA(Application.class));
        assertEquals(applicationResource, created);
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

        ProcessRole testProcessRole1 = newProcessRole().withId(0L).withUser(testUser1).withApplication(testApplication1).withRole(Role.APPLICANT).withOrganisationId(organisation1.getId()).build();
        ProcessRole testProcessRole2 = newProcessRole().withId(1L).withUser(testUser1).withApplication(testApplication2).withRole(Role.APPLICANT).withOrganisationId(organisation1.getId()).build();
        ProcessRole testProcessRole3 = newProcessRole().withId(2L).withUser(testUser2).withApplication(testApplication2).withRole(Role.APPLICANT).withOrganisationId(organisation2.getId()).build();
        ProcessRole testProcessRole4 = newProcessRole().withId(3L).withUser(testUser2).withApplication(testApplication3).withRole(Role.APPLICANT).withOrganisationId(organisation2.getId()).build();

        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userRepositoryMock.findById(2L)).thenReturn(Optional.of(testUser2));

        when(applicationRepositoryMock.findById(testApplication1.getId())).thenReturn(Optional.of(testApplication1));
        when(applicationRepositoryMock.findById(testApplication2.getId())).thenReturn(Optional.of(testApplication2));
        when(applicationRepositoryMock.findById(testApplication3.getId())).thenReturn(Optional.of(testApplication3));

        when(processRoleRepositoryMock.findByUser(testUser1)).thenReturn(new ArrayList<ProcessRole>() {{
            add(testProcessRole1);
            add(testProcessRole2);
        }});

        when(processRoleRepositoryMock.findByUser(testUser2)).thenReturn(new ArrayList<ProcessRole>() {{
            add(testProcessRole3);
            add(testProcessRole4);
        }});

        when(applicationMapperMock.mapToResource(testApplication1)).thenReturn(testApplication1Resource);
        when(applicationMapperMock.mapToResource(testApplication2)).thenReturn(testApplication2Resource);
        when(applicationMapperMock.mapToResource(testApplication3)).thenReturn(testApplication3Resource);

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

    private Pageable setUpPageable(Role role, String searchString, ApplicationResource applicationResource,
                                   int pageSize) {

        UserResource userResource = newUserResource().withRolesGlobal(singletonList(role)).build();
        User user = newUser().withId(userResource.getId()).withRoles(singleton(role)).build();

        setLoggedInUser(userResource);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        List<Application> applications = ApplicationBuilder.newApplication().build(5);

        Pageable pageable = PageRequest.of(0, pageSize);
        Page<Application> pagedResult = new PageImpl<>(applications, pageable, applications.size());

        when(applicationRepositoryMock.searchByIdLike(searchString, pageable)).thenReturn(pagedResult);
        when(applicationRepositoryMock.searchApplicationsByUserIdAndInnovationLeadRole(user.getId(), searchString, pageable)).thenReturn(pagedResult);
        when(applicationRepositoryMock.searchApplicationsByUserIdAndStakeholderRole(user.getId(), searchString, pageable)).thenReturn(pagedResult);
        when(applicationMapperMock.mapToResource(any(Application.class))).thenReturn(applicationResource);

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
        Long organisationId = 2L;
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

        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user));
        when(processRoleRepositoryMock.findByUser(user)).thenReturn(singletonList(
                newProcessRole().withUser(user).withOrganisationId(organisation.getId()).build()
        ));
        when(organisationRepositoryMock.findDistinctByProcessRolesUser(user)).thenReturn(singletonList(organisation));
        when(applicationRepositoryMock.save(any(Application.class))).thenReturn(application);
        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));

        Supplier<Application> applicationExpectations = () -> argThat(lambdaMatches(created -> {
            assertEquals(applicationName, created.getName());
            assertEquals(applicationState, created.getApplicationProcess().getProcessState());
            assertEquals(competitionId, created.getCompetition().getId());
            return true;
        }));

        when(applicationMapperMock.mapToResource(applicationExpectations.get())).thenReturn(newApplication);

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
        when(applicationMapperMock.mapToResource(applicationExpectations.get())).thenReturn(newApplication);

        ServiceResult<ApplicationResource> result = service.setApplicationFundingEmailDateTime(applicationId, tomorrow);
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
        when(applicationMapperMock.mapToResource(applicationExpectations.get())).thenReturn(newApplication);

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

        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationWorkflowHandlerMock.markIneligible(application, ineligibleOutcome)).thenReturn(true);
        when(applicationRepositoryMock.save(application)).thenReturn(application);

        ServiceResult<Void> result = service.markAsIneligible(applicationId, ineligibleOutcome);

        assertTrue(result.isSuccess());

        verify(applicationRepositoryMock).findById(applicationId);
        verify(applicationWorkflowHandlerMock).markIneligible(application, ineligibleOutcome);
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

        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationWorkflowHandlerMock.markIneligible(application, ineligibleOutcome)).thenReturn(false);

        ServiceResult<Void> result = service.markAsIneligible(applicationId, ineligibleOutcome);

        assertTrue(result.isFailure());
        assertEquals(APPLICATION_MUST_BE_SUBMITTED.getErrorKey(), result.getErrors().get(0).getErrorKey());

        verify(applicationRepositoryMock).findById(applicationId);
        verify(applicationWorkflowHandlerMock).markIneligible(application, ineligibleOutcome);
    }

    @Test
    public void showApplicationTeam() {
        User user = newUser().withRoles(singleton(Role.COMP_ADMIN)).build();
        when(userRepositoryMock.findById(234L)).thenReturn(Optional.of(user));

        ServiceResult<Boolean> serviceResult = service.showApplicationTeam(123L, 234L);

        assertTrue(serviceResult.isSuccess());
        assertTrue(serviceResult.getSuccess());
    }

    @Test
    public void showApplicationTeamWhenStakeholder() {
        User user = newUser().withRoles(singleton(Role.STAKEHOLDER)).build();
        when(userRepositoryMock.findById(234L)).thenReturn(Optional.of(user));

        ServiceResult<Boolean> serviceResult = service.showApplicationTeam(123L, 234L);

        assertTrue(serviceResult.isSuccess());
        assertTrue(serviceResult.getSuccess());
    }

    @Test
    public void showApplicationTeamNoUser() {

        when(userRepositoryMock.findById(234L)).thenReturn(Optional.empty());
        ServiceResult<Boolean> serviceResult = service.showApplicationTeam(123L, 234L);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getErrors().get(0).getErrorKey().equals(GENERAL_NOT_FOUND.getErrorKey()));
    }

    @Test
    public void findLatestEmailFundingDateByCompetitionId() {
        long competitionId = 1L;

        ZonedDateTime expectedDateTime = ZonedDateTime.of(2018, 8, 1, 1, 0, 0, 0, ZoneId.systemDefault());

        Application application = newApplication()
                .withManageFundingEmailDate(expectedDateTime)
                .build();

        when(applicationRepositoryMock.findTopByCompetitionIdOrderByManageFundingEmailDateDesc(competitionId))
                .thenReturn(Optional.of(application));

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

        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        when(competitionMapperMock.mapToResource(competition)).thenReturn(competitionResource);

        CompetitionResource actual = service.getCompetitionByApplicationId(application.getId()).getSuccess();

        assertEquals(competitionResource, actual);

        verify(applicationRepositoryMock, only()).findById(application.getId());
        verify(competitionMapperMock, only()).mapToResource(competition);
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
        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation));
        when(organisationAddressRepository.findFirstByOrganisationIdAndAddressTypeIdOrderByModifiedOnDesc(organisation.getId(), INTERNATIONAL.getId())).thenReturn(Optional.of(organisationAddress));

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
        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation));
        when(organisationAddressRepository.findFirstByOrganisationIdAndAddressTypeIdOrderByModifiedOnDesc(organisation.getId(), INTERNATIONAL.getId())).thenReturn(Optional.of(organisationAddress));
        when(organisationAddressRepository.save(any())).thenAnswer((invocation) -> invocation.getArgument(0));

        ServiceResult<Void> result = service.linkAddressesToOrganisation(organisation.getId(), application.getId());

        assertTrue(result.isSuccess());

        verify(applicationOrganisationAddressRepository).save(createLambdaMatcher(applicationOrganisationAddress -> {
            //Assert these are new objects to be copied into new row.
            assertNotSame(applicationOrganisationAddress.getOrganisationAddress(), organisationAddress);
            assertNotSame(applicationOrganisationAddress.getOrganisationAddress().getAddress(), organisationAddress.getAddress());
        }));
    }
}