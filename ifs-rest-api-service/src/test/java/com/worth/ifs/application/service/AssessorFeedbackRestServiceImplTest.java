package com.worth.ifs.application.service;

import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.file.resource.FileEntryResource;

public class AssessorFeedbackRestServiceImplTest extends BaseRestServiceUnitTest<AssessorFeedbackRestServiceImpl> {

    private static final String assessorFeedbackRestURL = "/assessorFeedback";

    @Test
    public void testAddAssessorFeedbackDocument() {

        String expectedUrl = assessorFeedbackRestURL + "/assessorFeedbackDocument?applicationId=123&filename=original.pdf";
        FileEntryResource returnedFileEntry = FileEntryResourceBuilder.newFileEntryResource().build();

        setupFileUploadWithRestResultExpectations(
                expectedUrl, FileEntryResource.class, "New content", "text/plain", 1000L, returnedFileEntry, OK);

        // now run the method under test
        FileEntryResource createdFileEntry =
                service.addAssessorFeedbackDocument(123L, "text/plain", 1000L, "original.pdf", "New content".getBytes()).getSuccessObject();

        Assert.assertEquals(returnedFileEntry, createdFileEntry);
    }

    @Test
    public void testGetAssessorFeedbackDocumentDetails() {

        String expectedUrl = assessorFeedbackRestURL + "/assessorFeedbackDocument/fileentry?applicationId=123";
        FileEntryResource returnedFileEntry = FileEntryResourceBuilder.newFileEntryResource().build();

        setupGetWithRestResultExpectations(expectedUrl, FileEntryResource.class, returnedFileEntry, OK);

        // now run the method under test
        FileEntryResource retrievedFileEntry = service.getAssessorFeedbackFileDetails(123L).getSuccessObject();

        Assert.assertEquals(returnedFileEntry, retrievedFileEntry);
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
    
    @Test
    public void testFeedbackUploaded() {

        String expectedUrl = assessorFeedbackRestURL + "/assessorFeedbackUploaded?competitionId=123";

        setupGetWithRestResultExpectations(expectedUrl, Boolean.class, true);

        // now run the method under test
        RestResult<Boolean> result = service.feedbackUploaded(123L);

        assertTrue(result.isSuccess());
        Assert.assertEquals(Boolean.TRUE, result.getSuccessObject());
    }
    
    @Test
    public void testSubmitAssessorFeedback() {

        String expectedUrl = assessorFeedbackRestURL + "/submitAssessorFeedback/123";

        setupPostWithRestResultExpectations(expectedUrl, null, HttpStatus.OK);

        // now run the method under test
        RestResult<Void> result = service.submitAssessorFeedback(123L);

        assertTrue(result.isSuccess());
    }

    @Override
    protected AssessorFeedbackRestServiceImpl registerRestServiceUnderTest() {
        AssessorFeedbackRestServiceImpl serviceUnderTest = new AssessorFeedbackRestServiceImpl();
        ReflectionTestUtils.setField(serviceUnderTest, "restUrl", assessorFeedbackRestURL);
        return serviceUnderTest;
    }

}
