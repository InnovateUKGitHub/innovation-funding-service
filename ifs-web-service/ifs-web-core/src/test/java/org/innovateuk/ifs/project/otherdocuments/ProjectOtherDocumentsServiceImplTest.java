package org.innovateuk.ifs.project.otherdocuments;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectOtherDocumentsServiceImplTest {

    @InjectMocks
    private ProjectOtherDocumentsServiceImpl projectOtherDocumentsService;

    @Mock
    private ProjectRestService projectRestService;

    @Test
    public void testAddCollaborationAgreement() {

        FileEntryResource createdFile = newFileEntryResource().build();

        when(projectRestService.addCollaborationAgreementDocument(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes())).
                thenReturn(restSuccess(createdFile));

        ServiceResult<FileEntryResource> result =
                projectOtherDocumentsService.addCollaborationAgreementDocument(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());

        assertTrue(result.isSuccess());
        assertEquals(createdFile, result.getSuccessObject());
    }

    @Test
    public void testGetCollaborationAgreementFile() {

        Optional<ByteArrayResource> content = Optional.of(new ByteArrayResource("My content!".getBytes()));
        when(projectRestService.getCollaborationAgreementFile(123L)).thenReturn(restSuccess(content));

        Optional<ByteArrayResource> result = projectOtherDocumentsService.getCollaborationAgreementFile(123L);
        assertEquals(content, result);
    }

    @Test
    public void testGetCollaborationAgreementFileDetails() {

        FileEntryResource returnedFile = newFileEntryResource().build();

        Optional<FileEntryResource> response = Optional.of(returnedFile);
        when(projectRestService.getCollaborationAgreementFileDetails(123L)).thenReturn(restSuccess(response));

        Optional<FileEntryResource> result = projectOtherDocumentsService.getCollaborationAgreementFileDetails(123L);
        assertEquals(response, result);
    }

    @Test
    public void testRemoveCollaborationAgreement() {

        when(projectRestService.removeCollaborationAgreementDocument(123L)).thenReturn(restSuccess());

        ServiceResult<Void> result = projectOtherDocumentsService.removeCollaborationAgreementDocument(123L);

        assertTrue(result.isSuccess());

        verify(projectRestService).removeCollaborationAgreementDocument(123L);
    }

    @Test
    public void testAddExploitationPlan() {

        FileEntryResource createdFile = newFileEntryResource().build();

        when(projectRestService.addExploitationPlanDocument(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes())).
                thenReturn(restSuccess(createdFile));

        ServiceResult<FileEntryResource> result =
                projectOtherDocumentsService.addExploitationPlanDocument(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());

        assertTrue(result.isSuccess());
        assertEquals(createdFile, result.getSuccessObject());
    }

    @Test
    public void testGetCExploitationPlanFile() {

        Optional<ByteArrayResource> content = Optional.of(new ByteArrayResource("My content!".getBytes()));
        when(projectRestService.getExploitationPlanFile(123L)).thenReturn(restSuccess(content));

        Optional<ByteArrayResource> result = projectOtherDocumentsService.getExploitationPlanFile(123L);
        assertEquals(content, result);
    }

    @Test
    public void testGetExploitationPlanFileDetails() {

        FileEntryResource returnedFile = newFileEntryResource().build();

        Optional<FileEntryResource> response = Optional.of(returnedFile);
        when(projectRestService.getExploitationPlanFileDetails(123L)).thenReturn(restSuccess(response));

        Optional<FileEntryResource> result = projectOtherDocumentsService.getExploitationPlanFileDetails(123L);
        assertEquals(response, result);
    }

    @Test
    public void testRemoveExploitationPlan() {

        when(projectRestService.removeExploitationPlanDocument(123L)).thenReturn(restSuccess());

        ServiceResult<Void> result = projectOtherDocumentsService.removeExploitationPlanDocument(123L);

        assertTrue(result.isSuccess());

        verify(projectRestService).removeExploitationPlanDocument(123L);
    }

    @Test
    public void testAcceptOrRejectOtherDocuments() {

        when(projectRestService.acceptOrRejectOtherDocuments(123L, true)).thenReturn(restSuccess());

        ServiceResult<Void> result = projectOtherDocumentsService.acceptOrRejectOtherDocuments(123L, true);

        assertTrue(result.isSuccess());

        verify(projectRestService).acceptOrRejectOtherDocuments(123L, true);
    }

    @Test
    public void testOtherDocumentsSubmitAllowedWhenAllFilesUploaded() throws Exception {

        when(projectRestService.isOtherDocumentsSubmitAllowed(123L)).thenReturn(restSuccess(true));

        Boolean submitAllowed = projectOtherDocumentsService.isOtherDocumentSubmitAllowed(123L);

        assertTrue(submitAllowed);

        verify(projectRestService).isOtherDocumentsSubmitAllowed(123L);
    }

    @Test
    public void testOtherDocumentsSubmitAllowedWhenNotAllFilesUploaded() throws Exception {

        when(projectRestService.isOtherDocumentsSubmitAllowed(123L)).thenReturn(restSuccess(false));

        Boolean submitAllowed = projectOtherDocumentsService.isOtherDocumentSubmitAllowed(123L);

        assertFalse(submitAllowed);

        verify(projectRestService).isOtherDocumentsSubmitAllowed(123L);
    }
    @Test
    public void testSetPartnerDocumentsAsSubmitted()  throws Exception {

        when(projectRestService.setPartnerDocumentsSubmitted(1L)).thenReturn(restSuccess());

        ServiceResult<Void> submitted = projectOtherDocumentsService.setPartnerDocumentsSubmitted(1L);

        assertTrue(submitted.isSuccess());

        verify(projectRestService).setPartnerDocumentsSubmitted(1L);
    }
}