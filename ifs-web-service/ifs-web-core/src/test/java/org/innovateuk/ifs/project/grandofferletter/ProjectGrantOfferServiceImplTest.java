package org.innovateuk.ifs.project.grandofferletter;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.grantofferletter.ProjectGrantOfferService;
import org.innovateuk.ifs.project.grantofferletter.service.ProjectGrantOfferLetterRestService;
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
public class ProjectGrantOfferServiceImplTest {

    @Mock
    ProjectGrantOfferLetterRestService projectGrantOfferLetterRestService;

    @Mock
    ProjectGrantOfferService projectGrantOfferService;

    @Test
    public void testGetGrantOfferLetterFile() {

        Optional<ByteArrayResource> content = Optional.of(new ByteArrayResource("My content!".getBytes()));
        when(projectGrantOfferLetterRestService.getGrantOfferFile(123L)).thenReturn(restSuccess(content));

        Optional<ByteArrayResource> result = projectGrantOfferService.getGrantOfferFile(123L);
        assertEquals(content, result);
    }

    @Test
    public void testGetGrantOfferLetterFileDetails() {

        FileEntryResource returnedFile = newFileEntryResource().build();

        Optional<FileEntryResource> response = Optional.of(returnedFile);
        when(projectGrantOfferLetterRestService.getGrantOfferFileDetails(123L)).thenReturn(restSuccess(response));

        Optional<FileEntryResource> result = projectGrantOfferService.getGrantOfferFileDetails(123L);
        assertEquals(response, result);
    }

    @Test
    public void testAddSignedGrantOfferLetter() {

        FileEntryResource createdFile = newFileEntryResource().build();

        when(projectGrantOfferLetterRestService.addSignedGrantOfferLetterFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes())).
                thenReturn(restSuccess(createdFile));

        ServiceResult<FileEntryResource> result =
                projectGrantOfferService.addSignedGrantOfferLetter(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());

        assertTrue(result.isSuccess());
        assertEquals(createdFile, result.getSuccessObject());
    }

    @Test
    public void testAddGrantOfferLetter() {

        FileEntryResource createdFile = newFileEntryResource().build();

        when(projectGrantOfferLetterRestService.addGrantOfferLetterFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes())).
                thenReturn(restSuccess(createdFile));

        ServiceResult<FileEntryResource> result =
                projectGrantOfferService.addGrantOfferLetter(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());

        assertTrue(result.isSuccess());
        assertEquals(createdFile, result.getSuccessObject());
    }

    @Test
    public void testRemoveGrantOfferLetter() {
        long projectId = 123L;

        when(projectGrantOfferLetterRestService.removeGrantOfferLetter(projectId)).thenReturn(restSuccess());

        ServiceResult<Void> result = projectGrantOfferService.removeGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testRemoveSignedGrantOfferLetter() {
        long projectId = 123L;

        when(projectGrantOfferLetterRestService.removeSignedGrantOfferLetter(projectId)).thenReturn(restSuccess());

        ServiceResult<Void> result = projectGrantOfferService.removeSignedGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testSubmitGrantOfferLetter() {
        long projectId = 123L;

        when(projectGrantOfferLetterRestService.submitGrantOfferLetter(projectId)).thenReturn(restSuccess());

        ServiceResult<Void> result = projectGrantOfferService.submitGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetAdditionalContractFile() {

        Optional<ByteArrayResource> content = Optional.of(new ByteArrayResource("My content!".getBytes()));
        when(projectGrantOfferLetterRestService.getAdditionalContractFile(123L)).thenReturn(restSuccess(content));

        Optional<ByteArrayResource> result = projectGrantOfferService.getAdditionalContractFile(123L);
        assertEquals(content, result);
    }

    @Test
    public void testAddAdditionalContractFile()  throws Exception {

        FileEntryResource createdFile = newFileEntryResource().build();

        when(projectGrantOfferLetterRestService.addAdditionalContractFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes())).
                thenReturn(restSuccess(createdFile));

        ServiceResult<FileEntryResource> result =
                projectGrantOfferService.addAdditionalContractFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());

        assertTrue(result.isSuccess());
        assertEquals(createdFile, result.getSuccessObject());

        verify(projectGrantOfferLetterRestService).addAdditionalContractFile(123L, "text/plain", 1000, "filename.txt", "My content!".getBytes());
    }

}