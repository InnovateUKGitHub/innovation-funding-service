package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.builder.ApplicationBuilder;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.form.builder.QuestionBuilder;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.user.builder.OrganisationBuilder;
import org.innovateuk.ifs.user.builder.ProcessRoleBuilder;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;

import static java.util.Collections.*;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.application.builder.IneligibleOutcomeBuilder.newIneligibleOutcome;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.APPLICATION_MUST_BE_SUBMITTED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
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
    private ApplicationFinanceHandler applicationFinanceHandlerMock;

    private FormInput formInput;
    private FormInputType formInputType;
    private Question question;
    private FileEntryResource fileEntryResource;
    private FormInputResponseFileEntryResource formInputResponseFileEntryResource;
    private FileEntry existingFileEntry;
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
        formInputResponseFileEntryResource = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);

        existingFileEntry = newFileEntry().with(id(999L)).build();
        existingFormInputResponse = newFormInputResponse().withFileEntry(existingFileEntry).build();
        existingFormInputResponses = singletonList(existingFormInputResponse);
        unlinkedFormInputFileEntry = newFormInputResponse().with(id(existingFormInputResponse.getId())).withFileEntry(null).build();
        final Competition openCompetition = newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build();
        openApplication = newApplication().withCompetition(openCompetition).build();

        when(applicationRepositoryMock.findOne(anyLong())).thenReturn(openApplication);

        multiAnswerQuestion = newQuestion().withMarksAsCompleteEnabled(Boolean.TRUE).withMultipleStatuses(Boolean.TRUE).withId(123L).build();
        leadAnswerQuestion = newQuestion().withMarksAsCompleteEnabled(Boolean.TRUE).withMultipleStatuses(Boolean.FALSE).withId(321L).build();

        orgType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        org1 = newOrganisation().withOrganisationType(orgType).withId(234L).build();
        org2 = newOrganisation().withId(345L).build();
        org3 = newOrganisation().withId(456L).build();

        roles = newProcessRole().withRole(Role.LEADAPPLICANT, Role.APPLICANT, Role.COLLABORATOR).withOrganisationId(234L, 345L, 456L).build(3).toArray(new ProcessRole[0]);
        section = newSection().withQuestions(Arrays.asList(multiAnswerQuestion, leadAnswerQuestion)).build();
        comp = newCompetition().withSections(Arrays.asList(section)).withMaxResearchRatio(30).build();
        app = newApplication().withCompetition(comp).withProcessRoles(roles).build();

        when(applicationRepositoryMock.findOne(app.getId())).thenReturn(app);
        when(organisationRepositoryMock.findOne(234L)).thenReturn(org1);
        when(organisationRepositoryMock.findOne(345L)).thenReturn(org2);
        when(organisationRepositoryMock.findOne(456L)).thenReturn(org3);
    }


    @Test
    public void createApplicationByApplicationNameForUserIdAndCompetitionId() {

        Competition competition = newCompetition().build();
        User user = newUser().build();
        Organisation organisation = newOrganisation().with(name("testOrganisation")).withId(organisationId).build();
        ProcessRole processRole = newProcessRole().withUser(user).withRole(Role.LEADAPPLICANT).withOrganisationId(organisation.getId()).build();
        ApplicationState applicationState = ApplicationState.CREATED;

        Application application = newApplication().
                withId(1L).
                withName("testApplication").
                withApplicationState(applicationState).
                withDurationInMonths(3L).
                withCompetition(competition).
                build();

        ApplicationResource applicationResource = newApplicationResource().build();

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(applicationRepositoryMock.save(any(Application.class))).thenReturn(application);
        when(processRoleRepositoryMock.findByUser(user)).thenReturn(singletonList(processRole));
        when(organisationRepositoryMock.findByUsers(user)).thenReturn(singletonList(organisation));
        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);

        Supplier<Application> applicationExpectations = () -> argThat(lambdaMatches(created -> {
            assertEquals("testApplication", created.getName());
            assertEquals(applicationState, created.getApplicationProcess().getActivityState());
            assertEquals(Long.valueOf(3), created.getDurationInMonths());
            assertEquals(competition.getId(), created.getCompetition().getId());
            assertNull(created.getStartDate());

            assertEquals(1, created.getProcessRoles().size());
            ProcessRole createdProcessRole = created.getProcessRoles().get(0);
            assertNull(createdProcessRole.getId());
            assertEquals(application.getId(), createdProcessRole.getApplicationId());
            assertEquals(organisation.getId(), createdProcessRole.getOrganisationId());
            assertEquals(Role.LEADAPPLICANT, createdProcessRole.getRole());
            assertEquals(user.getId(), createdProcessRole.getUser().getId());

            return true;
        }));

        when(applicationMapperMock.mapToResource(applicationExpectations.get())).thenReturn(applicationResource);
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.CREATED)).thenReturn(new ActivityState(ActivityType.APPLICATION, State.CREATED));

        ApplicationResource created =
                service.createApplicationByApplicationNameForUserIdAndCompetitionId("testApplication",
                        competition.getId(), user.getId()).getSuccess();

        verify(applicationRepositoryMock, times(2)).save(isA(Application.class));
        verify(processRoleRepositoryMock).save(isA(ProcessRole.class));
        assertEquals(applicationResource, created);
    }

    @Test
    public void applicationServiceShouldReturnApplicationByUserId() throws Exception {
        User testUser1 = new User(1L, "test", "User1", "email1@email.nl", "testToken123abc", "my-uid");
        User testUser2 = new User(2L, "test", "User2", "email2@email.nl", "testToken456def", "my-uid");

        Application testApplication1 = new Application(null, "testApplication1Name", null, new ActivityState(ActivityType.APPLICATION, State.CREATED));
        testApplication1.setId(1L);
        Application testApplication2 = new Application(null, "testApplication2Name", null, new ActivityState(ActivityType.APPLICATION, State.CREATED));
        testApplication2.setId(2L);
        Application testApplication3 = new Application(null, "testApplication3Name", null, new ActivityState(ActivityType.APPLICATION, State.CREATED));
        testApplication3.setId(3L);

        ApplicationResource testApplication1Resource = newApplicationResource().with(id(1L)).withName("testApplication1Name").build();
        ApplicationResource testApplication2Resource = newApplicationResource().with(id(2L)).withName("testApplication2Name").build();
        ApplicationResource testApplication3Resource = newApplicationResource().with(id(3L)).withName("testApplication3Name").build();

        Organisation organisation1 = new Organisation(1L, "test organisation 1");
        Organisation organisation2 = new Organisation(2L, "test organisation 2");

        ProcessRole testProcessRole1 = newProcessRole().withId(0L).withUser(testUser1).withApplication(testApplication1).withRole(Role.APPLICANT).withOrganisationId( organisation1.getId()).build();
        ProcessRole testProcessRole2 = newProcessRole().withId(1L).withUser(testUser1).withApplication(testApplication2).withRole(Role.APPLICANT).withOrganisationId( organisation1.getId()).build();
        ProcessRole testProcessRole3 = newProcessRole().withId(2L).withUser(testUser2).withApplication(testApplication2).withRole(Role.APPLICANT).withOrganisationId( organisation2.getId()).build();
        ProcessRole testProcessRole4 = newProcessRole().withId(3L).withUser(testUser2).withApplication(testApplication3).withRole(Role.APPLICANT).withOrganisationId( organisation2.getId()).build();

        when(userRepositoryMock.findOne(1L)).thenReturn(testUser1);
        when(userRepositoryMock.findOne(2L)).thenReturn(testUser2);

        when(applicationRepositoryMock.findOne(testApplication1.getId())).thenReturn(testApplication1);
        when(applicationRepositoryMock.findOne(testApplication2.getId())).thenReturn(testApplication2);
        when(applicationRepositoryMock.findOne(testApplication3.getId())).thenReturn(testApplication3);

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
    public void wildcardSearchByIdWithResultsOnSinglePage() throws Exception {

        String searchString = "12";
        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource().build();

        Pageable pageable = setUpMockingWildcardSearchById(searchString, applicationResource, 5);

        ServiceResult<ApplicationPageResource> result = service.wildcardSearchById(searchString, pageable);

        assertWildcardSearchById(result, applicationResource, 5, 1, 5);

    }

    @Test
    public void wildcardSearchByIdWithResultsAcrossMultiplePages() throws Exception {

        String searchString = "12";
        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource().build();

        Pageable pageable = setUpMockingWildcardSearchById(searchString, applicationResource, 2);

        ServiceResult<ApplicationPageResource> result = service.wildcardSearchById(searchString, pageable);

        assertWildcardSearchById(result, applicationResource, 2, 3, 5);

    }

    private Pageable setUpMockingWildcardSearchById(String searchString, ApplicationResource applicationResource,
                                                    int pageSize) {

        List<Application> applications = ApplicationBuilder.newApplication().build(5);

        Pageable pageable = new PageRequest(0, pageSize);
        Page<Application> pagedResult = new PageImpl<>(applications, pageable, applications.size());

        when(applicationRepositoryMock.searchByIdLike(searchString, pageable)).thenReturn(pagedResult);
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
    public void applicationControllerCanCreateApplication() throws Exception {
        Long competitionId = 1L;
        Long organisationId = 2L;
        Long userId = 3L;
        Competition competition = newCompetition().with(id(1L)).build();
        Organisation organisation = newOrganisation().with(id(organisationId)).build();
        User user = newUser().with(id(userId)).build();
        ApplicationState applicationState = ApplicationState.CREATED;

        String applicationName = "testApplication";

        Application application = newApplication().
                withId(1L).
                withName(applicationName).
                withApplicationState(applicationState).
                withCompetition(competition).
                build();

        ApplicationResource newApplication = newApplicationResource().build();

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(userRepositoryMock.findOne(userId)).thenReturn(user);
        when(processRoleRepositoryMock.findByUser(user)).thenReturn(singletonList(
                newProcessRole().withUser(user).withOrganisationId(organisation.getId()).build()
        ));
        when(organisationRepositoryMock.findByUsers(user)).thenReturn(singletonList(organisation));
        when(applicationRepositoryMock.save(any(Application.class))).thenReturn(application);
        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);

        Supplier<Application> applicationExpectations = () -> argThat(lambdaMatches(created -> {
            assertEquals(applicationName, created.getName());
            assertEquals(applicationState, created.getApplicationProcess().getActivityState());
            assertEquals(competitionId, created.getCompetition().getId());
            return true;
        }));

        when(applicationMapperMock.mapToResource(applicationExpectations.get())).thenReturn(newApplication);
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.CREATED)).thenReturn(new ActivityState(ActivityType.APPLICATION, State.CREATED));

        ApplicationResource created = service.createApplicationByApplicationNameForUserIdAndCompetitionId(applicationName, competitionId, userId).getSuccess();
        assertEquals(newApplication, created);
    }


    @Test
    public void setApplicationFundingEmailDateTime() throws Exception {

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
    public void setApplicationFundingEmailDateTime_Failure() throws Exception {

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
    public void markAsIneligible() throws Exception {
        long applicationId = 1L;
        String reason = "reason";

        Application application = newApplication()
                .withApplicationState(ApplicationState.SUBMITTED)
                .withId(applicationId)
                .build();

        IneligibleOutcome ineligibleOutcome = newIneligibleOutcome()
                .withReason(reason)
                .build();

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(applicationWorkflowHandlerMock.markIneligible(application, ineligibleOutcome)).thenReturn(true);
        when(applicationRepositoryMock.save(application)).thenReturn(application);

        ServiceResult<Void> result = service.markAsIneligible(applicationId, ineligibleOutcome);

        assertTrue(result.isSuccess());

        verify(applicationRepositoryMock).findOne(applicationId);
        verify(applicationWorkflowHandlerMock).markIneligible(application, ineligibleOutcome);
        verify(applicationRepositoryMock).save(application);
    }

    @Test
    public void markAsIneligible_applicationNotSubmitted() throws Exception {
        long applicationId = 1L;
        String reason = "reason";

        Application application = newApplication()
                .withApplicationState(ApplicationState.OPEN)
                .withId(applicationId)
                .build();

        IneligibleOutcome ineligibleOutcome = newIneligibleOutcome()
                .withReason(reason)
                .build();

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(applicationWorkflowHandlerMock.markIneligible(application, ineligibleOutcome)).thenReturn(false);

        ServiceResult<Void> result = service.markAsIneligible(applicationId, ineligibleOutcome);

        assertTrue(result.isFailure());
        assertEquals(APPLICATION_MUST_BE_SUBMITTED.getErrorKey(), result.getErrors().get(0).getErrorKey());

        verify(applicationRepositoryMock).findOne(applicationId);
        verify(applicationWorkflowHandlerMock).markIneligible(application, ineligibleOutcome);
    }

    @Test
    public void showApplicationTeam() {
        User user = newUser().withRoles(singleton(Role.COMP_ADMIN)).build();
        when(userRepositoryMock.findOne(234L)).thenReturn(user);

        ServiceResult<Boolean> serviceResult = service.showApplicationTeam(123L, 234L);

        assertTrue(serviceResult.isSuccess());
        assertTrue(serviceResult.getSuccess());
    }

    @Test
    public void showApplicationTeamNoUser() {

        when(userRepositoryMock.findOne(234L)).thenReturn(null);
        ServiceResult<Boolean> serviceResult = service.showApplicationTeam(123L, 234L);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getErrors().get(0).getErrorKey().equals(GENERAL_NOT_FOUND.getErrorKey()));
    }

    @Test
    public void findUnsuccessfulApplicationsWhenNoneFound() throws Exception {

        Long competitionId = 1L;

        Page<Application> pagedResult = mock(Page.class);
        when(pagedResult.getContent()).thenReturn(emptyList());
        when(pagedResult.getTotalElements()).thenReturn(0L);
        when(pagedResult.getTotalPages()).thenReturn(0);
        when(pagedResult.getNumber()).thenReturn(0);
        when(pagedResult.getSize()).thenReturn(0);
        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateIn(eq(competitionId), any(), any())).thenReturn(pagedResult);

        ServiceResult<ApplicationPageResource> result = service.findUnsuccessfulApplications(competitionId, 0, 20, "id");
        assertTrue(result.isSuccess());

        ApplicationPageResource unsuccessfulApplicationsPage = result.getSuccess();
        assertTrue(unsuccessfulApplicationsPage.getContent().isEmpty());

    }

    @Test
    public void findUnsuccessfulApplications() throws Exception {

        Long competitionId = 1L;

        Long leadOrganisationId = 7L;
        String leadOrganisationName = "lead Organisation name";
        Organisation leadOrganisation = OrganisationBuilder.newOrganisation()
                .withId(leadOrganisationId)
                .withName(leadOrganisationName)
                .build();

        ProcessRole leadProcessRole = ProcessRoleBuilder.newProcessRole()
                .withRole(Role.LEADAPPLICANT)
                .withOrganisationId(leadOrganisationId)
                .build();

        Application application1 = ApplicationBuilder.newApplication()
                .withId(11L)
                .withProcessRoles(leadProcessRole)
                .build();
        Application application2 = ApplicationBuilder.newApplication()
                .withId(12L)
                .withProcessRoles(leadProcessRole)
                .build();

        List<Application> unsuccessfulApplications = new ArrayList<>();
        unsuccessfulApplications.add(application1);
        unsuccessfulApplications.add(application2);

        ApplicationResource applicationResource1 = ApplicationResourceBuilder.newApplicationResource().build();
        ApplicationResource applicationResource2 = ApplicationResourceBuilder.newApplicationResource().build();

        Page<Application> pagedResult = mock(Page.class);
        when(pagedResult.getContent()).thenReturn(unsuccessfulApplications);
        when(pagedResult.getTotalElements()).thenReturn(2L);
        when(pagedResult.getTotalPages()).thenReturn(1);
        when(pagedResult.getNumber()).thenReturn(0);
        when(pagedResult.getSize()).thenReturn(2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateIn(eq(competitionId), any(), any())).thenReturn(pagedResult);
        when(applicationMapperMock.mapToResource(application1)).thenReturn(applicationResource1);
        when(applicationMapperMock.mapToResource(application2)).thenReturn(applicationResource2);
        when(organisationRepositoryMock.findOne(leadOrganisationId)).thenReturn(leadOrganisation);

        ServiceResult<ApplicationPageResource> result = service.findUnsuccessfulApplications(competitionId, 0, 20, "id");
        assertTrue(result.isSuccess());

        ApplicationPageResource unsuccessfulApplicationsPage = result.getSuccess();
        assertTrue(unsuccessfulApplicationsPage.getSize() == 2);
        assertEquals(applicationResource1, unsuccessfulApplicationsPage.getContent().get(0));
        assertEquals(applicationResource2, unsuccessfulApplicationsPage.getContent().get(1));
        assertEquals(leadOrganisationName, unsuccessfulApplicationsPage.getContent().get(0).getLeadOrganisationName());
    }
}
