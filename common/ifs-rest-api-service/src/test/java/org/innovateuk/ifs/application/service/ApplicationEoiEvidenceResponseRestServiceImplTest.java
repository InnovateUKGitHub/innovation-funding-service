package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
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

        String url = format("%s/%s/eoi-evidence/%s/upload?filename=%s", URL, applicationId, organisationId, originalFilename);
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
        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = new ApplicationEoiEvidenceResponseResource(id, applicationId, organisationId, fileEntryId);
        UserResource userResource = newUserResource().build();

        String submitUrl = URL + "/" + applicationEoiEvidenceResponseResource.getApplicationId() + "/eoi-evidence-response/submit/" + userResource.getId();
        setupPostWithRestResultExpectations(submitUrl, HttpStatus.OK);

        RestResult<Void> result = service.submitEoiEvidence(applicationEoiEvidenceResponseResource, userResource);
        assertTrue(result.isSuccess());

    }
//
//    @Test
//    public void remove() {
//        long id = 1L;
//        long applicationId = 2L;
//        long organisationId = 3L;
//        long fileEntryId = 4L;
//        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = new ApplicationEoiEvidenceResponseResource(id, applicationId, organisationId, fileEntryId);
//        UserResource userResource = newUserResource().build();
//
//        String removeUrl = URL + "/" + applicationEoiEvidenceResponseResource.getApplicationId() + "/eoi-evidence-response/remove/" + userResource.getId();
//        setupPostWithRestResultExpectations(removeUrl, applicationEoiEvidenceResponseResource, HttpStatus.OK);
//
//        RestResult<ApplicationEoiEvidenceResponseResource> result = service.remove(applicationEoiEvidenceResponseResource, userResource);
//        assertEquals(applicationEoiEvidenceResponseResource, result);
//    }

    @Test
    public void findOneByApplicationId() {
        long id = 1L;
        long applicationId = 2L;
        long organisationId = 3L;
        long fileEntryId = 4L;

        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = new ApplicationEoiEvidenceResponseResource(id, applicationId, organisationId, fileEntryId);

        String url = format("%s/%s/eoi-evidence-response", URL, applicationId);
        setupGetWithRestResultExpectations(url, ApplicationEoiEvidenceResponseResource.class, applicationEoiEvidenceResponseResource);
        RestResult <Optional<ApplicationEoiEvidenceResponseResource>> result = service.findOneByApplicationId(applicationId);

        assertEquals(applicationEoiEvidenceResponseResource, result.getSuccess().get());
    }

    @Override
    protected ApplicationEoiEvidenceResponseRestServiceImpl registerRestServiceUnderTest() {
        return new ApplicationEoiEvidenceResponseRestServiceImpl();
    }
}