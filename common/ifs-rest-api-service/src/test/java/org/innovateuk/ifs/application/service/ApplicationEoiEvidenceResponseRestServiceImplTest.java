package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static java.lang.String.format;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.OK;

public class ApplicationEoiEvidenceResponseRestServiceImplTest extends BaseRestServiceUnitTest<ApplicationEoiEvidenceResponseRestServiceImpl> {
    private static String URL = "/application";

    @Test
    public void uploadEoiEvidence() {
        long applicationId = 1L;
        long organisationId = 2L;
        long userId = 3L;
        String originalFilename = "filename";
        String contentType = "media/type";
        String requestBody = "content";
        long fileSizeBytes = 1000;

        String url = format("%s/%s/eoi-evidence-response/%s/%s/upload?filename=%s", URL, applicationId, organisationId, userId, originalFilename);
        FileEntryResource expectedFileEntryResource = newFileEntryResource().build();
        setupFileUploadWithRestResultExpectations(url, FileEntryResource.class, requestBody, contentType, fileSizeBytes, expectedFileEntryResource, OK);
        FileEntryResource result = service.uploadEoiEvidence(applicationId, organisationId, userId, contentType, fileSizeBytes, originalFilename, requestBody.getBytes()).getSuccess();

        assertEquals(expectedFileEntryResource, result);
    }

    @Test
    public void submitEoiEvidence() {
        long id = 1L;
        long applicationId = 2L;
        long organisationId = 3L;
        long fileEntryId = 4L;
        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = new ApplicationEoiEvidenceResponseResource(id, applicationId, organisationId, fileEntryId, State.NOT_SUBMITTED);
        UserResource userResource = newUserResource().build();

        String submitUrl = URL + "/" + applicationEoiEvidenceResponseResource.getApplicationId() + "/eoi-evidence-response/submit/" + userResource.getId();
        setupPostWithRestResultExpectations(submitUrl, HttpStatus.OK);

        RestResult<Void> result = service.submitEoiEvidence(applicationEoiEvidenceResponseResource, userResource);
        assertTrue(result.isSuccess());

    }

    @Test
    public void remove() {
        long id = 1L;
        long applicationId = 2L;
        long organisationId = 3L;
        long fileEntryId = 4L;
        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = new ApplicationEoiEvidenceResponseResource(id, applicationId, organisationId, fileEntryId, State.NOT_SUBMITTED);
        UserResource userResource = newUserResource().build();

        String removeUrl = URL + "/" + applicationEoiEvidenceResponseResource.getApplicationId() + "/eoi-evidence-response/remove/" + userResource.getId();
        setupPostWithRestResultExpectations(removeUrl, ApplicationEoiEvidenceResponseResource.class, null, applicationEoiEvidenceResponseResource, HttpStatus.OK);

        RestResult<ApplicationEoiEvidenceResponseResource> result = service.remove(applicationEoiEvidenceResponseResource, userResource);
        assertEquals(applicationEoiEvidenceResponseResource, result.getSuccess());
    }

    @Test
    public void findOneByApplicationId() {
        long id = 1L;
        long applicationId = 2L;
        long organisationId = 3L;
        long fileEntryId = 4L;

        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = new ApplicationEoiEvidenceResponseResource(id, applicationId, organisationId, fileEntryId, State.SUBMITTED);

        String url = format("%s/%s/eoi-evidence-response", URL, applicationId);
        setupGetWithRestResultExpectations(url, ApplicationEoiEvidenceResponseResource.class, applicationEoiEvidenceResponseResource);
        RestResult <Optional<ApplicationEoiEvidenceResponseResource>> result = service.findOneByApplicationId(applicationId);

        assertEquals(applicationEoiEvidenceResponseResource, result.getSuccess().get());
    }

    @Test
    public void getApplicationEoiEvidenceState() {
        long applicationId = 2L;

        ApplicationEoiEvidenceState applicationEoiEvidenceState = ApplicationEoiEvidenceState.NOT_SUBMITTED;

        String url = format("%s/%s/eoi-evidence-response-process-state", URL, applicationId);
        setupGetWithRestResultExpectations(url, ApplicationEoiEvidenceState.class, applicationEoiEvidenceState);
        RestResult <Optional<ApplicationEoiEvidenceState>> result = service.getApplicationEoiEvidenceState(applicationId);

        assertEquals(applicationEoiEvidenceState, result.getSuccess().get());
    }

    @Test
    public void getEvidenceByApplication() {
        long applicationId = 2L;

        ByteArrayResource byteArrayResource = new ByteArrayResource("1u8888888".getBytes());

        String url = format("%s/%s/view-eoi-evidence-file", URL, applicationId);
        setupGetWithRestResultExpectations(url, ByteArrayResource.class, byteArrayResource);
        RestResult<ByteArrayResource>  result = service.getEvidenceByApplication(applicationId);

        assertEquals(byteArrayResource, result.getSuccess());
    }

    @Test
    public void getEvidenceDetailsByApplication() {
        long applicationId = 2L;

        FileEntryResource fileEntryResource = newFileEntryResource().build();

        String url = format("%s/%s/view-eoi-evidence-file/details", URL, applicationId);
        setupGetWithRestResultExpectations(url, FileEntryResource.class, fileEntryResource);
        RestResult<FileEntryResource> result = service.getEvidenceDetailsByApplication(applicationId);

        assertEquals(fileEntryResource, result.getSuccess());
    }

    @Override
    protected ApplicationEoiEvidenceResponseRestServiceImpl registerRestServiceUnderTest() {
        return new ApplicationEoiEvidenceResponseRestServiceImpl();
    }
}