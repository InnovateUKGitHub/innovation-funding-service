package com.worth.ifs.application.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.file.resource.FileEntryResource;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public class AssessorFeedbackRestServiceImplTest extends BaseRestServiceUnitTest<AssessorFeedbackRestServiceImpl> {

    private static final String assessorFeedbackRestURL = "/assessorFeedback";

    @Test
    public void testAddAssessorFeedbackDocument() {

        String expectedUrl = assessorFeedbackRestURL + "/assessorFeedbackDocument?applicationId=123&filename=original.pdf";
        FileEntryResource returnedFileEntry = newFileEntryResource().build();

        setupFileUploadWithRestResultExpectations(
                expectedUrl, FileEntryResource.class, "New content", "text/plain", 1000L, returnedFileEntry, OK);

        // now run the method under test
        FileEntryResource createdFileEntry =
                service.addAssessorFeedbackDocument(123L, "text/plain", 1000L, "original.pdf", "New content".getBytes()).getSuccessObject();

        assertEquals(returnedFileEntry, createdFileEntry);
    }

    @Test
    public void testGetAssessorFeedbackDocumentDetails() {

        String expectedUrl = assessorFeedbackRestURL + "/assessorFeedbackDocument/fileentry?applicationId=123";
        FileEntryResource returnedFileEntry = newFileEntryResource().build();

        setupGetWithRestResultExpectations(expectedUrl, FileEntryResource.class, returnedFileEntry, OK);

        // now run the method under test
        FileEntryResource retrievedFileEntry = service.getAssessorFeedbackFileDetails(123L).getSuccessObject();

        assertEquals(returnedFileEntry, retrievedFileEntry);
    }

    @Test
    public void testGetAssessorFeedbackDocumentContent() {

        String expectedUrl = assessorFeedbackRestURL + "/assessorFeedbackDocument?applicationId=123";
        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, returnedFileContents, OK);

        // now run the method under test
        ByteArrayResource retrievedFileEntry = service.getAssessorFeedbackFile(123L).getSuccessObject();

        assertEquals(returnedFileContents, retrievedFileEntry);
    }

    @Test
    public void testDeleteAssessorFeedbackDocumentDetails() {

        String expectedUrl = assessorFeedbackRestURL + "/assessorFeedbackDocument?applicationId=123";

        setupDeleteWithRestResultExpectations(expectedUrl);

        // now run the method under test
        service.removeAssessorFeedbackDocument(123L);

        setupDeleteWithRestResultVerifications(expectedUrl);
    }

    @Override
    protected AssessorFeedbackRestServiceImpl registerRestServiceUnderTest() {
        AssessorFeedbackRestServiceImpl serviceUnderTest = new AssessorFeedbackRestServiceImpl();
        ReflectionTestUtils.setField(serviceUnderTest, "restUrl", assessorFeedbackRestURL);
        return serviceUnderTest;
    }

}
