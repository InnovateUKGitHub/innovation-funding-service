package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryId;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.form.builder.QuestionBuilder;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Optional.of;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_ALREADY_UPLOADED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.*;
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

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @InjectMocks
    private ApplicationFormInputUploadService service = new ApplicationFormInputUploadServiceImpl();

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
        initMocks(this);
        orgType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        org1 = newOrganisation().withOrganisationType(orgType).withId(234L).build();
        org2 = newOrganisation().withId(345L).build();
        org3 = newOrganisation().withId(456L).build();

        multiAnswerQuestion = newQuestion().withMarksAsCompleteEnabled(Boolean.TRUE).withMultipleStatuses(Boolean.TRUE).withId(123L).build();
        leadAnswerQuestion = newQuestion().withMarksAsCompleteEnabled(Boolean.TRUE).withMultipleStatuses(Boolean.FALSE).withId(321L).build();

        roles = newProcessRole().withRole(Role.LEADAPPLICANT, Role.APPLICANT, Role.COLLABORATOR).withOrganisationId(234L, 345L, 456L).build(3).toArray(new ProcessRole[0]);
        section = newSection().withQuestions(Arrays.asList(multiAnswerQuestion, leadAnswerQuestion)).build();
        comp = newCompetition().withSections(Arrays.asList(section)).withMaxResearchRatio(30).build();
        app = newApplication().withCompetition(comp).withProcessRoles(roles).build();

        question = QuestionBuilder.newQuestion().build();

        formInputType = FormInputType.FILEUPLOAD;

        formInput = newFormInput().withType(formInputType).withWordCount(2).build();
        formInput.setId(123L);
        formInput.setQuestion(question);
        question.setFormInputs(newArrayList(formInput));

        fileEntryResource = newFileEntryResource().with(id(999L)).build();
        formInputResponseFileEntryResource = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L, 999L);

        existingFileEntry = newArrayList(newFileEntry().with(id(999L)).build());
        existingFormInputResponse = newFormInputResponse().withFileEntries(existingFileEntry).withApplication(app).withFormInputs(formInput).build();
        existingFormInputResponses = newArrayList(existingFormInputResponse);
        unlinkedFormInputFileEntry = newFormInputResponse().with(id(existingFormInputResponse.getId())).withApplication(app).withFileEntries(emptyList()).build();
        final Competition openCompetition = newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build();
        openApplication = newApplication().withCompetition(openCompetition).build();

        when(applicationRepositoryMock.findById(anyLong())).thenReturn(Optional.of(openApplication));

        when(applicationRepositoryMock.findById(app.getId())).thenReturn(Optional.of(app));
        when(organisationRepositoryMock.findById(234L)).thenReturn(Optional.of(org1));
        when(organisationRepositoryMock.findById(345L)).thenReturn(Optional.of(org2));
        when(organisationRepositoryMock.findById(456L)).thenReturn(Optional.of(org3));
    }

    @Test
    public void createFormInputResponseFileUpload() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L, 999L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceSuccess(Pair.of(fileFound, newFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);
        when(processRoleRepositoryMock.findById(789L)).thenReturn(Optional.of(newProcessRole().build()));
        when(formInputRepositoryMock.findById(123L)).thenReturn(Optional.of(newFormInput().withQuestion(newQuestion().withMultipleStatuses(true).build()).build()));
        when(applicationRepositoryMock.findById(456L)).thenReturn(Optional.of(openApplication));

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.uploadResponse(fileEntry, inputStreamSupplier);

        assertTrue(result.isSuccess());
        FormInputResponseFileEntryResource resultParts = result.getSuccess();
        assertEquals(Long.valueOf(999), resultParts.getFileEntryResource().getId());

        verify(formInputResponseRepositoryMock).findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L);
    }

    @Test
    public void createFormInputResponseFileUploadMultipleFiles() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(987L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L, 999L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry alreadyExistingFileEntry = newFileEntry().with(id(987L)).build();
        FormInputResponse existingFormInputResponseWithLinkedFileEntry = newFormInputResponse().withFileEntries(newArrayList(alreadyExistingFileEntry)).withFormInputs(formInput).build();

        File fileFound = mock(File.class);

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withType(FormInputType.FILEUPLOAD).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(newArrayList(formInputLocal));

        when(fileServiceMock.deleteFileIgnoreNotFound(alreadyExistingFileEntry.getId())).thenReturn(serviceSuccess(alreadyExistingFileEntry));

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).thenReturn(serviceSuccess(Pair.of(fileFound, alreadyExistingFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponseWithLinkedFileEntry);
        when(formInputResponseRepositoryMock.save(existingFormInputResponseWithLinkedFileEntry)).thenReturn(existingFormInputResponseWithLinkedFileEntry);
        when(processRoleRepositoryMock.findById(789L)).thenReturn(Optional.of(newProcessRole().build()));
        when(formInputRepositoryMock.findById(123L)).thenReturn(Optional.of(formInputLocal));
        when(applicationRepositoryMock.findById(456L)).thenReturn(Optional.of(openApplication));

        when(fileServiceMock.getFileByFileEntryId(987L)).thenReturn(serviceSuccess(inputStreamSupplier));

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.uploadResponse(fileEntry, inputStreamSupplier);

        assertTrue(result.isSuccess());

        verify(formInputResponseRepositoryMock, times(1)).findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L);
    }

    @Test
    public void createFormInputResponseFileUploadButMaximumFilesAlready() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(987L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L, 999L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry alreadyExistingFileEntry = newFileEntry().with(id(987L)).build();
        FormInputResponse existingFormInputResponseWithLinkedFileEntry = newFormInputResponse().withFileEntries(newArrayList(alreadyExistingFileEntry, alreadyExistingFileEntry, alreadyExistingFileEntry)).withFormInputs(formInput).build();

        File fileFound = mock(File.class);

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withType(FormInputType.FILEUPLOAD).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(newArrayList(formInputLocal));

        when(fileServiceMock.deleteFileIgnoreNotFound(alreadyExistingFileEntry.getId())).thenReturn(serviceSuccess(alreadyExistingFileEntry));

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).thenReturn(serviceSuccess(Pair.of(fileFound, alreadyExistingFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponseWithLinkedFileEntry);
        when(formInputResponseRepositoryMock.save(existingFormInputResponseWithLinkedFileEntry)).thenReturn(existingFormInputResponseWithLinkedFileEntry);
        when(processRoleRepositoryMock.findById(789L)).thenReturn(Optional.of(newProcessRole().build()));
        when(formInputRepositoryMock.findById(123L)).thenReturn(Optional.of(formInputLocal));
        when(applicationRepositoryMock.findById(456L)).thenReturn(Optional.of(openApplication));

        when(fileServiceMock.getFileByFileEntryId(987L)).thenReturn(serviceSuccess(inputStreamSupplier));

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.uploadResponse(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_ALREADY_UPLOADED));

        verify(formInputResponseRepositoryMock, times(1)).findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L);
    }

    @Test
    public void createFormInputResponseFileUploadTemplate() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(987L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L, 999L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withType(FormInputType.TEMPLATE_DOCUMENT).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(newArrayList(formInputLocal));


        FileEntry alreadyExistingFileEntry = newFileEntry().with(id(987L)).build();
        FormInputResponse existingFormInputResponseWithLinkedFileEntry = newFormInputResponse().withFileEntries(newArrayList(alreadyExistingFileEntry, alreadyExistingFileEntry, alreadyExistingFileEntry)).withFormInputs(formInputLocal).build();

        when(fileServiceMock.deleteFileIgnoreNotFound(alreadyExistingFileEntry.getId())).thenReturn(serviceSuccess(alreadyExistingFileEntry));

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).thenReturn(serviceSuccess(Pair.of(fileFound, alreadyExistingFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponseWithLinkedFileEntry);
        when(formInputResponseRepositoryMock.save(existingFormInputResponseWithLinkedFileEntry)).thenReturn(existingFormInputResponseWithLinkedFileEntry);
        when(processRoleRepositoryMock.findById(789L)).thenReturn(Optional.of(newProcessRole().build()));
        when(formInputRepositoryMock.findById(123L)).thenReturn(Optional.of(formInputLocal));
        when(applicationRepositoryMock.findById(456L)).thenReturn(Optional.of(openApplication));

        when(fileServiceMock.getFileByFileEntryId(987L)).thenReturn(serviceSuccess(inputStreamSupplier));

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.uploadResponse(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_ALREADY_UPLOADED));

        verify(formInputResponseRepositoryMock, times(1)).findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L);
    }

    @Test
    public void createFormInputResponseFileUploadButFileServiceCallFails() {
        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L, 999L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceFailure(internalServerErrorError()));

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.uploadResponse(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError()));
    }

    @Test
    public void createFormInputResponseFileUploadWithAlreadyExistingFormInputResponse() {
        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L, 987L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceSuccess(Pair.of(fileFound, newFileEntry)));

        FormInputResponse existingFormInputResponse = newFormInputResponse().withFormInputs(formInput).build();
        when(formInputResponseRepositoryMock.findOneByApplicationIdAndFormInputId(456L, 123L)).thenReturn(of(existingFormInputResponse));

        when(formInputRepositoryMock.findById(123L)).thenReturn(Optional.of(newFormInput().withQuestion(newQuestion().withMultipleStatuses(false).build()).build()));
        when(processRoleRepositoryMock.findById(789L)).thenReturn(Optional.of(newProcessRole().build()));

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.uploadResponse(fileEntry, inputStreamSupplier);

        assertTrue(result.isSuccess());
        FormInputResponseFileEntryResource resultParts = result.getSuccess();
        assertEquals(Long.valueOf(999), resultParts.getFileEntryResource().getId());
        assertEquals(newFileEntry, existingFormInputResponse.getFileEntries().get(0));
    }

    @Test
    public void createFormInputResponseFileUploadButProcessRoleNotFound() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L, 999L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceSuccess(Pair.of(fileFound, newFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);
        when(processRoleRepositoryMock.findById(789L)).thenReturn(Optional.empty());

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.uploadResponse(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(ProcessRole.class, 789L)));
    }

    @Test
    public void createFormInputResponseFileUploadButFormInputNotFound() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L, 999L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceSuccess(Pair.of(fileFound, newFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);
        when(processRoleRepositoryMock.findById(789L)).thenReturn(Optional.of(newProcessRole().build()));
        when(formInputRepositoryMock.findById(123L)).thenReturn(Optional.empty());

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.uploadResponse(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FormInput.class, 123L)));
    }

    @Test
    public void createFormInputResponseFileUploadButApplicationNotFound() {

        FileEntryResource fileEntryResource = newFileEntryResource().build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L, 999L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        File fileFound = mock(File.class);
        FileEntry newFileEntry = newFileEntry().with(id(999L)).build();

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).
                thenReturn(serviceSuccess(Pair.of(fileFound, newFileEntry)));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);
        when(processRoleRepositoryMock.findById(789L)).thenReturn(Optional.of(newProcessRole().build()));
        when(formInputRepositoryMock.findById(123L)).thenReturn(Optional.of(newFormInput().build()));
        when(applicationRepositoryMock.findById(456L)).thenReturn(Optional.empty());

        ServiceResult<FormInputResponseFileEntryResource> result =
                service.uploadResponse(fileEntry, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Application.class, 456L)));
    }

    @Test
    public void deleteFormInputResponseFileUpload() {

        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(formInputResponseRepositoryMock.findOneByApplicationIdAndFormInputId(456L, 123L)).thenReturn(of(existingFormInputResponse));
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.get(0).getId())).thenReturn(serviceSuccess(inputStreamSupplier));
        when(formInputResponseRepositoryMock.save(existingFormInputResponse)).thenReturn(unlinkedFormInputFileEntry);
        when(fileServiceMock.deleteFileIgnoreNotFound(999L)).thenReturn(serviceSuccess(existingFileEntry.get(0)));
        when(formInputRepositoryMock.findById(formInputResponseFileEntryResource.getCompoundId().getFormInputId())).thenReturn
                (Optional.of(newFormInput().withQuestion(question).build()));

        ServiceResult<FormInputResponse> result =
                service.deleteFormInputResponseFileUpload(formInputResponseFileEntryResource.getCompoundId());

        assertTrue(result.isSuccess());
        assertEquals(unlinkedFormInputFileEntry, result.getSuccess());
        assertTrue(existingFormInputResponse.getFileEntries().isEmpty());
        verify(formInputResponseRepositoryMock, times(2)).findOneByApplicationIdAndFormInputId(456L, 123L);
        verify(formInputResponseRepositoryMock).save(existingFormInputResponse);
    }

    @Test
    public void deleteFormInputResponseFileUploadButFileServiceCallFails() {
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(formInputResponseRepositoryMock.findOneByApplicationIdAndFormInputId(456L, 123L)).thenReturn(of(existingFormInputResponse));
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.get(0).getId())).thenReturn(serviceSuccess(inputStreamSupplier));
        when(fileServiceMock.deleteFileIgnoreNotFound(999L)).thenReturn(serviceFailure(internalServerErrorError()));
        when(formInputRepositoryMock.findById(formInputResponseFileEntryResource.getCompoundId().getFormInputId())).thenReturn(
                Optional.of(newFormInput().withQuestion(question).withType(FormInputType.FILEUPLOAD).build()));
        when(applicationRepositoryMock.findById(formInputResponseFileEntryResource.getCompoundId().getApplicationId())).thenReturn(
                Optional.of(newApplication().withCompetition(newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build()).build()));

        ServiceResult<FormInputResponse> result =
                service.deleteFormInputResponseFileUpload(formInputResponseFileEntryResource.getCompoundId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError()));
    }

    @Test
    public void deleteFormInputResponseFileUploadButUnableToFindFormInputResponse() {
        when(formInputResponseRepositoryMock.findByApplicationIdAndFormInputId(456L, 123L)).thenReturn(newArrayList
                (existingFormInputResponse));
        when(formInputRepositoryMock.findById(formInputResponseFileEntryResource.getCompoundId().getFormInputId())).thenReturn(Optional.empty());
        ServiceResult<FormInputResponse> result =
                service.deleteFormInputResponseFileUpload(formInputResponseFileEntryResource.getCompoundId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FormInput.class, 123L)));
    }

    @Test
    public void deleteFormInputResponseFileUploadButFileEntryNotFound() {

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(999L)).build();
        FormInputResponseFileEntryResource fileEntry = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L, 999L);

        FileEntry existingFileEntry = newFileEntry().with(id(999L)).build();
        FormInputResponse existingFormInputResponse = newFormInputResponse().withFileEntries(newArrayList(existingFileEntry)).build();

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withType(FormInputType.FILEUPLOAD).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(newArrayList(formInputLocal));

        when(fileServiceMock.deleteFileIgnoreNotFound(999L)).thenReturn(serviceFailure(notFoundError(FileEntry.class, 999L)));
        when(formInputRepositoryMock.findById(123L)).thenReturn(Optional.of(formInputLocal));
        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(existingFormInputResponse);
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.getId())).thenReturn(serviceFailure(notFoundError(File.class, 999L)));

        ServiceResult<FormInputResponse> result = service.deleteFormInputResponseFileUpload(fileEntry.getCompoundId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FileEntry.class, 999L)));
    }

    @Test
    public void getFormInputResponseFileUpload() {

        FileEntry fileEntry = newFileEntry().with(id(999L)).build();
        FormInputResponse formInputResponse = newFormInputResponse().withFileEntries(newArrayList(fileEntry)).build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withType(FormInputType.FILEUPLOAD).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(newArrayList(formInputLocal));
        when(formInputRepositoryMock.findById(123L)).thenReturn(Optional.of(formInputLocal));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(formInputResponse);
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));

        ServiceResult<FormInputResponseFileAndContents> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L, 999L));

        assertTrue(result.isSuccess());
        assertEquals(inputStreamSupplier, result.getSuccess().getContentsSupplier());

        FileEntryResource fileEntryResource = newFileEntryResource().with(id(999L)).build();
        FormInputResponseFileEntryResource formInputResponseFile = result.getSuccess().getFormInputResponseFileEntry();

        assertEquals(fileEntryResource.getId(), formInputResponseFile.getFileEntryResource().getId());
        assertEquals(123L, formInputResponseFile.getCompoundId().getFormInputId());
        assertEquals(456L, formInputResponseFile.getCompoundId().getApplicationId());
        assertEquals(789L, formInputResponseFile.getCompoundId().getProcessRoleId());
    }

    @Test
    public void getFormInputResponseFileUploadButFileServiceCallFails() {

        FileEntry fileEntry = newFileEntry().withId(999L).build();
        FormInputResponse formInputResponse = newFormInputResponse().withFileEntries(newArrayList(fileEntry)).withFormInputs(formInput).build();

        Question question = QuestionBuilder.newQuestion().build();
        question.setMultipleStatuses(true);
        FormInput formInputLocal = newFormInput().withType(FormInputType.FILEUPLOAD).build();
        formInputLocal.setId(123L);
        formInputLocal.setQuestion(question);
        question.setFormInputs(newArrayList(formInputLocal));
        when(formInputRepositoryMock.findById(123L)).thenReturn(Optional.of(formInputLocal));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(formInputResponse);
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(serviceFailure(internalServerErrorError()));

        ServiceResult<FormInputResponseFileAndContents> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L, 999L));

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
        question.setFormInputs(newArrayList(formInputLocal));
        when(formInputRepositoryMock.findById(123L)).thenReturn(Optional.of(formInputLocal));

        when(formInputResponseRepositoryMock.findByApplicationIdAndUpdatedByIdAndFormInputId(456L, 789L, 123L)).thenReturn(null);

        ServiceResult<FormInputResponseFileAndContents> result =
                service.getFormInputResponseFileUpload(new FormInputResponseFileEntryId(123L, 456L, 789L, 999L));

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FormInputResponse.class, 456L, 789L, 123L)));
    }
}