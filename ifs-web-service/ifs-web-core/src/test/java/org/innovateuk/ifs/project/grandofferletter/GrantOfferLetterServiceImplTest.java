package org.innovateuk.ifs.project.grandofferletter;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.grantofferletter.GrantOfferLetterServiceImpl;
import org.innovateuk.ifs.project.grantofferletter.resource.GOLState;
import org.innovateuk.ifs.project.grantofferletter.service.GrantOfferLetterRestService;
import org.innovateuk.ifs.project.resource.ApprovalType;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GrantOfferLetterServiceImplTest {

    @InjectMocks
    GrantOfferLetterServiceImpl grantOfferLetterService;

    @Mock
    GrantOfferLetterRestService grantOfferLetterRestService;

    @Test
    public void testGetGrantOfferLetterFile() {

        Optional<ByteArrayResource> content = Optional.of(new ByteArrayResource("My content!".getBytes()));
        when(grantOfferLetterRestService.getGrantOfferFile(123L)).thenReturn(restSuccess(content));

        Optional<ByteArrayResource> result = grantOfferLetterService.getGrantOfferFile(123L);
        assertEquals(content, result);
    }

    @Test
    public void testGetGrantOfferLetterFileDetails() {

        FileEntryResource returnedFile = newFileEntryResource().build();

        Optional<FileEntryResource> response = Optional.of(returnedFile);
        when(grantOfferLetterRestService.getGrantOfferFileDetails(123L)).thenReturn(restSuccess(response));

        Optional<FileEntryResource> result = grantOfferLetterService.getGrantOfferFileDetails(123L);
        assertEquals(response, result);
    }

    @Test
    public void testAddSignedGrantOfferLetter() {

        FileEntryResource createdFile = newFileEntryResource().build();

        when(grantOfferLetterRestService.addSignedGrantOfferLetterFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes())).
                thenReturn(restSuccess(createdFile));

        ServiceResult<FileEntryResource> result =
                grantOfferLetterService.addSignedGrantOfferLetter(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());

        assertTrue(result.isSuccess());
        assertEquals(createdFile, result.getSuccessObject());
    }

    @Test
    public void testAddGrantOfferLetter() {

        FileEntryResource createdFile = newFileEntryResource().build();

        when(grantOfferLetterRestService.addGrantOfferLetterFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes())).
                thenReturn(restSuccess(createdFile));

        ServiceResult<FileEntryResource> result =
                grantOfferLetterService.addGrantOfferLetter(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());

        assertTrue(result.isSuccess());
        assertEquals(createdFile, result.getSuccessObject());
    }

    @Test
    public void testRemoveGrantOfferLetter() {
        long projectId = 123L;

        when(grantOfferLetterRestService.removeGrantOfferLetter(projectId)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.removeGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testRemoveSignedGrantOfferLetter() {
        long projectId = 123L;

        when(grantOfferLetterRestService.removeSignedGrantOfferLetter(projectId)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.removeSignedGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testSubmitGrantOfferLetter() {
        long projectId = 123L;

        when(grantOfferLetterRestService.submitGrantOfferLetter(projectId)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.submitGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetAdditionalContractFile() {

        Optional<ByteArrayResource> content = Optional.of(new ByteArrayResource("My content!".getBytes()));
        when(grantOfferLetterRestService.getAdditionalContractFile(123L)).thenReturn(restSuccess(content));

        Optional<ByteArrayResource> result = grantOfferLetterService.getAdditionalContractFile(123L);
        assertEquals(content, result);
    }

    @Test
    public void testAddAdditionalContractFile() throws Exception {

        FileEntryResource createdFile = newFileEntryResource().build();

        when(grantOfferLetterRestService.addAdditionalContractFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes())).
                thenReturn(restSuccess(createdFile));

        ServiceResult<FileEntryResource> result =
                grantOfferLetterService.addAdditionalContractFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());

        assertTrue(result.isSuccess());
        assertEquals(createdFile, result.getSuccessObject());

        verify(grantOfferLetterRestService).addAdditionalContractFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());
    }

    @Test
    public void testApproveOrRejectSignedGrantOfferLetter() throws Exception {

        when(grantOfferLetterRestService.approveOrRejectSignedGrantOfferLetter(123L, ApprovalType.APPROVED)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.approveOrRejectSignedGrantOfferLetter(123L, ApprovalType.APPROVED);

        assertTrue(result.isSuccess());

        verify(grantOfferLetterRestService).approveOrRejectSignedGrantOfferLetter(123L, ApprovalType.APPROVED);

    }

    @Test
    public void testIsSignedGrantOfferLetterApproved() throws Exception {

        when(grantOfferLetterRestService.isSignedGrantOfferLetterApproved(123L)).thenReturn(restSuccess(Boolean.TRUE));

        ServiceResult<Boolean> result = grantOfferLetterService.isSignedGrantOfferLetterApproved(123L);

        assertTrue(result.isSuccess());
        assertEquals(Boolean.TRUE, result.getSuccessObject());

        verify(grantOfferLetterRestService).isSignedGrantOfferLetterApproved(123L);

    }

    @Test
    public void testGrantOfferLetterAlreadySent() throws Exception {

        when(grantOfferLetterRestService.isGrantOfferLetterAlreadySent(123L)).thenReturn(restSuccess(Boolean.TRUE));

        ServiceResult<Boolean> result = grantOfferLetterService.isGrantOfferLetterAlreadySent(123L);

        assertTrue(result.isSuccess());
        assertEquals(Boolean.TRUE, result.getSuccessObject());

        verify(grantOfferLetterRestService).isGrantOfferLetterAlreadySent(123L);

    }

    @Test
    public void testIsSendGrantOfferLetterAllowed() throws Exception {

        when(grantOfferLetterRestService.isSendGrantOfferLetterAllowed(123L)).thenReturn(restSuccess(Boolean.TRUE));

        ServiceResult<Boolean> result = grantOfferLetterService.isSendGrantOfferLetterAllowed(123L);

        assertTrue(result.isSuccess());
        assertEquals(Boolean.TRUE, result.getSuccessObject());

        verify(grantOfferLetterRestService).isSendGrantOfferLetterAllowed(123L);

    }

    @Test
    public void testSendGrantOfferLetter() throws Exception {

        when(grantOfferLetterRestService.sendGrantOfferLetter(123L)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.sendGrantOfferLetter(123L);

        assertTrue(result.isSuccess());

        verify(grantOfferLetterRestService).sendGrantOfferLetter(123L);

    }

    @Test
    public void testGetGrantOfferLetterWorkflowState() throws Exception {

        Long projectId = 123L;

        when(grantOfferLetterRestService.getGrantOfferLetterWorkflowState(projectId)).thenReturn(restSuccess(GOLState.APPROVED));

        ServiceResult<GOLState> result = grantOfferLetterService.getGrantOfferLetterWorkflowState(projectId);

        assertTrue(result.isSuccess());
        assertEquals(GOLState.APPROVED, result.getSuccessObject());

        verify(grantOfferLetterRestService).getGrantOfferLetterWorkflowState(projectId);

    }

}