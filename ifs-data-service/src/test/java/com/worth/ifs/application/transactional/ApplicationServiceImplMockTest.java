package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.transactional.ServiceSuccess;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.util.Either;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

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
import static com.worth.ifs.application.transactional.ApplicationServiceImpl.ServiceFailures.UNABLE_TO_CREATE_FILE;
import static com.worth.ifs.application.transactional.ApplicationServiceImpl.ServiceFailures.UNABLE_TO_FIND_FILE;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.file.domain.builders.FileEntryBuilder.newFileEntry;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static com.worth.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static com.worth.ifs.transactional.BaseTransactionalService.Failures.*;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.domain.UserRoleType.LEADAPPLICANT;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;
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

    @Test
    public void testCreateApplicationByApplicationNameForUserIdAndCompetitionId() {

        Competition competition = newCompetition().build();
        User user = newUser().build();
        Organisation organisation = newOrganisation().with(name("testOrganisation")).build();
        Role leadApplicantRole = newRole().withType(LEADAPPLICANT).build();
        newProcessRole().withUser(user).withRole(leadApplicantRole).withOrganisation(organisation).build();
        ApplicationStatus applicationStatus = newApplicationStatus().withName(CREATED).build();

        when(applicationStatusRepositoryMock.findByName(CREATED.getName())).thenReturn(Collections.singletonList(applicationStatus));
        when(competitionsRepositoryMock.findOne(competition.getId())).thenReturn(competition);
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
                thenReturn(right(new ServiceSuccess(Pair.of(fileFound, newFileEntry))));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);
        when(processRoleRepositoryMock.findOne(789L)).thenReturn(newProcessRole().build());
        when(formInputRepositoryMock.findOne(123L)).thenReturn(newFormInput().build());
        when(applicationRepositoryMock.findOne(456L)).thenReturn(newApplication().build());

        Either<ServiceFailure, ServiceSuccess<Pair<File, FormInputResponseFileEntryResource>>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isRight());
        Pair<File, FormInputResponseFileEntryResource> resultParts = result.getRight().getResult();
        assertEquals(fileFound, resultParts.getKey());
        assertEquals(Long.valueOf(999), resultParts.getValue().getFileEntryResource().getId());

        verify(formInputResponseRepositoryMock).findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L);
    }

    @Test
    public void testCreateFormInputResponseFileUploadButFileServiceCallFails() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(left(ServiceFailure.error("no files for you...")));

        Either<ServiceFailure, ServiceSuccess<Pair<File, FormInputResponseFileEntryResource>>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is("no files for you..."));
    }

    @Test
    public void testCreateFormInputResponseFileUploadWithAlreadyExistingFormInputResponse() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(right(new ServiceSuccess(Pair.of(fileFound, newFileEntry))));

        FormInputResponse existingFormInputResponse = newFormInputResponse().build();
        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);

        Either<ServiceFailure, ServiceSuccess<Pair<File, FormInputResponseFileEntryResource>>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isRight());
        Pair<File, FormInputResponseFileEntryResource> resultParts = result.getRight().getResult();
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
                thenReturn(right(new ServiceSuccess(Pair.of(fileFound, newFileEntry))));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);
        when(processRoleRepositoryMock.findOne(789L)).thenReturn(null);

        Either<ServiceFailure, ServiceSuccess<Pair<File, FormInputResponseFileEntryResource>>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(PROCESS_ROLE_NOT_FOUND));
    }

    @Test
    public void testCreateFormInputResponseFileUploadButFormInputNotFound() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(right(new ServiceSuccess(Pair.of(fileFound, newFileEntry))));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);
        when(processRoleRepositoryMock.findOne(789L)).thenReturn(newProcessRole().build());
        when(formInputRepositoryMock.findOne(123L)).thenReturn(null);

        Either<ServiceFailure, ServiceSuccess<Pair<File, FormInputResponseFileEntryResource>>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(FORM_INPUT_NOT_FOUND));
    }

    @Test
    public void testCreateFormInputResponseFileUploadButApplicationNotFound() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(right(new ServiceSuccess(Pair.of(fileFound, newFileEntry))));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);
        when(processRoleRepositoryMock.findOne(789L)).thenReturn(newProcessRole().build());
        when(formInputRepositoryMock.findOne(123L)).thenReturn(newFormInput().build());
        when(applicationRepositoryMock.findOne(456L)).thenReturn(null);

        Either<ServiceFailure, ServiceSuccess<Pair<File, FormInputResponseFileEntryResource>>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(APPLICATION_NOT_FOUND));
    }

    @Test
    public void testCreateFormInputResponseFileUploadExistingFormInputResponseSaveThrowingExceptionHandledGracefully() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(right(new ServiceSuccess(Pair.of(fileFound, newFileEntry))));

        FormInputResponse existingFormInputResponse = newFormInputResponse().build();
        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);

        when(formInputResponseRepositoryMock.save(existingFormInputResponse)).thenThrow(new RuntimeException("Surprise!"));

        Either<ServiceFailure, ServiceSuccess<Pair<File, FormInputResponseFileEntryResource>>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(UNABLE_TO_CREATE_FILE));
    }

    @Test
    public void testCreateFormInputResponseFileUploadNewFormInputResponseSaveThrowingExceptionHandledGracefully() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(right(new ServiceSuccess(Pair.of(fileFound, newFileEntry))));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);
        when(processRoleRepositoryMock.findOne(789L)).thenReturn(newProcessRole().build());
        when(formInputRepositoryMock.findOne(123L)).thenReturn(newFormInput().build());
        when(applicationRepositoryMock.findOne(456L)).thenReturn(newApplication().build());

        when(formInputResponseRepositoryMock.save(isA(FormInputResponse.class))).thenThrow(new RuntimeException("Surprise!"));

        Either<ServiceFailure, ServiceSuccess<Pair<File, FormInputResponseFileEntryResource>>> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(UNABLE_TO_CREATE_FILE));
    }

    @Test
    public void testGetFormInputResponseFileUpload() {

        FileEntry fileEntry = newFileEntry().with(id(321L)).build();
        FormInputResponse formInputResponse = newFormInputResponse().withFileEntry(fileEntry).build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(formInputResponse);
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(right(new ServiceSuccess(inputStreamSupplier)));

        Either<ServiceFailure, ServiceSuccess<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>>> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L));

        assertTrue(result.isRight());
        assertEquals(inputStreamSupplier, result.getRight().getResult().getValue());

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(321L)).build();
        FormInputResponseFileEntryResource formInputResponseFile = result.getRight().getResult().getKey();

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
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(left(ServiceFailure.error("no files for you...")));

        Either<ServiceFailure, ServiceSuccess<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>>> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L));

        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is("no files for you..."));
    }

    @Test
    public void testGetFormInputResponseFileUploadButUnexpectedExceptionThrownAndHandledGracefully() {

        FileEntry fileEntry = newFileEntry().build();
        FormInputResponse formInputResponse = newFormInputResponse().withFileEntry(fileEntry).build();

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(formInputResponse);
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenThrow(new RuntimeException("not so fast!"));

        Either<ServiceFailure, ServiceSuccess<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>>> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L));

        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(UNABLE_TO_FIND_FILE));
    }


    @Test
    public void testGetFormInputResponseFileUploadButUnableToFindFormInputResponse() {

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);

        Either<ServiceFailure, ServiceSuccess<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>>> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L));

        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(FORM_INPUT_RESPONSE_NOT_FOUND));
    }

}
