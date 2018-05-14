package org.innovateuk.ifs.interview;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.interview.service.InterviewResponseRestServiceImpl;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;

import static java.lang.String.format;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class InterviewResponseRestServiceImplTest extends BaseRestServiceUnitTest<InterviewResponseRestServiceImpl> {

    private static final String interviewResponseRestUrl = "/interview-response";

    @Override
    protected InterviewResponseRestServiceImpl registerRestServiceUnderTest() {
        return new InterviewResponseRestServiceImpl();
    }

    @Test
    public void findResponse() throws Exception {
        long applicationId = 1L;
        FileEntryResource expected = new FileEntryResource();
        setupGetWithRestResultExpectations(format("%s/%s/%s", interviewResponseRestUrl, "details", applicationId), FileEntryResource.class, expected, OK);
        final FileEntryResource response = service.findResponse(applicationId).getSuccess();
        assertSame(expected, response);
    }

    @Test
    public void uploadResponse() throws Exception {
        String fileContentString = "keDFjFGrueurFGy3456efhjdg3";
        byte[] fileContent = fileContentString.getBytes();
        final String originalFilename = "testFile.pdf";
        final String contentType = "text/pdf";
        final long applicationId = 77L;
        setupFileUploadWithRestResultExpectations(format("%s/%s?filename=%s", interviewResponseRestUrl, applicationId, originalFilename),
                fileContentString, contentType, fileContent.length, CREATED);

        RestResult<Void> result = service.uploadResponse(applicationId, contentType, fileContent.length, originalFilename, fileContent);
        assertTrue(result.isSuccess());
    }

    @Test
    public void deleteResponse() throws Exception {
        long applicationId = 78L;
        setupDeleteWithRestResultExpectations(format("%s/%s", interviewResponseRestUrl, applicationId));
        service.deleteResponse(applicationId);
        setupDeleteWithRestResultVerifications(format("%s/%s", interviewResponseRestUrl, applicationId));
    }

    @Test
    public void downloadResponse() throws Exception {
        final long applicationId= 912L;
        ByteArrayResource expected = new ByteArrayResource("1u6536748".getBytes());
        setupGetWithRestResultExpectations(format("%s/%s", interviewResponseRestUrl, applicationId), ByteArrayResource.class, expected, OK);
        final ByteArrayResource response = service.downloadResponse(applicationId).getSuccess();
        assertSame(expected, response);
    }
}