package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.builder.ApplicationBuilder;
import com.worth.ifs.application.builder.QuestionBuilder;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.builder.CompetitionBuilder;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.resource.FileEntryResourceAssembler;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.notifications.resource.SystemNotificationSource;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserRoleType;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static com.worth.ifs.LambdaMatcher.lambdaMatches;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.ApplicationStatusBuilder.newApplicationStatus;
import static com.worth.ifs.application.constant.ApplicationStatusConstants.CREATED;
import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static com.worth.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static com.worth.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

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

    @Before
    public void setUp() throws Exception {
        question = QuestionBuilder.newQuestion().build();

        formInputType = new FormInputType(4L, "fileupload");

        formInput = newFormInput().withFormInputType(formInputType).build();
        formInput.setId(123L);
        formInput.setQuestion(question);
        question.setFormInputs(singletonList(formInput));

        fileEntryResource = newFileEntryResource().with(id(999L)).build();
        formInputResponseFileEntryResource = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);

        existingFileEntry = newFileEntry().with(id(999L)).build();
        existingFormInputResponse = newFormInputResponse().withFileEntry(existingFileEntry).build();
        existingFormInputResponses = singletonList(existingFormInputResponse);
        unlinkedFormInputFileEntry = newFormInputResponse().with(id(existingFormInputResponse.getId())).withFileEntry(null).build();
        final Competition openCompetition = newCompetition().withCompetitionStatus(CompetitionResource.Status.OPEN).build();
        openApplication = newApplication().withCompetition(openCompetition).build();

        when(applicationRepositoryMock.findOne(anyLong())).thenReturn(openApplication);
    }

    @Test
    public void testCreateApplicationByApplicationNameForUserIdAndCompetitionId() {

        Competition competition = newCompetition().build();
        User user = newUser().build();
        Organisation organisation = newOrganisation().with(name("testOrganisation")).build();
        Role leadApplicantRole = newRole().withType(LEADAPPLICANT).build();
        newProcessRole().withUser(user).withRole(leadApplicantRole).withOrganisation(organisation).build();
        ApplicationStatus applicationStatus = newApplicationStatus().withName(CREATED).build();

        ApplicationResource applicationResource = newApplicationResource().build();

        when(applicationStatusRepositoryMock.findByName(CREATED.getName())).thenReturn(singletonList(applicationStatus));
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(roleRepositoryMock.findOneByName(leadApplicantRole.getName())).thenReturn(leadApplicantRole);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        Supplier<Application> applicationExpectations = () -> argThat(lambdaMatches(created -> {
            assertEquals("testApplication", created.getName());
            assertEquals(applicationStatus.getId(), created.getApplicationStatus().getId());
            assertEquals(Long.valueOf(3), created.getDurationInMonths());
            assertEquals(competition.getId(), created.getCompetition().getId());
            assertNull(created.getStartDate());

            assertEquals(1, created.getProcessRoles().size());
            ProcessRole createdProcessRole = created.getProcessRoles().get(0);
            assertNull(createdProcessRole.getId());
            assertNull(createdProcessRole.getApplication().getId());
            assertEquals(organisation.getId(), createdProcessRole.getOrganisation().getId());
            assertEquals(leadApplicantRole.getId(), createdProcessRole.getRole().getId());
            assertEquals(user.getId(), createdProcessRole.getUser().getId());

            return true;
        }));

        when(applicationRepositoryMock.save(applicationExpectations.get())).thenReturn(ApplicationBuilder.newApplication().build());
        when(applicationMapperMock.mapToResource(applicationExpectations.get())).thenReturn(applicationResource);

        ApplicationResource created =
                service.createApplicationByApplicationNameForUserIdAndCompetitionId("testApplication", competition.getId(), user.getId()).getSuccessObject();

        verify(applicationRepositoryMock).save(isA(Application.class));
        verify(processRoleRepositoryMock).save(isA(ProcessRole.class));
        assertEquals(applicationResource, created);
    }

    @Test
    public void testCreateFormInputResponseFileUpload() {

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
    public void testCreateFormInputResponseFileUploadButReplaceIfFileAlreadyExistsForFormInputResponse() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(987L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry alreadyExistingFileEntry = newFileEntry().with(id(987L)).build();
        FormInputResponse existingFormInputResponseWithLinkedFileEntry = newFormInputResponse().withFileEntry(alreadyExistingFileEntry).build();

        File fileFound = mock(File.class);

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withFormInputType(formInputType).build();
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
    public void testCreateFormInputResponseFileUploadButFileServiceCallFails() {

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
    public void testCreateFormInputResponseFileUploadWithAlreadyExistingFormInputResponse() {

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
    public void testCreateFormInputResponseFileUploadButProcessRoleNotFound() {

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
    public void testCreateFormInputResponseFileUploadButFormInputNotFound() {

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
    public void testCreateFormInputResponseFileUploadButApplicationNotFound() {

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
    public void testUpdateFormInputResponseFileUpload() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(999L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry existingFileEntry = newFileEntry().with(id(999L)).build();

        FormInputResponse existingFormInputResponse = newFormInputResponse().withFileEntry(existingFileEntry).build();

        File fileFound = mock(File.class);

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withFormInputType(formInputType).build();
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
    public void testUpdateFormInputResponseFileUploadButFileServiceCallFails() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource formInputFileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry fileEntry = FileEntryResourceAssembler.valueOf(fileEntryResource);
        FormInputResponse existingFormInputResponse =
                newFormInputResponse().withFileEntry(fileEntry).build();

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withFormInputType(formInputType).build();
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
    public void testDeleteFormInputResponseFileUpload() {

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
    public void testDeleteFormInputResponseFileUploadButFileServiceCallFails() {
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
    public void testDeleteFormInputResponseFileUploadButUnableToFindFormInputResponse() {
        when(formInputResponseRepositoryMock.findByApplicationIdAndFormInputId(456L, 123L)).thenReturn(existingFormInputResponses);
        when(formInputRepositoryMock.findOne(formInputResponseFileEntryResource.getCompoundId().getFormInputId())).thenReturn(null);
        ServiceResult<FormInputResponse> result =
                service.deleteFormInputResponseFileUpload(formInputResponseFileEntryResource.getCompoundId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FormInput.class, 123L)));
    }

    @Test
    public void testDeleteFormInputResponseFileUploadButFileEntryNotFound() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(999L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);

        FileEntry existingFileEntry = newFileEntry().with(id(999L)).build();
        FormInputResponse existingFormInputResponse = newFormInputResponse().withFileEntry(existingFileEntry).build();

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withFormInputType(formInputType).build();
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
    public void testGetFormInputResponseFileUpload() {

        FileEntry fileEntry = newFileEntry().with(id(321L)).build();
        FormInputResponse formInputResponse = newFormInputResponse().withFileEntry(fileEntry).build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withFormInputType(formInputType).build();
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
    public void testGetFormInputResponseFileUploadButFileServiceCallFails() {

        FileEntry fileEntry = newFileEntry().build();
        FormInputResponse formInputResponse = newFormInputResponse().withFileEntry(fileEntry).build();

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withFormInputType(formInputType).build();
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
    public void testGetFormInputResponseFileUploadButUnableToFindFormInputResponse() {
        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withFormInputType(formInputType).build();
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
        User testUser2 = new User(2L, "test", "User2",  "email2@email.nl", "testToken456def", null, "my-uid");
        User testUser1 = new User(1L, "test", "User1",  "email1@email.nl", "testToken123abc", null, "my-uid");

        Application testApplication1 = new Application(null, "testApplication1Name", null, null, 1L);
        Application testApplication2 = new Application(null, "testApplication2Name", null, null, 2L);
        Application testApplication3 = new Application(null, "testApplication3Name", null, null, 3L);

        ApplicationResource testApplication1Resource = newApplicationResource().with(id(1L)).withName("testApplication1Name").build();
        ApplicationResource testApplication2Resource = newApplicationResource().with(id(2L)).withName("testApplication2Name").build();
        ApplicationResource testApplication3Resource = newApplicationResource().with(id(3L)).withName("testApplication3Name").build();

        Organisation organisation1 = new Organisation(1L, "test organisation 1");
        Organisation organisation2 = new Organisation(2L, "test organisation 2");

        ProcessRole testProcessRole1 = new ProcessRole(0L, testUser1, testApplication1, new Role(), organisation1);
        ProcessRole testProcessRole2 = new ProcessRole(1L, testUser1, testApplication2, new Role(), organisation1);
        ProcessRole testProcessRole3 = new ProcessRole(2L, testUser2, testApplication2, new Role(), organisation2);
        ProcessRole testProcessRole4 = new ProcessRole(3L, testUser2, testApplication3, new Role(), organisation2);

        when(userRepositoryMock.findOne(1L)).thenReturn(testUser1);
        when(userRepositoryMock.findOne(2L)).thenReturn(testUser2);

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
        ProcessRole processRole = newProcessRole().withOrganisation(organisation).build();
        User user = newUser().with(id(userId)).withOrganisations(asList(organisation)).withProcessRoles(asList(processRole)).build();
        ApplicationStatus applicationStatus = newApplicationStatus().withName(ApplicationStatusConstants.CREATED.getName()).build();

        String applicationName = "testApplication";

        ApplicationResource newApplication = newApplicationResource().build();

        when(applicationStatusRepositoryMock.findByName(applicationStatus.getName())).thenReturn(singletonList(applicationStatus));
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(roleRepositoryMock.findOneByName(role.getName())).thenReturn(role);
        when(userRepositoryMock.findOne(userId)).thenReturn(user);

        Supplier<Application> applicationExpectations = () -> argThat(lambdaMatches(created -> {
            assertEquals(applicationName, created.getName());
            assertEquals(applicationStatus, created.getApplicationStatus());
            assertEquals(competitionId, created.getCompetition().getId());
            return true;
        }));

        when(applicationRepositoryMock.save(applicationExpectations.get())).thenReturn(ApplicationBuilder.newApplication().build());
        when(applicationMapperMock.mapToResource(applicationExpectations.get())).thenReturn(newApplication);

        ApplicationResource created = service.createApplicationByApplicationNameForUserIdAndCompetitionId(applicationName, competitionId, userId).getSuccessObject();
        assertEquals(newApplication, created);
    }
}
