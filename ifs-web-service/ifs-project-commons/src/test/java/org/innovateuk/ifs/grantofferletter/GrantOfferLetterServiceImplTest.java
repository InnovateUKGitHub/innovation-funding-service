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
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GrantOfferLetterServiceImplTest {

    @InjectMocks
    private GrantOfferLetterServiceImpl grantOfferLetterService;

    @Mock
    private GrantOfferLetterRestService grantOfferLetterRestService;

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
        assertEquals(createdFile, result.getSuccess());
    }

    @Test
    public void testAddGrantOfferLetter() {

        FileEntryResource createdFile = newFileEntryResource().build();

        when(grantOfferLetterRestService.addGrantOfferLetterFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes())).
                thenReturn(restSuccess(createdFile));

        ServiceResult<FileEntryResource> result =
                grantOfferLetterService.addGrantOfferLetter(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());

        assertTrue(result.isSuccess());
        assertEquals(createdFile, result.getSuccess());
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
    public void testAddAdditionalContractFile() {

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
    public void testApproveOrRejectSignedGrantOfferLetter() {

        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(ApprovalType.APPROVED, null);
        when(grantOfferLetterRestService.approveOrRejectSignedGrantOfferLetter(123L, grantOfferLetterApprovalResource)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.approveOrRejectSignedGrantOfferLetter(123L, grantOfferLetterApprovalResource);

        assertTrue(result.isSuccess());

        verify(grantOfferLetterRestService).approveOrRejectSignedGrantOfferLetter(123L, grantOfferLetterApprovalResource);

    }

    @Test
    public void testSendGrantOfferLetter() {

        when(grantOfferLetterRestService.sendGrantOfferLetter(123L)).thenReturn(restSuccess());

        ServiceResult<Void> result = grantOfferLetterService.sendGrantOfferLetter(123L);

        assertTrue(result.isSuccess());

        verify(grantOfferLetterRestService).sendGrantOfferLetter(123L);

    }

    @Test
    public void testGetGrantOfferLetterState() {

        Long projectId = 123L;

        GrantOfferLetterStateResource state = GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.APPROVED, GrantOfferLetterEvent.SIGNED_GOL_APPROVED);

        when(grantOfferLetterRestService.getGrantOfferLetterState(projectId)).thenReturn(restSuccess(state));

        ServiceResult<GrantOfferLetterStateResource> result = grantOfferLetterService.getGrantOfferLetterState(projectId);

        assertTrue(result.isSuccess());
        assertSame(state, result.getSuccess());

        verify(grantOfferLetterRestService).getGrantOfferLetterState(projectId);

    }
}