package org.innovateuk.ifs.grantofferletter;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterApprovalResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.grantofferletter.service.GrantOfferLetterRestService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class GrantOfferLetterServiceImplTest {

    @InjectMocks
    private GrantOfferLetterServiceImpl grantOfferLetterService;

    @Mock
    private GrantOfferLetterRestService grantOfferLetterRestService;

    @Test
    public void getGrantOfferLetterFile() {

        Optional<ByteArrayResource> content = Optional.of(new ByteArrayResource("My content!".getBytes()));
        when(grantOfferLetterRestService.getGrantOfferFile(123L)).thenReturn(restSuccess(content));

        Optional<ByteArrayResource> result = grantOfferLetterService.getGrantOfferFile(123L);
        assertEquals(content, result);
    }

    @Test
    public void getGrantOfferLetterFileDetails() {

        FileEntryResource returnedFile = newFileEntryResource().build();

        Optional<FileEntryResource> response = Optional.of(returnedFile);
        when(grantOfferLetterRestService.getGrantOfferFileDetails(123L)).thenReturn(restSuccess(response));

        Optional<FileEntryResource> result = grantOfferLetterService.getGrantOfferFileDetails(123L);
        assertEquals(response, result);
    }

    @Test
    public void getSignedAdditionalContractFileDetails() {

        FileEntryResource returnedFile = newFileEntryResource().build();

        Optional<FileEntryResource> response = Optional.of(returnedFile);
        when(grantOfferLetterRestService.getSignedAdditionalContractFileDetails(123L)).thenReturn(restSuccess(response));

        Optional<FileEntryResource> result = grantOfferLetterService.getSignedAdditionalContractFileDetails(123L);
        assertEquals(response, result);
    }

    @Test
    public void addSignedGrantOfferLetter() {

        FileEntryResource createdFile = newFileEntryResource().build();

        when(grantOfferLetterRestService.addSignedGrantOfferLetterFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes())).
                thenReturn(restSuccess(createdFile));

        ServiceResult<FileEntryResource> result =
                grantOfferLetterService.addSignedGrantOfferLetter(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());

        assertTrue(result.isSuccess());
        assertEquals(createdFile, result.getSuccess());
    }

    @Test
    public void addGrantOfferLetter() {

        FileEntryResource createdFile = newFileEntryResource().build();

        when(grantOfferLetterRestService.addGrantOfferLetterFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes())).
                thenReturn(restSuccess(createdFile));

        ServiceResult<FileEntryResource> result =
                grantOfferLetterService.addGrantOfferLetter(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());

        assertTrue(result.isSuccess());
        assertEquals(createdFile, result.getSuccess());
    }

    @Test
    public void removeGrantOfferLetter() {
        long projectId = 123L;

        when(grantOfferLetterRestService.removeGrantOfferLetter(projectId)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.removeGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void resetGrantOfferLetter() {
        long projectId = 123L;

        when(grantOfferLetterRestService.resetGrantOfferLetter(projectId)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.resetGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void removeAdditionalContractFile() {
        long projectId = 123L;

        when(grantOfferLetterRestService.removeAdditionalContractFile(projectId)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.removeAdditionalContractFile(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void removeSignedGrantOfferLetter() {
        long projectId = 123L;

        when(grantOfferLetterRestService.removeSignedGrantOfferLetter(projectId)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.removeSignedGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void removeSignedAdditionalContractFile() {
        long projectId = 123L;

        when(grantOfferLetterRestService.removeSignedAdditionalContractFile(projectId)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.removeSignedAdditionalContract(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void submitGrantOfferLetter() {
        long projectId = 123L;

        when(grantOfferLetterRestService.submitGrantOfferLetter(projectId)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.submitGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void getAdditionalContractFile() {

        Optional<ByteArrayResource> content = Optional.of(new ByteArrayResource("My content!".getBytes()));
        when(grantOfferLetterRestService.getAdditionalContractFile(123L)).thenReturn(restSuccess(content));

        Optional<ByteArrayResource> result = grantOfferLetterService.getAdditionalContractFile(123L);
        assertEquals(content, result);
    }

    @Test
    public void addAdditionalContractFile() {

        FileEntryResource createdFile = newFileEntryResource().build();

        when(grantOfferLetterRestService.addAdditionalContractFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes())).
                thenReturn(restSuccess(createdFile));

        ServiceResult<FileEntryResource> result =
                grantOfferLetterService.addAdditionalContractFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());

        assertTrue(result.isSuccess());
        assertEquals(createdFile, result.getSuccess());

        verify(grantOfferLetterRestService).addAdditionalContractFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());
    }

    @Test
    public void addSignedAdditionalContractFile() {

        FileEntryResource createdFile = newFileEntryResource().build();

        when(grantOfferLetterRestService.addSignedAdditionalContractFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes())).
                thenReturn(restSuccess(createdFile));

        ServiceResult<FileEntryResource> result =
                grantOfferLetterService.addSignedAdditionalContract(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());

        assertTrue(result.isSuccess());
        assertEquals(createdFile, result.getSuccess());

        verify(grantOfferLetterRestService).addSignedAdditionalContractFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());
    }

    @Test
    public void getSignedAdditionalContractFile() {

        Optional<ByteArrayResource> content = Optional.of(new ByteArrayResource("My content!".getBytes()));
        when(grantOfferLetterRestService.getSignedAdditionalContractFile(123L)).thenReturn(restSuccess(content));

        Optional<ByteArrayResource> result = grantOfferLetterService.getSignedAdditionalContractFile(123L);
        assertEquals(content, result);
    }

    @Test
    public void approveOrRejectSignedGrantOfferLetter() {

        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(ApprovalType.APPROVED, null);
        when(grantOfferLetterRestService.approveOrRejectSignedGrantOfferLetter(123L, grantOfferLetterApprovalResource)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.approveOrRejectSignedGrantOfferLetter(123L, grantOfferLetterApprovalResource);

        assertTrue(result.isSuccess());

        verify(grantOfferLetterRestService).approveOrRejectSignedGrantOfferLetter(123L, grantOfferLetterApprovalResource);

    }

    @Test
    public void sendGrantOfferLetter() {

        when(grantOfferLetterRestService.sendGrantOfferLetter(123L)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.sendGrantOfferLetter(123L);

        assertTrue(result.isSuccess());

        verify(grantOfferLetterRestService).sendGrantOfferLetter(123L);

    }

    @Test
    public void getGrantOfferLetterState() {

        Long projectId = 123L;

        GrantOfferLetterStateResource state = GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.APPROVED, GrantOfferLetterEvent.SIGNED_GOL_APPROVED);

        when(grantOfferLetterRestService.getGrantOfferLetterState(projectId)).thenReturn(restSuccess(state));

        ServiceResult<GrantOfferLetterStateResource> result = grantOfferLetterService.getGrantOfferLetterState(projectId);

        assertTrue(result.isSuccess());
        assertSame(state, result.getSuccess());

        verify(grantOfferLetterRestService).getGrantOfferLetterState(projectId);

    }
}