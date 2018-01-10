package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.builder.QuestionBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.resource.FileEntryResourceAssembler;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.user.domain.*;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationIneligibleSendResourceBuilder.newApplicationIneligibleSendResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.IneligibleOutcomeBuilder.newIneligibleOutcome;
import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.application.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.application.transactional.ApplicationServiceImpl.Notifications.APPLICATION_SUBMITTED;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.APPLICATION_MUST_BE_INELIGIBLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.APPLICATION_MUST_BE_SUBMITTED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Tests for {@link ApplicationServiceImpl}
 */
public class ApplicationServiceImplMockTest extends BaseServiceUnitTest<ApplicationService> {

    private static final Set<State> FUNDING_DECISIONS_MADE_STATUSES = simpleMapSet(asLinkedSet(
            ApplicationState.APPROVED,
            ApplicationState.REJECTED), ApplicationState::getBackingState);
    private static final String WEB_BASE_URL = "www.baseUrl.com" ;

    private Application openApplication;

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

        roles = newProcessRole().withRole(UserRoleType.LEADAPPLICANT, UserRoleType.APPLICANT, UserRoleType.COLLABORATOR).withOrganisationId(234L, 345L, 456L).build(3).toArray(new ProcessRole[0]);
        section = newSection().withQuestions(Arrays.asList(multiAnswerQuestion, leadAnswerQuestion)).build();
        comp = newCompetition().withSections(Arrays.asList(section)).withMaxResearchRatio(30).build();
        app = newApplication().withCompetition(comp).withProcessRoles(roles).build();

        when(applicationRepositoryMock.findOne(app.getId())).thenReturn(app);
        when(organisationRepositoryMock.findOne(234L)).thenReturn(org1);
        when(organisationRepositoryMock.findOne(345L)).thenReturn(org2);
        when(organisationRepositoryMock.findOne(456L)).thenReturn(org3);
        ReflectionTestUtils.setField(service, "webBaseUrl", WEB_BASE_URL);
    }

    @Test
    public void testSendNotificationApplicationSubmitted() {
        User leadUser = newUser().withEmailAddress("leadapplicant@example.com").build();
        Role leadRole = newRole().withType(LEADAPPLICANT).build();
        ProcessRole leadProcessRole = newProcessRole().withUser(leadUser).withRole(leadRole).build();
        Competition competition = newCompetition().build();
        Application application = newApplication().withProcessRoles(leadProcessRole).withCompetition(competition).build();
        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);
        when(notificationServiceMock.sendNotification(any(), eq(EMAIL))).thenReturn(ServiceResult.serviceSuccess());

        ServiceResult<Void> result = service.sendNotificationApplicationSubmitted(application.getId());

        verify(notificationServiceMock).sendNotification(createLambdaMatcher(notification -> {
            assertEquals(application.getName(), notification.getGlobalArguments().get("applicationName"));
            assertEquals(competition.getName(), notification.getGlobalArguments().get("competitionName"));
            assertEquals(1, notification.getTo().size());
            assertEquals(leadUser.getEmail(), notification.getTo().get(0).getEmailAddress());
            assertEquals(leadUser.getName(), notification.getTo().get(0).getName());
            assertEquals(APPLICATION_SUBMITTED, notification.getMessageKey());
        }), eq(EMAIL));
        assertTrue(result.isSuccess());
    }

    @Test
    public void createApplicationByApplicationNameForUserIdAndCompetitionId() {

        Competition competition = newCompetition().build();
        User user = newUser().build();
        Organisation organisation = newOrganisation().with(name("testOrganisation")).withId(organisationId).build();
        Role leadApplicantRole = newRole().withType(LEADAPPLICANT).build();
        ProcessRole processRole = newProcessRole().withUser(user).withRole(leadApplicantRole).withOrganisationId(organisation.getId()).build();
        ApplicationState applicationState = ApplicationState.CREATED;

        Application application = newApplication().
                withId(1L).
                withName("testApplication").
                withApplicationState(applicationState).
                withDurationInMonths(3L).
                withCompetition(competition).
                build();

        ApplicationResource applicationResource = newApplicationResource().build();

        when(roleRepositoryMock.findOneByName(leadApplicantRole.getName())).thenReturn(leadApplicantRole);
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
            assertEquals(leadApplicantRole.getId(), createdProcessRole.getRole().getId());
            assertEquals(user.getId(), createdProcessRole.getUser().getId());

            return true;
        }));

        when(applicationMapperMock.mapToResource(applicationExpectations.get())).thenReturn(applicationResource);
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.CREATED)).thenReturn(new ActivityState(ActivityType.APPLICATION, State.CREATED));

        ApplicationResource created =
                service.createApplicationByApplicationNameForUserIdAndCompetitionId("testApplication",
                        competition.getId(), user.getId()).getSuccessObject();

        verify(applicationRepositoryMock, times(2)).save(isA(Application.class));
        verify(processRoleRepositoryMock).save(isA(ProcessRole.class));
        assertEquals(applicationResource, created);
    }

    @Test
    public void createFormInputResponseFileUpload() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceSuccess(Pair.of(fileFound, newFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);
        when(processRoleRepositoryMock.findOne(789L)).thenReturn(newProcessRole().build());
        when(formInputRepositoryMock.findOne(123L)).thenReturn(newFormInput().build());
        when(applicationRepositoryMock.findOne(456L)).thenReturn(openApplication);

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isSuccess());
        FormInputResponseFileEntryResource resultParts = result.getSuccessObject();
        assertEquals(Long.valueOf(999), resultParts.getFileEntryResource().getId());

        verify(formInputResponseRepositoryMock).findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L);
    }

    @Test
    public void createFormInputResponseFileUploadButReplaceIfFileAlreadyExistsForFormInputResponse() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(987L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry alreadyExistingFileEntry = newFileEntry().with(id(987L)).build();
        FormInputResponse existingFormInputResponseWithLinkedFileEntry = newFormInputResponse().withFileEntry(alreadyExistingFileEntry).build();

        File fileFound = mock(File.class);

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withType(formInputType).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(singletonList(formInputLocal));

        when(fileServiceMock.deleteFileIgnoreNotFound(alreadyExistingFileEntry.getId())).thenReturn(serviceSuccess(alreadyExistingFileEntry));

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).thenReturn(serviceSuccess(Pair.of(fileFound, alreadyExistingFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponseWithLinkedFileEntry);
        when(processRoleRepositoryMock.findOne(789L)).thenReturn(newProcessRole().build());
        when(formInputRepositoryMock.findOne(123L)).thenReturn(formInputLocal);
        when(applicationRepositoryMock.findOne(456L)).thenReturn(openApplication);

        when(fileServiceMock.getFileByFileEntryId(987L)).thenReturn(serviceSuccess(inputStreamSupplier));

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        FormInputResponseFileEntryResource resultParts = result.getSuccessObject();
        assertEquals(Long.valueOf(987), resultParts.getFileEntryResource().getId());

        verify(formInputResponseRepositoryMock, times(3)).findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L);
    }

    @Test
    public void createFormInputResponseFileUploadButFileServiceCallFails() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceFailure(internalServerErrorError()));

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError()));
    }

    @Test
    public void createFormInputResponseFileUploadWithAlreadyExistingFormInputResponse() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceSuccess(Pair.of(fileFound, newFileEntry)));

        FormInputResponse existingFormInputResponse = newFormInputResponse().build();
        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isSuccess());
        FormInputResponseFileEntryResource resultParts = result.getSuccessObject();
        assertEquals(Long.valueOf(999), resultParts.getFileEntryResource().getId());

        assertEquals(newFileEntry, existingFormInputResponse.getFileEntry());
    }

    @Test
    public void createFormInputResponseFileUploadButProcessRoleNotFound() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceSuccess(Pair.of(fileFound, newFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);
        when(processRoleRepositoryMock.findOne(789L)).thenReturn(null);

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(ProcessRole.class, 789L)));
    }

    @Test
    public void createFormInputResponseFileUploadButFormInputNotFound() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceSuccess(Pair.of(fileFound, newFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);
        when(processRoleRepositoryMock.findOne(789L)).thenReturn(newProcessRole().build());
        when(formInputRepositoryMock.findOne(123L)).thenReturn(null);

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FormInput.class, 123L)));
    }

    @Test
    public void createFormInputResponseFileUploadButApplicationNotFound() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceSuccess(Pair.of(fileFound, newFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);
        when(processRoleRepositoryMock.findOne(789L)).thenReturn(newProcessRole().build());
        when(formInputRepositoryMock.findOne(123L)).thenReturn(newFormInput().build());
        when(applicationRepositoryMock.findOne(456L)).thenReturn(null);

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Application.class, 456L)));
    }

    @Test
    public void updateFormInputResponseFileUpload() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(999L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry existingFileEntry = newFileEntry().with(id(999L)).build();

        FormInputResponse existingFormInputResponse = newFormInputResponse().withFileEntry(existingFileEntry).build();

        File fileFound = mock(File.class);

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withType(formInputType).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(singletonList(formInputLocal));
        when(formInputRepositoryMock.findOne(123L)).thenReturn(formInputLocal);

        when(fileServiceMock.updateFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceSuccess(Pair.of(fileFound, existingFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));

        ServiceResult<Void> result =
                service.updateFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isSuccess());

        verify(formInputResponseRepositoryMock).findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L);
    }

    @Test
    public void updateFormInputResponseFileUploadButFileServiceCallFails() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource formInputFileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry fileEntry = FileEntryResourceAssembler.valueOf(fileEntryResource);
        FormInputResponse existingFormInputResponse =
                newFormInputResponse().withFileEntry(fileEntry).build();

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withType(formInputType).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(singletonList(formInputLocal));

        when(formInputRepositoryMock.findOne(123L)).thenReturn(formInputLocal);
        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));

        when(fileServiceMock.updateFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceFailure(internalServerErrorError()));

        ServiceResult<Void> result =
                service.updateFormInputResponseFileUpload(formInputFileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError()));
    }

    @Test
    public void deleteFormInputResponseFileUpload() {

        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(formInputResponseRepositoryMock.findByApplicationIdAndFormInputId(456L, 123L)).thenReturn(existingFormInputResponses);
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));
        when(formInputResponseRepositoryMock.save(existingFormInputResponse)).thenReturn(unlinkedFormInputFileEntry);
        when(fileServiceMock.deleteFileIgnoreNotFound(999L)).thenReturn(serviceSuccess(existingFileEntry));
        when(formInputRepositoryMock.findOne(formInputResponseFileEntryResource.getCompoundId().getFormInputId())).thenReturn(formInput);

        ServiceResult<FormInputResponse> result =
                service.deleteFormInputResponseFileUpload(formInputResponseFileEntryResource.getCompoundId());

        assertTrue(result.isSuccess());
        assertEquals(unlinkedFormInputFileEntry, result.getSuccessObject());
        assertNull(existingFormInputResponse.getFileEntry());
        verify(formInputResponseRepositoryMock, times(2)).findByApplicationIdAndFormInputId(456L, 123L);
        verify(formInputResponseRepositoryMock).save(existingFormInputResponse);
    }

    @Test
    public void deleteFormInputResponseFileUploadButFileServiceCallFails() {
        Supplier<InputStream> inputStreamSupplier = () -> null;

        //when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);
        when(formInputResponseRepositoryMock.findByApplicationIdAndFormInputId(456L, 123L)).thenReturn(existingFormInputResponses);
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));
        when(fileServiceMock.deleteFileIgnoreNotFound(999L)).thenReturn(serviceFailure(internalServerErrorError()));
        when(formInputRepositoryMock.findOne(formInputResponseFileEntryResource.getCompoundId().getFormInputId())).thenReturn(formInput);

        ServiceResult<FormInputResponse> result =
                service.deleteFormInputResponseFileUpload(formInputResponseFileEntryResource.getCompoundId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError()));
    }

    @Test
    public void deleteFormInputResponseFileUploadButUnableToFindFormInputResponse() {
        when(formInputResponseRepositoryMock.findByApplicationIdAndFormInputId(456L, 123L)).thenReturn(existingFormInputResponses);
        when(formInputRepositoryMock.findOne(formInputResponseFileEntryResource.getCompoundId().getFormInputId())).thenReturn(null);
        ServiceResult<FormInputResponse> result =
                service.deleteFormInputResponseFileUpload(formInputResponseFileEntryResource.getCompoundId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FormInput.class, 123L)));
    }

    @Test
    public void deleteFormInputResponseFileUploadButFileEntryNotFound() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(999L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);

        FileEntry existingFileEntry = newFileEntry().with(id(999L)).build();
        FormInputResponse existingFormInputResponse = newFormInputResponse().withFileEntry(existingFileEntry).build();

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withType(formInputType).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(singletonList(formInputLocal));

        when(fileServiceMock.deleteFileIgnoreNotFound(999L)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 999L)));
        when(formInputRepositoryMock.findOne(123L)).thenReturn(formInputLocal);
        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.getId())).thenReturn(serviceFailure(notFoundError(File.class, 999L)));

        ServiceResult<FormInputResponse> result = service.deleteFormInputResponseFileUpload(fileEntry.getCompoundId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FileEntry.class, 999L)));
    }

    @Test
    public void getFormInputResponseFileUpload() {

        FileEntry fileEntry = newFileEntry().with(id(321L)).build();
        FormInputResponse formInputResponse = newFormInputResponse().withFileEntry(fileEntry).build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withType(formInputType).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(singletonList(formInputLocal));
        when(formInputRepositoryMock.findOne(123L)).thenReturn(formInputLocal);

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(formInputResponse);
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));

        ServiceResult<FormInputResponseFileAndContents> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L));

        assertTrue(result.isSuccess());
        assertEquals(inputStreamSupplier, result.getSuccessObject().getContentsSupplier());

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(321L)).build();
        FormInputResponseFileEntryResource formInputResponseFile = result.getSuccessObject().getFormInputResponseFileEntry();

        assertEquals(fileEntryResource.getId(), formInputResponseFile.getFileEntryResource().getId());
        assertEquals(123L, formInputResponseFile.getCompoundId().getFormInputId());
        assertEquals(456L, formInputResponseFile.getCompoundId().getApplicationId());
        assertEquals(789L, formInputResponseFile.getCompoundId().getProcessRoleId());
    }

    @Test
    public void getFormInputResponseFileUploadButFileServiceCallFails() {

        FileEntry fileEntry = newFileEntry().build();
        FormInputResponse formInputResponse = newFormInputResponse().withFileEntry(fileEntry).build();

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withType(formInputType).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(singletonList(formInputLocal));
        when(formInputRepositoryMock.findOne(123L)).thenReturn(formInputLocal);

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(formInputResponse);
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(serviceFailure(internalServerErrorError()));

        ServiceResult<FormInputResponseFileAndContents> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L));

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError()));
    }

    @Test
    public void getFormInputResponseFileUploadButUnableToFindFormInputResponse() {
        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withType(formInputType).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(singletonList(formInputLocal));
        when(formInputRepositoryMock.findOne(123L)).thenReturn(formInputLocal);

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);

        ServiceResult<FormInputResponseFileAndContents> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L));

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FormInputResponse.class, 456L, 789L, 123L)));
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

        ProcessRole testProcessRole1 = new ProcessRole(0L, testUser1, testApplication1.getId(), new Role(), organisation1.getId());
        ProcessRole testProcessRole2 = new ProcessRole(1L, testUser1, testApplication2.getId(), new Role(), organisation1.getId());
        ProcessRole testProcessRole3 = new ProcessRole(2L, testUser2, testApplication2.getId(), new Role(), organisation2.getId());
        ProcessRole testProcessRole4 = new ProcessRole(3L, testUser2, testApplication3.getId(), new Role(), organisation2.getId());

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

        List<ApplicationResource> applicationsForUser1 = service.findByUserId(testUser1.getId()).getSuccessObject();
        assertEquals(2, applicationsForUser1.size());
        assertEquals(testApplication1Resource.getId(), applicationsForUser1.get(0).getId());
        assertEquals(testApplication2Resource.getId(), applicationsForUser1.get(1).getId());

        List<ApplicationResource> applicationsForUser2 = service.findByUserId(testUser2.getId()).getSuccessObject();
        assertEquals(2, applicationsForUser1.size());
        assertEquals(testApplication2Resource.getId(), applicationsForUser2.get(0).getId());
        assertEquals(testApplication3Resource.getId(), applicationsForUser2.get(1).getId());
    }

    @Test
    public void applicationControllerCanCreateApplication() throws Exception {
        Long competitionId = 1L;
        Long organisationId = 2L;
        Long userId = 3L;
        String roleName = LEADAPPLICANT.getName();
        Competition competition = newCompetition().with(id(1L)).build();
        Role role = newRole().with(name(roleName)).build();
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
        when(roleRepositoryMock.findOneByName(role.getName())).thenReturn(role);
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

        ApplicationResource created = service.createApplicationByApplicationNameForUserIdAndCompetitionId(applicationName, competitionId, userId).getSuccessObject();
        assertEquals(newApplication, created);
    }

    @Test
    public void notifyApplicantsByCompetition() throws Exception {
        Long competitionId = 1L;
        Long applicationOneId = 2L;
        Long applicationTwoId = 3L;
        Long applicationThreeId = 4L;

        Competition competition = newCompetition()
                .withName("Competition")
                .build();

        List<User> users = newUser()
                .withFirstName("John", "Jane", "Bob")
                .withLastName("Smith", "Jones", "Davies")
                .withEmailAddress("john@smith.com", "jane@jones.com", "bob@davie.com")
                .build(3);

        List<ProcessRole> processRoles = newProcessRole()
                .withUser(users.get(0), users.get(1), users.get(2))
                .withRole(newRole().withType(LEADAPPLICANT).withUrl("url").build())
                .build(3);

        List<Application> applications = newApplication()
                .withCompetition(competition)
                .withId(applicationOneId, applicationTwoId, applicationThreeId)
                .withName("App1", "App2", "App3")
                .build(3);

        applications.get(0).setProcessRoles(singletonList(processRoles.get(0)));
        applications.get(1).setProcessRoles(singletonList(processRoles.get(1)));
        applications.get(2).setProcessRoles(singletonList(processRoles.get(2)));

        processRoles.get(0).setApplicationId(applicationOneId);
        processRoles.get(1).setApplicationId(applicationTwoId);
        processRoles.get(2).setApplicationId(applicationThreeId);

        List<NotificationTarget> notificationTargets = asList(
                new ExternalUserNotificationTarget(users.get(0).getName(), users.get(0).getEmail()),
                new ExternalUserNotificationTarget(users.get(1).getName(), users.get(1).getEmail()),
                new ExternalUserNotificationTarget(users.get(2).getName(), users.get(2).getEmail())
        );

        List<EmailContent> emailContents = newEmailContentResource()
                .build(3);

        List<Notification> notifications = asList(
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(0)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(0).getName(),
                                "applicationName", applications.get(0).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL + "/" + processRoles.get(0).getRole().getUrl())
                ),
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(1)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(1).getName(),
                                "applicationName", applications.get(1).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL + "/" + processRoles.get(1).getRole().getUrl())
                ),
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(2)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(2).getName(),
                                "applicationName", applications.get(2).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL + "/" + processRoles.get(2).getRole().getUrl())
                )
        );

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, FUNDING_DECISIONS_MADE_STATUSES)).thenReturn(applications);

        when(applicationRepositoryMock.findOne(applicationOneId)).thenReturn(applications.get(0));
        when(applicationRepositoryMock.findOne(applicationTwoId)).thenReturn(applications.get(1));
        when(applicationRepositoryMock.findOne(applicationThreeId)).thenReturn(applications.get(2));

        when(notificationSenderMock.renderTemplates(Matchers.eq(notifications.get(0))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(0), emailContents.get(0))));
        when(notificationSenderMock.renderTemplates(Matchers.eq(notifications.get(1))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(1), emailContents.get(1))));
        when(notificationSenderMock.renderTemplates(Matchers.eq(notifications.get(2))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(2), emailContents.get(2))));

        when(notificationSenderMock.sendEmailWithContent(
                Matchers.eq(notifications.get(0)),
                Matchers.eq(notificationTargets.get(0)),
                Matchers.eq(emailContents.get(0))))
                .thenReturn(serviceSuccess(
                        singletonList(new EmailAddress(users.get(0).getEmail(), users.get(0).getName()))));
        when(notificationSenderMock.sendEmailWithContent(
                Matchers.eq(notifications.get(1)),
                Matchers.eq(notificationTargets.get(1)),
                Matchers.eq(emailContents.get(1))))
                .thenReturn(serviceSuccess(
                        singletonList(new EmailAddress(users.get(1).getEmail(), users.get(1).getName()))));
        when(notificationSenderMock.sendEmailWithContent(
                Matchers.eq(notifications.get(2)),
                Matchers.eq(notificationTargets.get(2)),
                Matchers.eq(emailContents.get(2))))
                .thenReturn(serviceSuccess(
                        singletonList(new EmailAddress(users.get(2).getEmail(), users.get(2).getName()))));

        ServiceResult<Void> result = service.notifyApplicantsByCompetition(competitionId);

        InOrder inOrder = inOrder(applicationRepositoryMock, notificationSenderMock);
        inOrder.verify(applicationRepositoryMock).findByCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, FUNDING_DECISIONS_MADE_STATUSES);

        inOrder.verify(applicationRepositoryMock).findOne(applicationOneId);
        inOrder.verify(notificationSenderMock).renderTemplates(notifications.get(0));
        inOrder.verify(notificationSenderMock)
                .sendEmailWithContent(notifications.get(0), notificationTargets.get(0), emailContents.get(0));

        inOrder.verify(applicationRepositoryMock).findOne(applicationTwoId);
        inOrder.verify(notificationSenderMock).renderTemplates(notifications.get(1));
        inOrder.verify(notificationSenderMock)
                .sendEmailWithContent(notifications.get(1), notificationTargets.get(1), emailContents.get(1));

        inOrder.verify(applicationRepositoryMock).findOne(applicationThreeId);
        inOrder.verify(notificationSenderMock).renderTemplates(notifications.get(2));
        inOrder.verify(notificationSenderMock)
                .sendEmailWithContent(notifications.get(2), notificationTargets.get(2), emailContents.get(2));

        inOrder.verifyNoMoreInteractions();

        assertTrue(result.isSuccess());
    }

    @Test
    public void notifyApplicantsByCompetition_oneFailure() throws Exception {
        Long competitionId = 1L;
        Long applicationOneId = 2L;
        Long applicationTwoId = 3L;
        Long applicationThreeId = 4L;

        Competition competition = newCompetition()
                .withName("Competition")
                .build();

        List<User> users = newUser()
                .withFirstName("John", "Jane", "Bob")
                .withLastName("Smith", "Jones", "Davies")
                .withEmailAddress("john@smith.com", "jane@jones.com", "bob@davie.com")
                .build(3);

        List<ProcessRole> processRoles = newProcessRole()
                .withUser(users.get(0), users.get(1), users.get(2))
                .withRole(newRole().withType(LEADAPPLICANT).withUrl("url").build())
                .build(3);

        List<Application> applications = newApplication()
                .withCompetition(competition)
                .withId(applicationOneId, applicationTwoId, applicationThreeId)
                .withName("App1", "App2", "App3")
                .build(3);

        applications.get(0).setProcessRoles(singletonList(processRoles.get(0)));
        applications.get(1).setProcessRoles(singletonList(processRoles.get(1)));
        applications.get(2).setProcessRoles(singletonList(processRoles.get(2)));

        processRoles.get(0).setApplicationId(applicationOneId);
        processRoles.get(1).setApplicationId(applicationTwoId);
        processRoles.get(2).setApplicationId(applicationThreeId);

        List<NotificationTarget> notificationTargets = asList(
                new ExternalUserNotificationTarget(users.get(0).getName(), users.get(0).getEmail()),
                new ExternalUserNotificationTarget(users.get(1).getName(), users.get(1).getEmail()),
                new ExternalUserNotificationTarget(users.get(2).getName(), users.get(2).getEmail())
        );

        List<EmailContent> emailContents = newEmailContentResource()
                .build(3);

        List<Notification> notifications = asList(
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(0)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(0).getName(),
                                "applicationName", applications.get(0).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL + "/" + processRoles.get(0).getRole().getUrl())
                ),
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(1)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(1).getName(),
                                "applicationName", applications.get(1).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL + "/" + processRoles.get(1).getRole().getUrl())
                ),
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(2)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(2).getName(),
                                "applicationName", applications.get(2).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL + "/" + processRoles.get(2).getRole().getUrl())
                )
        );

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, FUNDING_DECISIONS_MADE_STATUSES)).thenReturn(applications);

        when(applicationRepositoryMock.findOne(applicationOneId)).thenReturn(applications.get(0));
        when(applicationRepositoryMock.findOne(applicationTwoId)).thenReturn(applications.get(1));
        when(applicationRepositoryMock.findOne(applicationThreeId)).thenReturn(applications.get(2));

        when(notificationSenderMock.renderTemplates(Matchers.eq(notifications.get(0))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(0), emailContents.get(0))));
        when(notificationSenderMock.renderTemplates(Matchers.eq(notifications.get(1))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(1), emailContents.get(1))));
        when(notificationSenderMock.renderTemplates(Matchers.eq(notifications.get(2))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(2), emailContents.get(2))));

        when(notificationSenderMock.sendEmailWithContent(
                Matchers.eq(notifications.get(0)),
                Matchers.eq(notificationTargets.get(0)),
                Matchers.eq(emailContents.get(0))))
                .thenReturn(serviceSuccess(
                        singletonList(new EmailAddress(users.get(0).getEmail(), users.get(0).getName()))));
        when(notificationSenderMock.sendEmailWithContent(
                Matchers.eq(notifications.get(1)),
                Matchers.eq(notificationTargets.get(1)),
                Matchers.eq(emailContents.get(1))))
                .thenReturn(serviceSuccess(
                        singletonList(new EmailAddress(users.get(1).getEmail(), users.get(1).getName()))));
        when(notificationSenderMock.sendEmailWithContent(
                Matchers.eq(notifications.get(2)),
                Matchers.eq(notificationTargets.get(2)),
                Matchers.eq(emailContents.get(2))))
                .thenReturn(serviceFailure(new Error("error", INTERNAL_SERVER_ERROR)));

        ServiceResult<Void> result = service.notifyApplicantsByCompetition(competitionId);

        InOrder inOrder = inOrder(applicationRepositoryMock, notificationSenderMock);
        inOrder.verify(applicationRepositoryMock).findByCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, FUNDING_DECISIONS_MADE_STATUSES);

        inOrder.verify(applicationRepositoryMock).findOne(applicationOneId);
        inOrder.verify(notificationSenderMock).renderTemplates(notifications.get(0));
        inOrder.verify(notificationSenderMock)
                .sendEmailWithContent(notifications.get(0), notificationTargets.get(0), emailContents.get(0));

        inOrder.verify(applicationRepositoryMock).findOne(applicationTwoId);
        inOrder.verify(notificationSenderMock).renderTemplates(notifications.get(1));
        inOrder.verify(notificationSenderMock)
                .sendEmailWithContent(notifications.get(1), notificationTargets.get(1), emailContents.get(1));

        inOrder.verify(applicationRepositoryMock).findOne(applicationThreeId);
        inOrder.verify(notificationSenderMock).renderTemplates(notifications.get(2));
        inOrder.verify(notificationSenderMock)
                .sendEmailWithContent(notifications.get(2), notificationTargets.get(2), emailContents.get(2));

        inOrder.verifyNoMoreInteractions();

        assertTrue(result.isFailure());
        assertEquals(1, result.getErrors().size());
        assertEquals("error", result.getErrors().get(0).getErrorKey());
        assertEquals(INTERNAL_SERVER_ERROR, result.getErrors().get(0).getStatusCode());
    }

    @Test
    public void notifyApplicantsByCompetition_allFailure() throws Exception {
        Long competitionId = 1L;
        Long applicationOneId = 2L;
        Long applicationTwoId = 3L;
        Long applicationThreeId = 4L;

        Competition competition = newCompetition()
                .withName("Competition")
                .build();

        List<User> users = newUser()
                .withFirstName("John", "Jane", "Bob")
                .withLastName("Smith", "Jones", "Davies")
                .withEmailAddress("john@smith.com", "jane@jones.com", "bob@davie.com")
                .build(3);

        List<ProcessRole> processRoles = newProcessRole()
                .withUser(users.get(0), users.get(1), users.get(2))
                .withRole(newRole().withType(LEADAPPLICANT).withUrl("url").build())
                .build(3);

        List<Application> applications = newApplication()
                .withCompetition(competition)
                .withId(applicationOneId, applicationTwoId, applicationThreeId)
                .withName("App1", "App2", "App3")
                .build(3);

        applications.get(0).setProcessRoles(singletonList(processRoles.get(0)));
        applications.get(1).setProcessRoles(singletonList(processRoles.get(1)));
        applications.get(2).setProcessRoles(singletonList(processRoles.get(2)));

        processRoles.get(0).setApplicationId(applicationOneId);
        processRoles.get(1).setApplicationId(applicationTwoId);
        processRoles.get(2).setApplicationId(applicationThreeId);

        List<NotificationTarget> notificationTargets = asList(
                new ExternalUserNotificationTarget(users.get(0).getName(), users.get(0).getEmail()),
                new ExternalUserNotificationTarget(users.get(1).getName(), users.get(1).getEmail()),
                new ExternalUserNotificationTarget(users.get(2).getName(), users.get(2).getEmail())
        );

        List<EmailContent> emailContents = newEmailContentResource()
                .build(3);

        List<Notification> notifications = asList(
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(0)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(0).getName(),
                                "applicationName", applications.get(0).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL + "/" + processRoles.get(0).getRole().getUrl())
                ),
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(1)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(1).getName(),
                                "applicationName", applications.get(1).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL + "/" + processRoles.get(1).getRole().getUrl())
                ),
                new Notification(
                        systemNotificationSourceMock,
                        singletonList(notificationTargets.get(2)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(2).getName(),
                                "applicationName", applications.get(2).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", WEB_BASE_URL + "/" + processRoles.get(2).getRole().getUrl())
                )
        );

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, FUNDING_DECISIONS_MADE_STATUSES)).thenReturn(applications);

        when(applicationRepositoryMock.findOne(applicationOneId)).thenReturn(applications.get(0));
        when(applicationRepositoryMock.findOne(applicationTwoId)).thenReturn(applications.get(1));
        when(applicationRepositoryMock.findOne(applicationThreeId)).thenReturn(applications.get(2));

        when(notificationSenderMock.renderTemplates(Matchers.eq(notifications.get(0))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(0), emailContents.get(0))));
        when(notificationSenderMock.renderTemplates(Matchers.eq(notifications.get(1))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(1), emailContents.get(1))));
        when(notificationSenderMock.renderTemplates(Matchers.eq(notifications.get(2))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(2), emailContents.get(2))));

        when(notificationSenderMock.sendEmailWithContent(
                Matchers.eq(notifications.get(0)),
                Matchers.eq(notificationTargets.get(0)),
                Matchers.eq(emailContents.get(0))))
                .thenReturn(serviceFailure(new Error("error", INTERNAL_SERVER_ERROR)));
        when(notificationSenderMock.sendEmailWithContent(
                Matchers.eq(notifications.get(1)),
                Matchers.eq(notificationTargets.get(1)),
                Matchers.eq(emailContents.get(1))))
                .thenReturn(serviceFailure(new Error("error", INTERNAL_SERVER_ERROR)));
        when(notificationSenderMock.sendEmailWithContent(
                Matchers.eq(notifications.get(2)),
                Matchers.eq(notificationTargets.get(2)),
                Matchers.eq(emailContents.get(2))))
                .thenReturn(serviceFailure(new Error("error", INTERNAL_SERVER_ERROR)));

        ServiceResult<Void> result = service.notifyApplicantsByCompetition(competitionId);

        InOrder inOrder = inOrder(applicationRepositoryMock, notificationSenderMock);
        inOrder.verify(applicationRepositoryMock).findByCompetitionIdAndApplicationProcessActivityStateStateIn(competitionId, FUNDING_DECISIONS_MADE_STATUSES);

        inOrder.verify(applicationRepositoryMock).findOne(applicationOneId);
        inOrder.verify(notificationSenderMock).renderTemplates(notifications.get(0));
        inOrder.verify(notificationSenderMock)
                .sendEmailWithContent(notifications.get(0), notificationTargets.get(0), emailContents.get(0));

        inOrder.verify(applicationRepositoryMock).findOne(applicationTwoId);
        inOrder.verify(notificationSenderMock).renderTemplates(notifications.get(1));
        inOrder.verify(notificationSenderMock)
                .sendEmailWithContent(notifications.get(1), notificationTargets.get(1), emailContents.get(1));

        inOrder.verify(applicationRepositoryMock).findOne(applicationThreeId);
        inOrder.verify(notificationSenderMock).renderTemplates(notifications.get(2));
        inOrder.verify(notificationSenderMock)
                .sendEmailWithContent(notifications.get(2), notificationTargets.get(2), emailContents.get(2));

        inOrder.verifyNoMoreInteractions();

        assertTrue(result.isFailure());
        assertEquals(3, result.getErrors().size());
        assertEquals("error", result.getErrors().get(0).getErrorKey());
        assertEquals(INTERNAL_SERVER_ERROR, result.getErrors().get(0).getStatusCode());
        assertEquals("error", result.getErrors().get(1).getErrorKey());
        assertEquals(INTERNAL_SERVER_ERROR, result.getErrors().get(1).getStatusCode());
        assertEquals("error", result.getErrors().get(2).getErrorKey());
        assertEquals(INTERNAL_SERVER_ERROR, result.getErrors().get(2).getStatusCode());
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
    public void getApplicationReadyToSubmit() throws Exception {

        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org1.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org2.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org3.getId())).thenReturn(serviceSuccess(Boolean.TRUE));

        when(questionServiceMock.isMarkedAsComplete(leadAnswerQuestion, app.getId(), 0L)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(sectionServiceMock.childSectionsAreCompleteForAllOrganisations(null, app.getId(), null)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(applicationFinanceHandlerMock.getResearchParticipationPercentage(app.getId())).thenReturn(new BigDecimal("29"));

        ServiceResult<Boolean> result = service.applicationReadyForSubmit(app.getId());
        assertTrue(result.isSuccess());
        assertTrue(result.getSuccessObject() == Boolean.TRUE);
    }

    @Test
    public void applicationNotReadyToSubmitResearchParticipationTooHigh() throws Exception {

        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org1.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org2.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org3.getId())).thenReturn(serviceSuccess(Boolean.TRUE));

        when(questionServiceMock.isMarkedAsComplete(leadAnswerQuestion, app.getId(), 0L)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(sectionServiceMock.childSectionsAreCompleteForAllOrganisations(null, app.getId(), null)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(applicationFinanceHandlerMock.getResearchParticipationPercentage(app.getId())).thenReturn(new BigDecimal("31"));

        ServiceResult<Boolean> result = service.applicationReadyForSubmit(app.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccessObject());
    }

    @Test
    public void applicationNotReadyToSubmitProgressNotComplete() throws Exception {

        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org1.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org2.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org3.getId())).thenReturn(serviceSuccess(Boolean.FALSE));

        when(questionServiceMock.isMarkedAsComplete(leadAnswerQuestion, app.getId(), 0L)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(sectionServiceMock.childSectionsAreCompleteForAllOrganisations(null, app.getId(), null)).thenReturn(serviceSuccess(Boolean.TRUE));

        ServiceResult<Boolean> result = service.applicationReadyForSubmit(app.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccessObject());
    }

    @Test
    public void applicationNotReadyToSubmitChildSectionsNotComplete() throws Exception {

        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org1.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org2.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org3.getId())).thenReturn(serviceSuccess(Boolean.TRUE));

        when(questionServiceMock.isMarkedAsComplete(leadAnswerQuestion, app.getId(), 0L)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(sectionServiceMock.childSectionsAreCompleteForAllOrganisations(null, app.getId(), null)).thenReturn(serviceSuccess(Boolean.FALSE));

        ServiceResult<Boolean> result = service.applicationReadyForSubmit(app.getId());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccessObject());
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
    public void informIneligible() throws Exception {
        long applicationId = 1L;
        String subject = "subject";
        String content = "content";
        String email = "email@address.com";
        String firstName = "first";
        String lastName = "last";
        String fullName = String.format("%s %s", firstName, lastName);

        ApplicationIneligibleSendResource resource = newApplicationIneligibleSendResource()
                .withSubject(subject)
                .withContent(content)
                .build();

        User[] users = newUser()
                .withFirstName(firstName, "other")
                .withLastName(lastName, "other")
                .withEmailAddress(email, "other@email.com")
                .buildArray(2, User.class);

        ProcessRole[] processRoles = newProcessRole()
                .withUser(users)
                .withRole(LEADAPPLICANT, COLLABORATOR)
                .buildArray(2, ProcessRole.class);

        Application application = newApplication()
                .withId(applicationId)
                .withProcessRoles(processRoles)
                .build();

        Map<String, Object> expectedNotificationArguments = asMap(
                "subject", subject,
                "bodyPlain", content,
                "bodyHtml", content
        );

        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to = new ExternalUserNotificationTarget(fullName, email);
        Notification notification = new Notification(from, singletonList(to), ApplicationServiceImpl.Notifications.APPLICATION_INELIGIBLE, expectedNotificationArguments);

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(applicationWorkflowHandlerMock.informIneligible(application)).thenReturn(true);
        when(notificationSenderMock.sendNotification(notification)).thenReturn(serviceSuccess(notification));

        ServiceResult<Void> serviceResult = service.informIneligible(applicationId, resource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(applicationRepositoryMock, applicationWorkflowHandlerMock, notificationSenderMock);
        inOrder.verify(applicationRepositoryMock).findOne(applicationId);
        inOrder.verify(applicationWorkflowHandlerMock).informIneligible(application);
        inOrder.verify(applicationRepositoryMock).save(application);
        inOrder.verify(notificationSenderMock).sendNotification(notification);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void informIneligible_workflowError() throws Exception {
        long applicationId = 1L;
        String subject = "subject";
        String content = "content";

        ApplicationIneligibleSendResource resource = newApplicationIneligibleSendResource()
                .withSubject(subject)
                .withContent(content)
                .build();

        Application application = newApplication()
                .withId(applicationId)
                .build();

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(applicationWorkflowHandlerMock.informIneligible(application)).thenReturn(false);

        ServiceResult<Void> serviceResult = service.informIneligible(applicationId, resource);
        assertTrue(serviceResult.isFailure());
        assertEquals(APPLICATION_MUST_BE_INELIGIBLE.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());

        InOrder inOrder = inOrder(applicationRepositoryMock, applicationWorkflowHandlerMock, notificationSenderMock);
        inOrder.verify(applicationRepositoryMock).findOne(applicationId);
        inOrder.verify(applicationWorkflowHandlerMock).informIneligible(application);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void showApplicationTeam() {
        Role compAdmin = newRole(COMP_ADMIN).build();
        User user = newUser().withRoles(singleton(compAdmin)).build();
        when(userRepositoryMock.findOne(234L)).thenReturn(user);

        ServiceResult<Boolean> serviceResult = service.showApplicationTeam(123L, 234L);

        assertTrue(serviceResult.isSuccess());
        assertTrue(serviceResult.getSuccessObject());
    }

    @Test
    public void showApplicationTeamNoUser() {

        when(userRepositoryMock.findOne(234L)).thenReturn(null);
        ServiceResult<Boolean> serviceResult = service.showApplicationTeam(123L, 234L);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getErrors().get(0).getErrorKey().equals(GENERAL_NOT_FOUND.getErrorKey()));
    }
}
