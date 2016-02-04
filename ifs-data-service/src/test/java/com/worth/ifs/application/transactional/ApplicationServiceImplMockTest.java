package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.resource.FileEntryResourceAssembler;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.notifications.resource.SystemNotificationSource;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.function.Supplier;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.ApplicationStatusBuilder.newApplicationStatus;
import static com.worth.ifs.application.constant.ApplicationStatusConstants.CREATED;
import static com.worth.ifs.transactional.ServiceFailureKeys.*;
import static com.worth.ifs.commons.error.Errors.internalServerErrorError;
import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.file.domain.builders.FileEntryBuilder.newFileEntry;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static com.worth.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.domain.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link ApplicationServiceImpl}
 */
public class ApplicationServiceImplMockTest extends BaseServiceUnitTest<ApplicationService> {

    @Override
    protected ApplicationService supplyServiceUnderTest() {
        return new ApplicationServiceImpl();
    }

    @Mock
    private SystemNotificationSource systemNotificationSourceMock;

    @Test
    public void testCreateApplicationByApplicationNameForUserIdAndCompetitionId() {

        Competition competition = newCompetition().build();
        User user = newUser().build();
        Organisation organisation = newOrganisation().with(name("testOrganisation")).build();
        Role leadApplicantRole = newRole().withType(LEADAPPLICANT).build();
        newProcessRole().withUser(user).withRole(leadApplicantRole).withOrganisation(organisation).build();
        ApplicationStatus applicationStatus = newApplicationStatus().withName(CREATED).build();

        when(applicationStatusRepositoryMock.findByName(CREATED.getName())).thenReturn(Collections.singletonList(applicationStatus));
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(roleRepositoryMock.findByName(leadApplicantRole.getName())).thenReturn(Collections.singletonList(leadApplicantRole));
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        Application created =
                service.createApplicationByApplicationNameForUserIdAndCompetitionId("testApplication", competition.getId(), user.getId());

        verify(applicationRepositoryMock).save(isA(Application.class));
        verify(processRoleRepositoryMock).save(isA(ProcessRole.class));

        assertEquals("testApplication", created.getName());
        assertEquals(applicationStatus.getId(), created.getApplicationStatus().getId());
        assertEquals(Long.valueOf(3), created.getDurationInMonths());
        assertEquals(competition.getId(), created.getCompetition().getId());
        assertEquals(LocalDate.now(), created.getStartDate());

        assertEquals(1, created.getProcessRoles().size());
        ProcessRole createdProcessRole = created.getProcessRoles().get(0);
        assertNull(createdProcessRole.getId());
        assertNull(createdProcessRole.getApplication().getId());
        assertEquals(organisation.getId(), createdProcessRole.getOrganisation().getId());
        assertEquals(leadApplicantRole.getId(), createdProcessRole.getRole().getId());
        assertEquals(user.getId(), createdProcessRole.getUser().getId());
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
        when(applicationRepositoryMock.findOne(456L)).thenReturn(newApplication().build());

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isSuccess());
        Pair<File, FormInputResponseFileEntryResource> resultParts = result.getSuccessObject();
        assertEquals(fileFound, resultParts.getKey());
        assertEquals(Long.valueOf(999), resultParts.getValue().getFileEntryResource().getId());

        verify(formInputResponseRepositoryMock).findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L);
    }

    @Test
    public void testCreateFormInputResponseFileUploadButFileAlreadyExistsForFormInputResponse() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(987L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry alreadyExistingFileEntry = newFileEntry().with(id(987L)).build();
        FormInputResponse existingFormInputResponseWithLinkedFileEntry = newFormInputResponse().withFileEntry(alreadyExistingFileEntry).build();

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponseWithLinkedFileEntry);
        when(processRoleRepositoryMock.findOne(789L)).thenReturn(newProcessRole().build());
        when(formInputRepositoryMock.findOne(123L)).thenReturn(newFormInput().build());
        when(applicationRepositoryMock.findOne(456L)).thenReturn(newApplication().build());

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_FILE_ALREADY_LINKED_TO_FORM_INPUT_RESPONSE, "987"));
    }

    @Test
    public void testCreateFormInputResponseFileUploadButFileServiceCallFails() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceFailure(internalServerErrorError("no files for you...")));

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError("no files for you...")));
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

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isSuccess());
        Pair<File, FormInputResponseFileEntryResource> resultParts = result.getSuccessObject();
        assertEquals(fileFound, resultParts.getKey());
        assertEquals(Long.valueOf(999), resultParts.getValue().getFileEntryResource().getId());

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

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> result =
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

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> result =
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

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Application.class, 456L)));
    }

    @Test
    public void testCreateFormInputResponseFileUploadExistingFormInputResponseSaveThrowingExceptionHandledGracefully() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceSuccess(Pair.of(fileFound, newFileEntry)));

        FormInputResponse existingFormInputResponse = newFormInputResponse().build();
        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);

        when(formInputResponseRepositoryMock.save(existingFormInputResponse)).thenThrow(new RuntimeException("Surprise!"));

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_UNABLE_TO_CREATE_FILE));
    }

    @Test
    public void testCreateFormInputResponseFileUploadNewFormInputResponseSaveThrowingExceptionHandledGracefully() {

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
        when(applicationRepositoryMock.findOne(456L)).thenReturn(newApplication().build());

        when(formInputResponseRepositoryMock.save(isA(FormInputResponse.class))).thenThrow(new RuntimeException("Surprise!"));

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_UNABLE_TO_CREATE_FILE));
    }

    @Test
    public void testUpdateFormInputResponseFileUpload() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(999L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry existingFileEntry = newFileEntry().with(id(999L)).build();

        FormInputResponse existingFormInputResponse = newFormInputResponse().withFileEntry(existingFileEntry).build();

        File fileFound = mock(File.class);

        when(fileServiceMock.updateFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceSuccess(Pair.of(fileFound, existingFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> result =
                service.updateFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isSuccess());
        Pair<File, FormInputResponseFileEntryResource> resultParts = result.getSuccessObject();
        assertEquals(fileFound, resultParts.getKey());
        assertEquals(Long.valueOf(999), resultParts.getValue().getFileEntryResource().getId());

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

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));

        when(fileServiceMock.updateFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceFailure(internalServerErrorError("no files for you...")));

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> result =
                service.updateFormInputResponseFileUpload(formInputFileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError("no files for you...")));
    }

    @Test
    public void testDeleteFormInputResponseFileUpload() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(999L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry existingFileEntry = newFileEntry().with(id(999L)).build();
        FormInputResponse existingFormInputResponse = newFormInputResponse().withFileEntry(existingFileEntry).build();
        FormInputResponse unlinkedFormInputFileEntry = newFormInputResponse().with(id(existingFormInputResponse.getId())).withFileEntry(null).build();

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));

        when(formInputResponseRepositoryMock.save(existingFormInputResponse)).thenReturn(unlinkedFormInputFileEntry);
        when(fileServiceMock.deleteFile(999L)).thenReturn(serviceSuccess(existingFileEntry));

        ServiceResult<FormInputResponse> result =
                service.deleteFormInputResponseFileUpload(fileEntry.getCompoundId());

        assertTrue(result.isSuccess());
        assertEquals(unlinkedFormInputFileEntry, result.getSuccessObject());
        assertNull(existingFormInputResponse.getFileEntry());
        verify(formInputResponseRepositoryMock, times(2)).findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L);
        verify(formInputResponseRepositoryMock).save(existingFormInputResponse);
    }

    @Test
    public void testDeleteFormInputResponseFileUploadButFileServiceCallFails() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(999L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry existingFileEntry = newFileEntry().with(id(999L)).build();
        FormInputResponse existingFormInputResponse = newFormInputResponse().withFileEntry(existingFileEntry).build();

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));
        when(fileServiceMock.deleteFile(999L)).thenReturn(serviceFailure(internalServerErrorError("no deleting for you...")));

        ServiceResult<FormInputResponse> result =
                service.deleteFormInputResponseFileUpload(fileEntry.getCompoundId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError("no deleting for you...")));
    }

    @Test
    public void testDeleteFormInputResponseFileUploadButUnableToFindFormInputResponse() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(999L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);

        ServiceResult<FormInputResponse> result =
                service.deleteFormInputResponseFileUpload(fileEntry.getCompoundId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FormInputResponse.class, 456L, 789L, 123L)));
    }

    @Test
    public void testDeleteFormInputResponseFileUploadButFileEntryNotFound() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(999L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);

        FileEntry existingFileEntry = newFileEntry().with(id(999L)).build();
        FormInputResponse existingFormInputResponse = newFormInputResponse().withFileEntry(existingFileEntry).build();

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.getId())).thenReturn(serviceFailure(notFoundError(File.class, 999L)));

        ServiceResult<FormInputResponse> result = service.deleteFormInputResponseFileUpload(fileEntry.getCompoundId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(File.class, 999L)));
    }

    @Test
    public void testDeleteFormInputResponseFileUploadButUnexpectedExceptionOccursAndHandledGracefully() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(999L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry existingFileEntry = newFileEntry().with(id(999L)).build();
        FormInputResponse existingFormInputResponse = newFormInputResponse().withFileEntry(existingFileEntry).build();

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));
        when(fileServiceMock.deleteFile(999L)).thenThrow(new IllegalArgumentException("No deleting files today..."));

        ServiceResult<FormInputResponse> result =
                service.deleteFormInputResponseFileUpload(fileEntry.getCompoundId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_UNABLE_TO_DELETE_FILE));
    }


    @Test
    public void testGetFormInputResponseFileUpload() {

        FileEntry fileEntry = newFileEntry().with(id(321L)).build();
        FormInputResponse formInputResponse = newFormInputResponse().withFileEntry(fileEntry).build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(formInputResponse);
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));

        ServiceResult<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L));

        assertTrue(result.isSuccess());
        assertEquals(inputStreamSupplier, result.getSuccessObject().getValue());

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(321L)).build();
        FormInputResponseFileEntryResource formInputResponseFile = result.getSuccessObject().getKey();

        assertEquals(fileEntryResource.getId(), formInputResponseFile.getFileEntryResource().getId());
        assertEquals(123L, formInputResponseFile.getCompoundId().getFormInputId());
        assertEquals(456L, formInputResponseFile.getCompoundId().getApplicationId());
        assertEquals(789L, formInputResponseFile.getCompoundId().getProcessRoleId());
    }

    @Test
    public void testGetFormInputResponseFileUploadButFileServiceCallFails() {

        FileEntry fileEntry = newFileEntry().build();
        FormInputResponse formInputResponse = newFormInputResponse().withFileEntry(fileEntry).build();

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(formInputResponse);
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(serviceFailure(internalServerErrorError("no files for you...")));

        ServiceResult<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L));

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError("no files for you...")));
    }

    @Test
    public void testGetFormInputResponseFileUploadButUnexpectedExceptionThrownAndHandledGracefully() {

        FileEntry fileEntry = newFileEntry().build();
        FormInputResponse formInputResponse = newFormInputResponse().withFileEntry(fileEntry).build();

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(formInputResponse);
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenThrow(new RuntimeException("not so fast!"));

        ServiceResult<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L));

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FileEntry.class, 123L, 456L, 789L)));
    }


    @Test
    public void testGetFormInputResponseFileUploadButUnableToFindFormInputResponse() {

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);

        ServiceResult<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L));

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FormInputResponse.class, 456L, 789L, 123L)));
    }
}
