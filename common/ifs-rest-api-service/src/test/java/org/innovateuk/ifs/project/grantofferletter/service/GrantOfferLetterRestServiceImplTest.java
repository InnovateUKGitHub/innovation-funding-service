package org.innovateuk.ifs.project.grantofferletter.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterApprovalResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class GrantOfferLetterRestServiceImplTest extends BaseRestServiceUnitTest<GrantOfferLetterRestServiceImpl> {
    private static final String projectRestURL = "/project";

    @Test
    public void getSignedGrantOfferLetterFileContent() {

        String expectedUrl = projectRestURL + "/123/signed-grant-offer";
        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, returnedFileContents, OK);

        ByteArrayResource retrievedFileEntry = service.getSignedGrantOfferLetterFile(123L).getSuccess().get();

        assertEquals(returnedFileContents, retrievedFileEntry);
    }

    @Test
    public void getSignedGrantOfferLetterFileContentEmptyIfNotFound() {

        String expectedUrl = projectRestURL + "/123/signed-grant-offer";

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, null, NOT_FOUND);

        Optional<ByteArrayResource> retrievedFileEntry = service.getSignedGrantOfferLetterFile(123L).getSuccess();

        assertFalse(retrievedFileEntry.isPresent());
    }

    @Test
    public void getSignedAdditionalContractFileContent() {

        String expectedUrl = projectRestURL + "/123/signed-additional-contract";
        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, returnedFileContents, OK);

        ByteArrayResource retrievedFileEntry = service.getSignedAdditionalContractFile(123L).getSuccess().get();

        assertEquals(returnedFileContents, retrievedFileEntry);
    }

    @Test
    public void getSignedAdditionalContractFileContentEmptyIfNotFound() {

        String expectedUrl = projectRestURL + "/123/signed-additional-contract";

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, null, NOT_FOUND);

        Optional<ByteArrayResource> retrievedFileEntry = service.getSignedAdditionalContractFile(123L).getSuccess();

        assertFalse(retrievedFileEntry.isPresent());
    }

    @Test
    public void getGeneratedGrantOfferLetterFileContent() {

        String expectedUrl = projectRestURL + "/123/grant-offer";
        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, returnedFileContents, OK);

        ByteArrayResource retrievedFileEntry = service.getGrantOfferFile(123L).getSuccess().get();

        assertEquals(returnedFileContents, retrievedFileEntry);
    }

    @Test
    public void getGeneratedGrantOfferLetterFileContentEmptyIfNotFound() {

        String expectedUrl = projectRestURL + "/123/grant-offer";

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, null, NOT_FOUND);

        Optional<ByteArrayResource> retrievedFileEntry = service.getGrantOfferFile(123L).getSuccess();

        assertFalse(retrievedFileEntry.isPresent());
    }

    @Test
    public void removeGeneratedGrantOfferLetter() {

        Long projectId = 123L;
        String nonBaseUrl = projectRestURL + "/" + projectId + "/grant-offer";

        setupDeleteWithRestResultExpectations(nonBaseUrl);

        RestResult<Void> result = service.removeGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());

        setupDeleteWithRestResultVerifications(nonBaseUrl);
    }

    @Test
    public void resetGrantOfferLetter() {

        Long projectId = 123L;
        String nonBaseUrl = projectRestURL + "/" + projectId + "/grant-offer/reset";

        setupDeleteWithRestResultExpectations(nonBaseUrl);

        RestResult<Void> result = service.resetGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());

        setupDeleteWithRestResultVerifications(nonBaseUrl);
    }

    @Test
    public void removeAdditionalContractFile() {

        Long projectId = 123L;
        String nonBaseUrl = projectRestURL + "/" + projectId + "/additional-contract";

        setupDeleteWithRestResultExpectations(nonBaseUrl);

        RestResult<Void> result = service.removeAdditionalContractFile(projectId);

        assertTrue(result.isSuccess());

        setupDeleteWithRestResultVerifications(nonBaseUrl);
    }

    @Test
    public void removeSignedGrantOfferLetter() {

        Long projectId = 123L;
        String nonBaseUrl = projectRestURL + "/" + projectId + "/signed-grant-offer-letter";

        setupDeleteWithRestResultExpectations(nonBaseUrl);

        RestResult<Void> result = service.removeSignedGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());

        setupDeleteWithRestResultVerifications(nonBaseUrl);
    }

    @Test
    public void removeSignedAdditionalContractFile() {

        Long projectId = 123L;
        String nonBaseUrl = projectRestURL + "/" + projectId + "/signed-additional-contract";

        setupDeleteWithRestResultExpectations(nonBaseUrl);

        RestResult<Void> result = service.removeSignedAdditionalContractFile(projectId);

        assertTrue(result.isSuccess());

        setupDeleteWithRestResultVerifications(nonBaseUrl);
    }

    @Test
    public void submitGrantOfferLetter() {
        long projectId = 123L;
        String expectedUrl = projectRestURL + "/" + projectId + "/grant-offer/submit";
        setupPostWithRestResultExpectations(expectedUrl, OK);

        RestResult<Void> result = service.submitGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void sendGrantOfferLetter() {
        long projectId = 123L;

        String expectedUrl = projectRestURL + "/" + projectId + "/grant-offer/send";
        setupPostWithRestResultExpectations(expectedUrl, OK);

        RestResult<Void> result = service.sendGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void approveSignedGrantOfferLetter() {
        long projectId = 123L;

        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(ApprovalType.APPROVED, null);
        String expectedUrl = projectRestURL + "/" + projectId + "/signed-grant-offer-letter/approval/";
        setupPostWithRestResultExpectations(expectedUrl, grantOfferLetterApprovalResource, OK);

        RestResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource);

        setupPostWithRestResultVerifications(expectedUrl, Void.class, grantOfferLetterApprovalResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void getGrantOfferLetterState() {
        long projectId = 123L;

        String nonBaseUrl = projectRestURL + "/" + projectId + "/grant-offer-letter/current-state";
        GrantOfferLetterStateResource state = GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.APPROVED, GrantOfferLetterEvent.SIGNED_GOL_APPROVED);

        setupGetWithRestResultExpectations(nonBaseUrl, GrantOfferLetterStateResource.class, state, OK);

        RestResult<GrantOfferLetterStateResource> result = service.getGrantOfferLetterState(projectId);

        assertTrue(result.isSuccess());
        assertSame(state, result.getSuccess());
    }

    @Override
    protected GrantOfferLetterRestServiceImpl registerRestServiceUnderTest() {
        GrantOfferLetterRestServiceImpl grantOfferLetterRestService = new GrantOfferLetterRestServiceImpl();
        ReflectionTestUtils.setField(grantOfferLetterRestService, "projectRestURL", projectRestURL);
        return grantOfferLetterRestService;
    }

}