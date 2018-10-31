package org.innovateuk.ifs.project.documents.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competitionsetup.repository.ProjectDocumentConfigRepository;
import org.innovateuk.ifs.file.builder.FileEntryResourceBuilder;
import org.innovateuk.ifs.file.builder.FileTypeBuilder;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.domain.FileType;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentDecision;
import org.innovateuk.ifs.project.documents.builder.ProjectDocumentBuilder;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.documents.repository.ProjectDocumentRepository;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterService;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_FAILURE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_ALREADY_COMPLETE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DOCUMENT_CANNOT_BE_ACCEPTED_OR_REJECTED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DOCUMENT_CANNOT_BE_DELETED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DOCUMENT_INVALID_DECISION;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DOCUMENT_NOT_YET_UPLOADED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.APPROVED;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.REJECTED;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.SUBMITTED;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.UNSET;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.UPLOADED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DocumentsServiceImplTest extends BaseServiceUnitTest<DocumentsService> {

    private Long projectId = 1L;
    private Long documentConfigId = 2L;
    private Long projectDocumentId = 3L;
    private Long fileEntryId = 5L;


    private Project project;
    private ProjectDocument projectDocument;
    private org.innovateuk.ifs.competitionsetup.domain.ProjectDocument configuredProjectDocument;
    private FileEntry fileEntry;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private ProjectDocumentConfigRepository projectDocumentConfigRepositoryMock;

    @Mock
    private ProjectWorkflowHandler projectWorkflowHandlerMock;

    @Mock
    private ProjectDocumentRepository projectDocumentRepositoryMock;

    @Mock
    private GrantOfferLetterService grantOfferLetterServiceMock;

    @Before
    public void setUp() {

        project = newProject().withId(projectId).build();

        FileType pdfFileType = FileTypeBuilder.newFileType()
                .withName("PDF")
                .withExtension(".pdf")
                .build();

        configuredProjectDocument = org.innovateuk.ifs.competition.builder.ProjectDocumentBuilder
                .newCompetitionProjectDocument()
                .withId(documentConfigId)
                .withTitle("Risk Register")
                .withGuidance("Guidance for Risk Register")
                .withFileTypes(Collections.singletonList(pdfFileType))
                .build();

        fileEntry = newFileEntry().withId(fileEntryId).build();

        projectDocument = ProjectDocumentBuilder.newProjectDocument()
                .withId(projectDocumentId)
                .withProjectDocument(configuredProjectDocument)
                .withFileEntry(fileEntry)
                .build();

        project.setProjectDocuments(singletonList(projectDocument));

        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        when(projectDocumentConfigRepositoryMock.findOne(documentConfigId)).thenReturn(configuredProjectDocument);
    }

    @Test
    public void getValidMediaTypesForDocumentWhenConfiguredProjectDocumentNotPresent() {

        when(projectDocumentConfigRepositoryMock.findOne(documentConfigId)).thenReturn(null);
        ServiceResult<List<String>> result = service.getValidMediaTypesForDocument(documentConfigId);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonErrors.notFoundError(org.innovateuk.ifs.competitionsetup.domain.ProjectDocument.class, documentConfigId)));
    }

    @Test
    public void getValidMediaTypesForDocument() {

        ServiceResult<List<String>> result = service.getValidMediaTypesForDocument(documentConfigId);

        assertTrue(result.isSuccess());

        assertEquals(1, result.getSuccess().size());
        assertEquals("application/pdf", result.getSuccess().get(0));
    }

    @Test
    public void createDocumentFileEntryWhenProjectNotInSetup() {

        FileEntryResource fileEntryResource = FileEntryResourceBuilder.newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.LIVE);
        ServiceResult<FileEntryResource> result = service.createDocumentFileEntry(projectId, documentConfigId, fileEntryResource, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
        verify(fileServiceMock, never()).createFile(any(), any());
        verify(projectDocumentRepositoryMock, never()).save(any(ProjectDocument.class));
    }

    @Test
    public void createDocumentFileEntry() {

        FileEntry fileEntry = newFileEntry().build();
        FileEntryResource fileEntryResource = FileEntryResourceBuilder.newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;
        ServiceResult<Pair<File, FileEntry>> fileDetails = serviceSuccess(Pair.of(new File("newfile"), fileEntry));

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).thenReturn(fileDetails);
        when(fileEntryMapperMock.mapToResource(fileEntry)).thenReturn(fileEntryResource);

        ServiceResult<FileEntryResource> result = service.createDocumentFileEntry(projectId, documentConfigId, fileEntryResource, inputStreamSupplier);

        assertTrue(result.isSuccess());

        verify(projectDocumentRepositoryMock).save(any(ProjectDocument.class));
        ArgumentCaptor<ProjectDocument> captor = ArgumentCaptor.forClass(ProjectDocument.class);
        verify(projectDocumentRepositoryMock).save(captor.capture());
        ProjectDocument savedProjectDocument = captor.getValue();

        assertEquals(project, savedProjectDocument.getProject());
        assertEquals(configuredProjectDocument, savedProjectDocument.getProjectDocument());
        assertEquals(fileEntry, savedProjectDocument.getFileEntry());
        assertEquals(UPLOADED, savedProjectDocument.getStatus());

        assertEquals(fileEntryResource, result.getSuccess());
    }

    @Test
    public void getFileContents() {

        FileEntryResource fileEntryResource = FileEntryResourceBuilder.newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(fileServiceMock.getFileByFileEntryId(fileEntryId)).thenReturn(serviceSuccess(inputStreamSupplier));
        when(fileEntryMapperMock.mapToResource(fileEntry)).thenReturn(fileEntryResource);

        ServiceResult<FileAndContents> result = service.getFileContents(projectId, documentConfigId);

        assertTrue(result.isSuccess());
        assertEquals(fileEntryResource, result.getSuccess().getFileEntry());
        assertEquals(inputStreamSupplier, result.getSuccess().getContentsSupplier());
    }

    @Test
    public void getFileEntryDetails() {

        FileEntryResource fileEntryResource = FileEntryResourceBuilder.newFileEntryResource().build();
        when(fileEntryMapperMock.mapToResource(fileEntry)).thenReturn(fileEntryResource);

        ServiceResult<FileEntryResource> result = service.getFileEntryDetails(projectId, documentConfigId);

        assertTrue(result.isSuccess());
        assertEquals(fileEntryResource, result.getSuccess());
    }


    @Test
    public void deleteDocumentWhenProjectNotInSetup() {

        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.LIVE);
        ServiceResult<Void> result = service.deleteDocument(projectId, documentConfigId);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
        verify(projectDocumentRepositoryMock, never()).delete(any(ProjectDocument.class));
        verify(fileServiceMock, never()).deleteFileIgnoreNotFound(fileEntryId);
    }

    @Test
    public void deleteDocumentWhenProjectDocumentAlreadyApproved() {

        projectDocument.setStatus(APPROVED);
        ServiceResult<Void> result = service.deleteDocument(projectId, documentConfigId);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_DOCUMENT_CANNOT_BE_DELETED));
        verify(projectDocumentRepositoryMock, never()).delete(any(ProjectDocument.class));
        verify(fileServiceMock, never()).deleteFileIgnoreNotFound(fileEntryId);
    }

    @Test
    public void deleteDocumentWhenProjectDocumentAlreadySubmitted() {

        projectDocument.setStatus(SUBMITTED);
        ServiceResult<Void> result = service.deleteDocument(projectId, documentConfigId);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_DOCUMENT_CANNOT_BE_DELETED));
        verify(projectDocumentRepositoryMock, never()).delete(any(ProjectDocument.class));
        verify(fileServiceMock, never()).deleteFileIgnoreNotFound(fileEntryId);
    }

    @Test
    public void deleteDocument() {
        projectDocument.setStatus(UPLOADED);
        ServiceResult<Void> result = service.deleteDocument(projectId, documentConfigId);

        assertTrue(result.isSuccess());
        verify(projectDocumentRepositoryMock).delete(projectDocument);
        verify(fileServiceMock).deleteFileIgnoreNotFound(fileEntryId);
    }

    @Test
    public void submitDocumentWhenProjectNotInSetup() {

        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.LIVE);
        ServiceResult<Void> result = service.submitDocument(projectId, documentConfigId);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
        verify(projectDocumentRepositoryMock, never()).save(any(ProjectDocument.class));
    }

    @Test
    public void submitDocumentWhenProjectDocumentNotInUploadedState() {

        projectDocument.setStatus(UNSET);
        ServiceResult<Void> result = service.submitDocument(projectId, documentConfigId);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_DOCUMENT_NOT_YET_UPLOADED));
        verify(projectDocumentRepositoryMock, never()).save(any(ProjectDocument.class));
    }

    @Test
    public void submitDocument() {
        projectDocument.setStatus(UPLOADED);
        ServiceResult<Void> result = service.submitDocument(projectId, documentConfigId);

        assertTrue(result.isSuccess());
        assertEquals(SUBMITTED, projectDocument.getStatus());
        verify(projectDocumentRepositoryMock).save(projectDocument);
    }

    @Test
    public void documentDecisionWhenDecisionInvalid() {

        ProjectDocumentDecision documentDecision = new ProjectDocumentDecision();
        ServiceResult<Void> result = service.documentDecision(projectId, documentConfigId, documentDecision);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_DOCUMENT_INVALID_DECISION));
        verify(projectDocumentRepositoryMock, never()).save(any(ProjectDocument.class));
        verify(grantOfferLetterServiceMock, never()).generateGrantOfferLetterIfReady(projectId);
    }

    @Test
    public void documentDecisionWhenRejectedWithoutReason() {

        ProjectDocumentDecision documentDecision = new ProjectDocumentDecision(false, null);
        ServiceResult<Void> result = service.documentDecision(projectId, documentConfigId, documentDecision);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_DOCUMENT_INVALID_DECISION));
        verify(projectDocumentRepositoryMock, never()).save(any(ProjectDocument.class));
        verify(grantOfferLetterServiceMock, never()).generateGrantOfferLetterIfReady(projectId);
    }

    @Test
    public void documentDecisionWhenRejectedWithEmptyReason() {

        ProjectDocumentDecision documentDecision = new ProjectDocumentDecision(false, "  ");
        ServiceResult<Void> result = service.documentDecision(projectId, documentConfigId, documentDecision);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_DOCUMENT_INVALID_DECISION));
        verify(projectDocumentRepositoryMock, never()).save(any(ProjectDocument.class));
        verify(grantOfferLetterServiceMock, never()).generateGrantOfferLetterIfReady(projectId);
    }


    @Test
    public void documentDecisionWhenProjectNotInSetup() {

        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.LIVE);
        ProjectDocumentDecision documentDecision = new ProjectDocumentDecision(false, "Missing details");
        ServiceResult<Void> result = service.documentDecision(projectId, documentConfigId, documentDecision);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
        verify(projectDocumentRepositoryMock, never()).save(any(ProjectDocument.class));
        verify(grantOfferLetterServiceMock, never()).generateGrantOfferLetterIfReady(projectId);
    }

    @Test
    public void documentDecisionWhenProjectDocumentNotInSubmittedState() {

        projectDocument.setStatus(UPLOADED);
        ProjectDocumentDecision documentDecision = new ProjectDocumentDecision(false, "Missing details");
        ServiceResult<Void> result = service.documentDecision(projectId, documentConfigId, documentDecision);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_DOCUMENT_CANNOT_BE_ACCEPTED_OR_REJECTED));
        verify(projectDocumentRepositoryMock, never()).save(any(ProjectDocument.class));
        verify(grantOfferLetterServiceMock, never()).generateGrantOfferLetterIfReady(projectId);
    }

    @Test
    public void documentDecisionWhenRejected() {

        String rejectionReason = "Missing details";
        projectDocument.setStatus(SUBMITTED);
        ProjectDocumentDecision documentDecision = new ProjectDocumentDecision(false, rejectionReason);

        when(grantOfferLetterServiceMock.generateGrantOfferLetterIfReady(projectId)).thenReturn(serviceSuccess());
        ServiceResult<Void> result = service.documentDecision(projectId, documentConfigId, documentDecision);

        assertTrue(result.isSuccess());
        assertEquals(REJECTED, projectDocument.getStatus());
        assertEquals(rejectionReason, projectDocument.getStatusComments());
        verify(projectDocumentRepositoryMock).save(projectDocument);
        verify(grantOfferLetterServiceMock).generateGrantOfferLetterIfReady(projectId);
    }

    @Test
    public void documentDecisionWhenGOLGenerationFails() {

        String rejectionReason = "Missing details";
        projectDocument.setStatus(SUBMITTED);
        ProjectDocumentDecision documentDecision = new ProjectDocumentDecision(false, rejectionReason);

        when(grantOfferLetterServiceMock.generateGrantOfferLetterIfReady(projectId)).thenReturn(serviceFailure(GRANT_OFFER_LETTER_GENERATION_FAILURE));
        ServiceResult<Void> result = service.documentDecision(projectId, documentConfigId, documentDecision);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(GRANT_OFFER_LETTER_GENERATION_FAILURE));
        assertEquals(REJECTED, projectDocument.getStatus());
        assertEquals(rejectionReason, projectDocument.getStatusComments());
        verify(projectDocumentRepositoryMock).save(projectDocument);
        verify(grantOfferLetterServiceMock).generateGrantOfferLetterIfReady(projectId);
    }

    @Test
    public void documentDecisionWhenApproved() {

        String rejectionReason = "Reason not used when approved";
        projectDocument.setStatus(SUBMITTED);
        ProjectDocumentDecision documentDecision = new ProjectDocumentDecision(true, rejectionReason);

        when(grantOfferLetterServiceMock.generateGrantOfferLetterIfReady(projectId)).thenReturn(serviceSuccess());
        ServiceResult<Void> result = service.documentDecision(projectId, documentConfigId, documentDecision);

        assertTrue(result.isSuccess());
        assertEquals(APPROVED, projectDocument.getStatus());
        assertNull(projectDocument.getStatusComments());
        verify(projectDocumentRepositoryMock).save(projectDocument);
        verify(grantOfferLetterServiceMock).generateGrantOfferLetterIfReady(projectId);
    }


    @Override
    protected DocumentsService supplyServiceUnderTest() {
        return  new DocumentsServiceImpl();
    }
}

