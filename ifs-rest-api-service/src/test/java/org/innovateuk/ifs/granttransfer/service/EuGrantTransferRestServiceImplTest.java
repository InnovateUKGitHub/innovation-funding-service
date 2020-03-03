package org.innovateuk.ifs.granttransfer.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;

import static java.lang.String.format;
import static org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResourceBuilder.newEuGrantTransferResource;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class EuGrantTransferRestServiceImplTest extends BaseRestServiceUnitTest<EuGrantTransferRestServiceImpl> {

    private static final String REST_URL = "/eu-grant-transfer";

    @Override
    protected EuGrantTransferRestServiceImpl registerRestServiceUnderTest() {
        return new EuGrantTransferRestServiceImpl();
    }

    @Test
    public void findGrantAgreement() {
        long applicationId = 1L;
        FileEntryResource expected = new FileEntryResource();
        setupGetWithRestResultExpectations(format("%s/%s/%s", REST_URL, "grant-agreement-details", applicationId), FileEntryResource.class, expected, OK);
        final FileEntryResource response = service.findGrantAgreement(applicationId).getSuccess();
        assertSame(expected, response);
    }

    @Test
    public void uploadGrantAgreement() {
        String fileContentString = "keDFjFGrueurFGy3456efhjdg3";
        byte[] fileContent = fileContentString.getBytes();
        final String originalFilename = "testFile.pdf";
        final String contentType = "text/pdf";
        final long applicationId = 77L;
        setupFileUploadWithRestResultExpectations(format("%s/%s/%s?filename=%s", REST_URL, "grant-agreement", applicationId, originalFilename),
                fileContentString, contentType, fileContent.length, CREATED);

        RestResult<Void> result = service.uploadGrantAgreement(applicationId, contentType, fileContent.length, originalFilename, fileContent);
        assertTrue(result.isSuccess());
    }

    @Test
    public void deleteGrantAgreement() {
        long applicationId = 78L;
        setupDeleteWithRestResultExpectations(format("%s/%s/%s", REST_URL, "grant-agreement", applicationId));
        service.deleteGrantAgreement(applicationId);
        setupDeleteWithRestResultVerifications(format("%s/%s/%s", REST_URL, "grant-agreement", applicationId));
    }

    @Test
    public void downloadGrantAgreement() {
        final long applicationId = 912L;
        ByteArrayResource expected = new ByteArrayResource("1u6536748".getBytes());
        setupGetWithRestResultExpectations(format("%s/%s/%s", REST_URL, "grant-agreement", applicationId), ByteArrayResource.class, expected, OK);
        final ByteArrayResource response = service.downloadGrantAgreement(applicationId).getSuccess();
        assertSame(expected, response);
    }

    @Test
    public void findDetailsByApplicationId() {
        final long applicationId = 912L;
        EuGrantTransferResource euGrantTransferResource = newEuGrantTransferResource().build();
        setupGetWithRestResultExpectations(format("%s/%s", REST_URL, applicationId), EuGrantTransferResource.class, euGrantTransferResource, OK);

        RestResult<EuGrantTransferResource> result = service.findDetailsByApplicationId(applicationId);

        assertSame(euGrantTransferResource, result.getSuccess());
    }

    @Test
    public void updateGrantTransferDetails() {
        final long applicationId = 912L;
        EuGrantTransferResource euGrantTransferResource = newEuGrantTransferResource().build();
        setupPostWithRestResultExpectations(format("%s/%s", REST_URL, applicationId), euGrantTransferResource, OK);

        RestResult<Void> result = service.updateGrantTransferDetails(euGrantTransferResource, applicationId);

        assertTrue(result.isSuccess());
    }
}