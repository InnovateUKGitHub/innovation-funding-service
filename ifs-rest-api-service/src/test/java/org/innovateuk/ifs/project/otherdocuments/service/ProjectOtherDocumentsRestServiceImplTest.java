package org.innovateuk.ifs.project.otherdocuments.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class ProjectOtherDocumentsRestServiceImplTest extends BaseRestServiceUnitTest<ProjectOtherDocumentsRestServiceImpl> {
    private static final String projectRestURL = "/project";

    @Override
    protected ProjectOtherDocumentsRestServiceImpl registerRestServiceUnderTest() {
        ProjectOtherDocumentsRestServiceImpl projectOtherDocumentsRestService = new ProjectOtherDocumentsRestServiceImpl();
        ReflectionTestUtils.setField(projectOtherDocumentsRestService, "projectRestURL", projectRestURL);
        return projectOtherDocumentsRestService;
    }

    @Test
    public void testAddCollaborationAgreementDocument() {

        String fileContentString = "12345678901234567";
        byte[] fileContent = fileContentString.getBytes();

        FileEntryResource response = new FileEntryResource();

        setupFileUploadWithRestResultExpectations(
                projectRestURL + "/123/collaboration-agreement?filename=filename.txt", FileEntryResource.class,
                fileContentString, "text/plain", 17, response, CREATED);

        RestResult<FileEntryResource> result =
                service.addCollaborationAgreementDocument(123L, "text/plain", 17, "filename.txt", fileContent);

        assertTrue(result.isSuccess());
        Assert.assertEquals(response, result.getSuccessObject());
    }

    @Test
    public void testGetCollaborationAgreementDocumentDetails() {
        String expectedUrl = projectRestURL + "/123/collaboration-agreement/details";
        FileEntryResource returnedFileEntry = new FileEntryResource();
        setupGetWithRestResultExpectations(expectedUrl, FileEntryResource.class, returnedFileEntry, OK);
        // now run the method under test
        FileEntryResource retrievedFileEntry = service.getCollaborationAgreementFileDetails(123L).getSuccessObject().get();
        Assert.assertEquals(returnedFileEntry, retrievedFileEntry);
    }

    @Test
    public void testGetCollaborationAgreementDocumentDetailsEmptyIfNotFound() {
        String expectedUrl = projectRestURL + "/123/collaboration-agreement/details";
        setupGetWithRestResultExpectations(expectedUrl, FileEntryResource.class, null, NOT_FOUND);
        // now run the method under test
        Optional<FileEntryResource> retrievedFileEntry = service.getCollaborationAgreementFileDetails(123L).getSuccessObject();
        assertFalse(retrievedFileEntry.isPresent());
    }

    @Test
    public void testGetCollaborationAgreementDocumentDetailsErrorIfErrorDifferentFromNotFound() {
        String expectedUrl = projectRestURL + "/123/collaboration-agreement/details";
        setupGetWithRestResultExpectations(expectedUrl, FileEntryResource.class, null, NOT_ACCEPTABLE);
        // now run the method under test
        RestResult<Optional<FileEntryResource>> result = service.getCollaborationAgreementFileDetails(123L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(CommonFailureKeys.GENERAL_REST_RESULT_UNEXPECTED_STATUS_CODE, NOT_ACCEPTABLE)));
    }

    @Test
    public void testGetCollaborationAgreementDocumentContent() {

        String expectedUrl = projectRestURL + "/123/collaboration-agreement";
        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, returnedFileContents, OK);

        // now run the method under test
        ByteArrayResource retrievedFileEntry = service.getCollaborationAgreementFile(123L).getSuccessObject().get();

        assertEquals(returnedFileContents, retrievedFileEntry);
    }

    @Test
    public void testGetCollaborationAgreementDocumentContentEmptyIfNotFound() {

        String expectedUrl = projectRestURL + "/123/collaboration-agreement";

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, null, NOT_FOUND);

        // now run the method under test
        Optional<ByteArrayResource> retrievedFileEntry = service.getCollaborationAgreementFile(123L).getSuccessObject();

        assertFalse(retrievedFileEntry.isPresent());
    }

    @Test
    public void testGetCollaborationAgreementDocumentContentErrorIfErrorDifferentFromNotFound() {

        String expectedUrl = projectRestURL + "/123/collaboration-agreement";

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, null, NOT_ACCEPTABLE);

        // now run the method under test
        RestResult<Optional<ByteArrayResource>> result = service.getCollaborationAgreementFile(123L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(CommonFailureKeys.GENERAL_REST_RESULT_UNEXPECTED_STATUS_CODE, NOT_ACCEPTABLE)));
    }

    @Test
    public void testDeleteCollaborationAgreementDocumentDetails() {

        String expectedUrl = projectRestURL + "/123/collaboration-agreement";

        setupDeleteWithRestResultExpectations(expectedUrl);

        // now run the method under test
        service.removeCollaborationAgreementDocument(123L);

        setupDeleteWithRestResultVerifications(expectedUrl);
    }

    @Test
    public void testAddExploitationPlanDocument() {

        String fileContentString = "12345678901234567";
        byte[] fileContent = fileContentString.getBytes();

        FileEntryResource response = new FileEntryResource();

        setupFileUploadWithRestResultExpectations(
                projectRestURL + "/123/exploitation-plan?filename=filename.txt", FileEntryResource.class,
                fileContentString, "text/plain", 17, response, CREATED);

        RestResult<FileEntryResource> result =
                service.addExploitationPlanDocument(123L, "text/plain", 17, "filename.txt", fileContent);

        assertTrue(result.isSuccess());
        Assert.assertEquals(response, result.getSuccessObject());
    }

    @Test
    public void testGetExploitationPlanDocumentDetails() {

        String expectedUrl = projectRestURL + "/123/exploitation-plan/details";
        FileEntryResource returnedFileEntry = new FileEntryResource();

        setupGetWithRestResultExpectations(expectedUrl, FileEntryResource.class, returnedFileEntry, OK);

        // now run the method under test
        FileEntryResource retrievedFileEntry = service.getExploitationPlanFileDetails(123L).getSuccessObject().get();

        Assert.assertEquals(returnedFileEntry, retrievedFileEntry);
    }

    @Test
    public void testGetExploitationPlanDocumentDetailsEmptyIfNotFound() {

        String expectedUrl = projectRestURL + "/123/exploitation-plan/details";

        setupGetWithRestResultExpectations(expectedUrl, FileEntryResource.class, null, NOT_FOUND);

        // now run the method under test
        Optional<FileEntryResource> retrievedFileEntry = service.getExploitationPlanFileDetails(123L).getSuccessObject();

        assertFalse(retrievedFileEntry.isPresent());
    }

    @Test
    public void testGetExploitationPlanDocumentDetailsErrorIfErrorDifferentFromNotFound() {

        String expectedUrl = projectRestURL + "/123/exploitation-plan/details";

        setupGetWithRestResultExpectations(expectedUrl, FileEntryResource.class, null, NOT_ACCEPTABLE);

        // now run the method under test
        RestResult<Optional<FileEntryResource>> result = service.getExploitationPlanFileDetails(123L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(CommonFailureKeys.GENERAL_REST_RESULT_UNEXPECTED_STATUS_CODE, NOT_ACCEPTABLE)));
    }

    @Test
    public void testGetExploitationPlanDocumentContent() {

        String expectedUrl = projectRestURL + "/123/exploitation-plan";
        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, returnedFileContents, OK);

        // now run the method under test
        ByteArrayResource retrievedFileEntry = service.getExploitationPlanFile(123L).getSuccessObject().get();

        assertEquals(returnedFileContents, retrievedFileEntry);
    }

    @Test
    public void testGetExploitationPlanDocumentContentEmptyIfNotFound() {

        String expectedUrl = projectRestURL + "/123/exploitation-plan";

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, null, NOT_FOUND);

        // now run the method under test
        Optional<ByteArrayResource> retrievedFileEntry = service.getExploitationPlanFile(123L).getSuccessObject();

        assertFalse(retrievedFileEntry.isPresent());
    }

    @Test
    public void testGetExploitationPlanDocumentContentErrorIfErrorDifferentFromNotFound() {

        String expectedUrl = projectRestURL + "/123/exploitation-plan";

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, null, NOT_ACCEPTABLE);

        // now run the method under test
        RestResult<Optional<ByteArrayResource>> result = service.getExploitationPlanFile(123L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(CommonFailureKeys.GENERAL_REST_RESULT_UNEXPECTED_STATUS_CODE, NOT_ACCEPTABLE)));
    }

    @Test
    public void testDeleteExploitationPlanDocumentDetails() {

        String expectedUrl = projectRestURL + "/123/exploitation-plan";

        setupDeleteWithRestResultExpectations(expectedUrl);

        // now run the method under test
        service.removeExploitationPlanDocument(123L);

        setupDeleteWithRestResultVerifications(expectedUrl);
    }

    @Test
    public void testAcceptOrRejectOtherDocuments() {

        setupPostWithRestResultExpectations(projectRestURL + "/" + 123L + "/partner/documents/approved/" + true, OK);

        // now run the method under test
        RestResult<Void> result = service.acceptOrRejectOtherDocuments(123L, true);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testSetPartnerDocumentsSubmitted() {
        setupPostWithRestResultExpectations(projectRestURL + "/" + 123L + "/partner/documents/submit", null, OK);

        RestResult<Void> result = service.setPartnerDocumentsSubmitted(123L);

        assertTrue(result.isSuccess());
    }
}
