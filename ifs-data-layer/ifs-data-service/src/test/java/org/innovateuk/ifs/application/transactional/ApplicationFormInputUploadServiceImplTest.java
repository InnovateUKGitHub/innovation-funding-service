package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.builder.QuestionBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryId;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.resource.FileEntryResourceAssembler;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.FormInputResponseRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.File;
import java.io.InputStream;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ApplicationFormInputUploadServiceImplTest {
    @Mock
    private FileService fileServiceMock;

    @Mock
    private FormInputRepository formInputRepositoryMock;

    @Mock
    private FormInputResponseRepository formInputResponseRepositoryMock;

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @InjectMocks
    private ApplicationFormInputUploadService service;

    private Application openApplication;
    private FileEntry existingFileEntry;
    private FormInputResponse existingFormInputResponse;
    private FormInputResponse unlinkedFormInputFileEntry;

    private FileEntryResource fileEntryResource;
    private FormInputResponseFileEntryResource formInputResponseFileEntryResource;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        final Competition openCompetition = newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build();
        openApplication = newApplication().withCompetition(openCompetition).build();

        existingFileEntry = newFileEntry().with(id(999L)).build();
        existingFormInputResponse = newFormInputResponse().withFileEntry(existingFileEntry).build();
        unlinkedFormInputFileEntry = newFormInputResponse().with(id(existingFormInputResponse.getId())).withFileEntry(null).build();


        fileEntryResource = newFileEntryResource().with(id(999L)).build();
        formInputResponseFileEntryResource = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);
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
        FormInputResponseFileEntryResource resultParts = result.getSuccess();
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
        FormInput formInputLocal = newFormInput().withType(FormInputType.FILEUPLOAD).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(singletonList(formInputLocal));

        when(fileServiceMock.deleteFileIgnoreNotFound(alreadyExistingFileEntry.getId())).thenReturn(serviceSuccess(alreadyExistingFileEntry));

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).thenReturn(serviceSuccess(Pair.of(fileFound, alreadyExistingFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponseWithLinkedFileEntry);
        when(formInputResponseRepositoryMock.save(existingFormInputResponseWithLinkedFileEntry)).thenReturn(existingFormInputResponseWithLinkedFileEntry);
        when(processRoleRepositoryMock.findOne(789L)).thenReturn(newProcessRole().build());
        when(formInputRepositoryMock.findOne(123L)).thenReturn(formInputLocal);
        when(applicationRepositoryMock.findOne(456L)).thenReturn(openApplication);

        when(fileServiceMock.getFileByFileEntryId(987L)).thenReturn(serviceSuccess(inputStreamSupplier));

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.createFormInputResponseFileUpload(fileEntry, inputStreamSupplier);

        FormInputResponseFileEntryResource resultParts = result.getSuccess();
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
        FormInputResponseFileEntryResource resultParts = result.getSuccess();
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
        FormInput formInputLocal = newFormInput().withType(FormInputType.FILEUPLOAD).build();
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
        FormInput formInputLocal = newFormInput().withType(FormInputType.FILEUPLOAD).build();
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

        when(formInputResponseRepositoryMock.findByApplicationIdAndFormInputId(456L, 123L)).thenReturn(singletonList
                (existingFormInputResponse));
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));
        when(formInputResponseRepositoryMock.save(existingFormInputResponse)).thenReturn(unlinkedFormInputFileEntry);
        when(fileServiceMock.deleteFileIgnoreNotFound(999L)).thenReturn(serviceSuccess(existingFileEntry));
        when(formInputRepositoryMock.findOne(formInputResponseFileEntryResource.getCompoundId().getFormInputId())).thenReturn
                (newFormInput().build());

        ServiceResult<FormInputResponse> result =
                service.deleteFormInputResponseFileUpload(formInputResponseFileEntryResource.getCompoundId());

        assertTrue(result.isSuccess());
        assertEquals(unlinkedFormInputFileEntry, result.getSuccess());
        assertNull(existingFormInputResponse.getFileEntry());
        verify(formInputResponseRepositoryMock, times(2)).findByApplicationIdAndFormInputId(456L, 123L);
        verify(formInputResponseRepositoryMock).save(existingFormInputResponse);
    }

    @Test
    public void deleteFormInputResponseFileUploadButFileServiceCallFails() {
        Supplier<InputStream> inputStreamSupplier = () -> null;

        //when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);
        when(formInputResponseRepositoryMock.findByApplicationIdAndFormInputId(456L, 123L)).thenReturn(singletonList(existingFormInputResponse));
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));
        when(fileServiceMock.deleteFileIgnoreNotFound(999L)).thenReturn(serviceFailure(internalServerErrorError()));
        when(formInputRepositoryMock.findOne(formInputResponseFileEntryResource.getCompoundId().getFormInputId())).thenReturn
                (newFormInput().withType(FormInputType.FILEUPLOAD).build());

        ServiceResult<FormInputResponse> result =
                service.deleteFormInputResponseFileUpload(formInputResponseFileEntryResource.getCompoundId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError()));
    }

    @Test
    public void deleteFormInputResponseFileUploadButUnableToFindFormInputResponse() {
        when(formInputResponseRepositoryMock.findByApplicationIdAndFormInputId(456L, 123L)).thenReturn(singletonList
                (existingFormInputResponse));
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
        FormInput formInputLocal = newFormInput().withType(FormInputType.FILEUPLOAD).build();
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
        FormInput formInputLocal = newFormInput().withType(FormInputType.FILEUPLOAD).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(singletonList(formInputLocal));
        when(formInputRepositoryMock.findOne(123L)).thenReturn(formInputLocal);

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(formInputResponse);
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));

        ServiceResult<FormInputResponseFileAndContents> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L));

        assertTrue(result.isSuccess());
        assertEquals(inputStreamSupplier, result.getSuccess().getContentsSupplier());

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(321L)).build();
        FormInputResponseFileEntryResource formInputResponseFile = result.getSuccess().getFormInputResponseFileEntry();

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
        FormInput formInputLocal = newFormInput().withType(FormInputType.FILEUPLOAD).build();
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
        FormInput formInputLocal = newFormInput().withType(FormInputType.FILEUPLOAD).build();
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
}