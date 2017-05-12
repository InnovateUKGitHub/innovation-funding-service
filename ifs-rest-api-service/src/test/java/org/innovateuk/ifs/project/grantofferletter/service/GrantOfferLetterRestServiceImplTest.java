package org.innovateuk.ifs.project.grantofferletter.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.grantofferletter.resource.GOLState;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class GrantOfferLetterRestServiceImplTest extends BaseRestServiceUnitTest<GrantOfferLetterRestServiceImpl> {
    private static final String projectRestURL = "/project";

    @Test
    public void testGetSignedGrantOfferLetterFileContent() {

        String expectedUrl = projectRestURL + "/123/signed-grant-offer";
        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, returnedFileContents, OK);

        ByteArrayResource retrievedFileEntry = service.getSignedGrantOfferLetterFile(123L).getSuccessObject().get();

        assertEquals(returnedFileContents, retrievedFileEntry);
    }

    @Test
    public void testGetSignedGrantOfferLetterFileContentEmptyIfNotFound() {

        String expectedUrl = projectRestURL + "/123/signed-grant-offer";

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, null, NOT_FOUND);

        Optional<ByteArrayResource> retrievedFileEntry = service.getSignedGrantOfferLetterFile(123L).getSuccessObject();

        assertFalse(retrievedFileEntry.isPresent());
    }

    @Test
    public void testGetGeneratedGrantOfferLetterFileContent() {

        String expectedUrl = projectRestURL + "/123/grant-offer";
        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, returnedFileContents, OK);

        ByteArrayResource retrievedFileEntry = service.getGrantOfferFile(123L).getSuccessObject().get();

        assertEquals(returnedFileContents, retrievedFileEntry);
    }

    @Test
    public void testGetGeneratedGrantOfferLetterFileContentEmptyIfNotFound() {

        String expectedUrl = projectRestURL + "/123/grant-offer";

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, null, NOT_FOUND);

        Optional<ByteArrayResource> retrievedFileEntry = service.getGrantOfferFile(123L).getSuccessObject();

        assertFalse(retrievedFileEntry.isPresent());
    }

    @Test
    public void testRemoveGeneratedGrantOfferLetter() {

        Long projectId = 123L;
        String nonBaseUrl = projectRestURL + "/" + projectId + "/grant-offer";

        setupDeleteWithRestResultExpectations(nonBaseUrl);

        RestResult<Void> result = service.removeGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());

        setupDeleteWithRestResultVerifications(nonBaseUrl);
    }

    @Test
    public void testRemoveSignedGrantOfferLetter() {

        Long projectId = 123L;
        String nonBaseUrl = projectRestURL + "/" + projectId + "/signed-grant-offer-letter";

        setupDeleteWithRestResultExpectations(nonBaseUrl);

        RestResult<Void> result = service.removeSignedGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());

        setupDeleteWithRestResultVerifications(nonBaseUrl);
    }

    @Test
    public void testSubmitGrantOfferLetter() {
        long projectId = 123L;
        String expectedUrl = projectRestURL + "/" + projectId + "/grant-offer/submit";
        setupPostWithRestResultExpectations(expectedUrl, OK);

        RestResult<Void> result = service.submitGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testSendGrantOfferLetter() {
        long projectId = 123L;

        String expectedUrl = projectRestURL + "/" + projectId + "/grant-offer/send";
        setupPostWithRestResultExpectations(expectedUrl, OK);

        RestResult<Void> result = service.sendGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testIsSendGrantOfferLetterAllowed() {
        long projectId = 123L;

        String expectedUrl = projectRestURL + "/" + projectId + "/is-send-grant-offer-letter-allowed";
        setupGetWithRestResultExpectations(expectedUrl, Boolean.class, Boolean.FALSE, OK);

        RestResult<Boolean> result = service.isSendGrantOfferLetterAllowed(projectId);

        assertTrue(result.isSuccess());
        assertEquals(Boolean.FALSE, result.getSuccessObject());
    }

    @Test
    public void testIsGrantOfferLetterAlreadySent() {
        long projectId = 123L;

        String expectedUrl = projectRestURL + "/" + projectId + "/is-grant-offer-letter-already-sent";
        setupGetWithRestResultExpectations(expectedUrl, Boolean.class, Boolean.TRUE, OK);

        RestResult<Boolean> result = service.isGrantOfferLetterAlreadySent(projectId);

        assertTrue(result.isSuccess());
        assertEquals(Boolean.TRUE, result.getSuccessObject());
    }

    @Test
    public void testApproveSignedGrantOfferLetter() {
        long projectId = 123L;

        String expectedUrl = projectRestURL + "/" + projectId + "/signed-grant-offer-letter/approval/" + ApprovalType.APPROVED;
        setupPostWithRestResultExpectations(expectedUrl, OK);

        RestResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, ApprovalType.APPROVED);

        setupPostWithRestResultVerifications(expectedUrl, Void.class, null);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testIsSignedGrantOfferLetterApproved() {
        long projectId = 123L;

        String expectedUrl = projectRestURL + "/" + projectId + "/signed-grant-offer-letter/approval";
        setupGetWithRestResultExpectations(expectedUrl, Boolean.class, Boolean.TRUE, OK);

        RestResult<Boolean> result = service.isSignedGrantOfferLetterApproved(projectId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetGrantOfferLetterWorkflowState() {
        long projectId = 123L;

        String nonBaseUrl = projectRestURL + "/" + projectId + "/grant-offer-letter/state";
        setupGetWithRestResultExpectations(nonBaseUrl, GOLState.class, GOLState.APPROVED, OK);

        RestResult<GOLState> result = service.getGrantOfferLetterWorkflowState(projectId);

        assertTrue(result.isSuccess());
        assertEquals(GOLState.APPROVED, result.getSuccessObject());
    }

    @Override
    protected GrantOfferLetterRestServiceImpl registerRestServiceUnderTest() {
        GrantOfferLetterRestServiceImpl grantOfferLetterRestService = new GrantOfferLetterRestServiceImpl();
        ReflectionTestUtils.setField(grantOfferLetterRestService, "projectRestURL", projectRestURL);
        return grantOfferLetterRestService;
    }

}