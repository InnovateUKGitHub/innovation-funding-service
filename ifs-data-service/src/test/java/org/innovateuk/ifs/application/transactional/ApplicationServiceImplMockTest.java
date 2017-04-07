package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.builder.ApplicationBuilder;
import org.innovateuk.ifs.application.builder.QuestionBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationStatus;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryId;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.resource.FileEntryResourceAssembler;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.io.File;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
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

    private Application openApplication;

    @Override
    protected ApplicationService supplyServiceUnderTest() {
        return new ApplicationServiceImpl();
    }

    @Mock
    private SystemNotificationSource systemNotificationSourceMock;

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
    }

    @Test
    public void createApplicationByApplicationNameForUserIdAndCompetitionId() {

        Competition competition = newCompetition().build();
        User user = newUser().build();
        Organisation organisation = newOrganisation().with(name("testOrganisation")).withId(organisationId).build();
        Role leadApplicantRole = newRole().withType(LEADAPPLICANT).build();
        ProcessRole processRole = newProcessRole().withUser(user).withRole(leadApplicantRole).withOrganisationId(organisation.getId()).build();
        ApplicationStatus applicationStatus = ApplicationStatus.CREATED;

        Application application = ApplicationBuilder.newApplication().
                withId(1L).
                withName("testApplication").
                withApplicationStatus(applicationStatus).
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
            assertEquals(applicationStatus, created.getApplicationStatus());
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

        when(fileServiceMock.deleteFile(alreadyExistingFileEntry.getId())).thenReturn(serviceSuccess(alreadyExistingFileEntry));

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
        when(fileServiceMock.deleteFile(999L)).thenReturn(serviceSuccess(existingFileEntry));
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
        when(fileServiceMock.deleteFile(999L)).thenReturn(serviceFailure(internalServerErrorError()));
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

        when(formInputRepositoryMock.findOne(123L)).thenReturn(formInputLocal);
        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.getId())).thenReturn(serviceFailure(notFoundError(File.class, 999L)));

        ServiceResult<FormInputResponse> result = service.deleteFormInputResponseFileUpload(fileEntry.getCompoundId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(File.class, 999L)));
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

        Application testApplication1 = new Application(null, "testApplication1Name", null, null, 1L);
        Application testApplication2 = new Application(null, "testApplication2Name", null, null, 2L);
        Application testApplication3 = new Application(null, "testApplication3Name", null, null, 3L);

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
        String roleName = UserRoleType.LEADAPPLICANT.getName();
        Competition competition = CompetitionBuilder.newCompetition().with(id(1L)).build();
        Role role = newRole().with(name(roleName)).build();
        Organisation organisation = newOrganisation().with(id(organisationId)).build();
        User user = newUser().with(id(userId)).build();
        ApplicationStatus applicationStatus = ApplicationStatus.CREATED;

        String applicationName = "testApplication";

        Application application = ApplicationBuilder.newApplication().
                withId(1L).
                withName(applicationName).
                withApplicationStatus(applicationStatus).
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
            assertEquals(applicationStatus, created.getApplicationStatus());
            assertEquals(competitionId, created.getCompetition().getId());
            return true;
        }));

        when(applicationMapperMock.mapToResource(applicationExpectations.get())).thenReturn(newApplication);

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
                        null,
                        singletonList(notificationTargets.get(0)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(0).getName(),
                                "applicationName", applications.get(0).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", processRoles.get(0).getRole().getUrl())
                ),
                new Notification(
                        null,
                        singletonList(notificationTargets.get(1)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(1).getName(),
                                "applicationName", applications.get(1).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", processRoles.get(1).getRole().getUrl())
                ),
                new Notification(
                        null,
                        singletonList(notificationTargets.get(2)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(2).getName(),
                                "applicationName", applications.get(2).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", processRoles.get(2).getRole().getUrl())
                )
        );

        when(applicationRepositoryMock.findByCompetitionId(competitionId)).thenReturn(applications);

        when(applicationRepositoryMock.findOne(applicationOneId)).thenReturn(applications.get(0));
        when(applicationRepositoryMock.findOne(applicationTwoId)).thenReturn(applications.get(1));
        when(applicationRepositoryMock.findOne(applicationThreeId)).thenReturn(applications.get(2));

        when(notificationSender.renderTemplates(Matchers.eq(notifications.get(0))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(0), emailContents.get(0))));
        when(notificationSender.renderTemplates(Matchers.eq(notifications.get(1))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(1), emailContents.get(1))));
        when(notificationSender.renderTemplates(Matchers.eq(notifications.get(2))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(2), emailContents.get(2))));

        when(notificationSender.sendEmailWithContent(
                Matchers.eq(notifications.get(0)),
                Matchers.eq(notificationTargets.get(0)),
                Matchers.eq(emailContents.get(0))))
                .thenReturn(serviceSuccess(
                        singletonList(new EmailAddress(users.get(0).getEmail(), users.get(0).getName()))));
        when(notificationSender.sendEmailWithContent(
                Matchers.eq(notifications.get(1)),
                Matchers.eq(notificationTargets.get(1)),
                Matchers.eq(emailContents.get(1))))
                .thenReturn(serviceSuccess(
                        singletonList(new EmailAddress(users.get(1).getEmail(), users.get(1).getName()))));
        when(notificationSender.sendEmailWithContent(
                Matchers.eq(notifications.get(2)),
                Matchers.eq(notificationTargets.get(2)),
                Matchers.eq(emailContents.get(2))))
                .thenReturn(serviceSuccess(
                        singletonList(new EmailAddress(users.get(2).getEmail(), users.get(2).getName()))));

        ServiceResult<Void> result = service.notifyApplicantsByCompetition(competitionId);

        InOrder inOrder = inOrder(applicationRepositoryMock, notificationSender);
        inOrder.verify(applicationRepositoryMock).findByCompetitionId(competitionId);

        inOrder.verify(applicationRepositoryMock).findOne(applicationOneId);
        inOrder.verify(notificationSender).renderTemplates(notifications.get(0));
        inOrder.verify(notificationSender)
                .sendEmailWithContent(notifications.get(0), notificationTargets.get(0), emailContents.get(0));

        inOrder.verify(applicationRepositoryMock).findOne(applicationTwoId);
        inOrder.verify(notificationSender).renderTemplates(notifications.get(1));
        inOrder.verify(notificationSender)
                .sendEmailWithContent(notifications.get(1), notificationTargets.get(1), emailContents.get(1));

        inOrder.verify(applicationRepositoryMock).findOne(applicationThreeId);
        inOrder.verify(notificationSender).renderTemplates(notifications.get(2));
        inOrder.verify(notificationSender)
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
                        null,
                        singletonList(notificationTargets.get(0)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(0).getName(),
                                "applicationName", applications.get(0).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", processRoles.get(0).getRole().getUrl())
                ),
                new Notification(
                        null,
                        singletonList(notificationTargets.get(1)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(1).getName(),
                                "applicationName", applications.get(1).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", processRoles.get(1).getRole().getUrl())
                ),
                new Notification(
                        null,
                        singletonList(notificationTargets.get(2)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(2).getName(),
                                "applicationName", applications.get(2).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", processRoles.get(2).getRole().getUrl())
                )
        );

        when(applicationRepositoryMock.findByCompetitionId(competitionId)).thenReturn(applications);

        when(applicationRepositoryMock.findOne(applicationOneId)).thenReturn(applications.get(0));
        when(applicationRepositoryMock.findOne(applicationTwoId)).thenReturn(applications.get(1));
        when(applicationRepositoryMock.findOne(applicationThreeId)).thenReturn(applications.get(2));

        when(notificationSender.renderTemplates(Matchers.eq(notifications.get(0))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(0), emailContents.get(0))));
        when(notificationSender.renderTemplates(Matchers.eq(notifications.get(1))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(1), emailContents.get(1))));
        when(notificationSender.renderTemplates(Matchers.eq(notifications.get(2))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(2), emailContents.get(2))));

        when(notificationSender.sendEmailWithContent(
                Matchers.eq(notifications.get(0)),
                Matchers.eq(notificationTargets.get(0)),
                Matchers.eq(emailContents.get(0))))
                .thenReturn(serviceSuccess(
                        singletonList(new EmailAddress(users.get(0).getEmail(), users.get(0).getName()))));
        when(notificationSender.sendEmailWithContent(
                Matchers.eq(notifications.get(1)),
                Matchers.eq(notificationTargets.get(1)),
                Matchers.eq(emailContents.get(1))))
                .thenReturn(serviceSuccess(
                        singletonList(new EmailAddress(users.get(1).getEmail(), users.get(1).getName()))));
        when(notificationSender.sendEmailWithContent(
                Matchers.eq(notifications.get(2)),
                Matchers.eq(notificationTargets.get(2)),
                Matchers.eq(emailContents.get(2))))
                .thenReturn(serviceFailure(new Error("error", INTERNAL_SERVER_ERROR)));

        ServiceResult<Void> result = service.notifyApplicantsByCompetition(competitionId);

        InOrder inOrder = inOrder(applicationRepositoryMock, notificationSender);
        inOrder.verify(applicationRepositoryMock).findByCompetitionId(competitionId);

        inOrder.verify(applicationRepositoryMock).findOne(applicationOneId);
        inOrder.verify(notificationSender).renderTemplates(notifications.get(0));
        inOrder.verify(notificationSender)
                .sendEmailWithContent(notifications.get(0), notificationTargets.get(0), emailContents.get(0));

        inOrder.verify(applicationRepositoryMock).findOne(applicationTwoId);
        inOrder.verify(notificationSender).renderTemplates(notifications.get(1));
        inOrder.verify(notificationSender)
                .sendEmailWithContent(notifications.get(1), notificationTargets.get(1), emailContents.get(1));

        inOrder.verify(applicationRepositoryMock).findOne(applicationThreeId);
        inOrder.verify(notificationSender).renderTemplates(notifications.get(2));
        inOrder.verify(notificationSender)
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
                        null,
                        singletonList(notificationTargets.get(0)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(0).getName(),
                                "applicationName", applications.get(0).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", processRoles.get(0).getRole().getUrl())
                ),
                new Notification(
                        null,
                        singletonList(notificationTargets.get(1)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(1).getName(),
                                "applicationName", applications.get(1).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", processRoles.get(1).getRole().getUrl())
                ),
                new Notification(
                        null,
                        singletonList(notificationTargets.get(2)),
                        ApplicationServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                        asMap("name", users.get(2).getName(),
                                "applicationName", applications.get(2).getName(),
                                "competitionName", competition.getName(),
                                "dashboardUrl", processRoles.get(2).getRole().getUrl())
                )
        );

        when(applicationRepositoryMock.findByCompetitionId(competitionId)).thenReturn(applications);

        when(applicationRepositoryMock.findOne(applicationOneId)).thenReturn(applications.get(0));
        when(applicationRepositoryMock.findOne(applicationTwoId)).thenReturn(applications.get(1));
        when(applicationRepositoryMock.findOne(applicationThreeId)).thenReturn(applications.get(2));

        when(notificationSender.renderTemplates(Matchers.eq(notifications.get(0))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(0), emailContents.get(0))));
        when(notificationSender.renderTemplates(Matchers.eq(notifications.get(1))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(1), emailContents.get(1))));
        when(notificationSender.renderTemplates(Matchers.eq(notifications.get(2))))
                .thenReturn(serviceSuccess(asMap(notificationTargets.get(2), emailContents.get(2))));

        when(notificationSender.sendEmailWithContent(
                Matchers.eq(notifications.get(0)),
                Matchers.eq(notificationTargets.get(0)),
                Matchers.eq(emailContents.get(0))))
                .thenReturn(serviceFailure(new Error("error", INTERNAL_SERVER_ERROR)));
        when(notificationSender.sendEmailWithContent(
                Matchers.eq(notifications.get(1)),
                Matchers.eq(notificationTargets.get(1)),
                Matchers.eq(emailContents.get(1))))
                .thenReturn(serviceFailure(new Error("error", INTERNAL_SERVER_ERROR)));
        when(notificationSender.sendEmailWithContent(
                Matchers.eq(notifications.get(2)),
                Matchers.eq(notificationTargets.get(2)),
                Matchers.eq(emailContents.get(2))))
                .thenReturn(serviceFailure(new Error("error", INTERNAL_SERVER_ERROR)));

        ServiceResult<Void> result = service.notifyApplicantsByCompetition(competitionId);

        InOrder inOrder = inOrder(applicationRepositoryMock, notificationSender);
        inOrder.verify(applicationRepositoryMock).findByCompetitionId(competitionId);

        inOrder.verify(applicationRepositoryMock).findOne(applicationOneId);
        inOrder.verify(notificationSender).renderTemplates(notifications.get(0));
        inOrder.verify(notificationSender)
                .sendEmailWithContent(notifications.get(0), notificationTargets.get(0), emailContents.get(0));

        inOrder.verify(applicationRepositoryMock).findOne(applicationTwoId);
        inOrder.verify(notificationSender).renderTemplates(notifications.get(1));
        inOrder.verify(notificationSender)
                .sendEmailWithContent(notifications.get(1), notificationTargets.get(1), emailContents.get(1));

        inOrder.verify(applicationRepositoryMock).findOne(applicationThreeId);
        inOrder.verify(notificationSender).renderTemplates(notifications.get(2));
        inOrder.verify(notificationSender)
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
}
