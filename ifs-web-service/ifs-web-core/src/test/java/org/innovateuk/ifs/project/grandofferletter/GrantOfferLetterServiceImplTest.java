package org.innovateuk.ifs.project.grandofferletter;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.grantofferletter.GrantOfferLetterService;
import org.innovateuk.ifs.project.grantofferletter.service.GrantOfferLetterRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    @Mock
    GrantOfferLetterRestService grantOfferLetterRestService;

    @Mock
    GrantOfferLetterService grantOfferLetterService;

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

}