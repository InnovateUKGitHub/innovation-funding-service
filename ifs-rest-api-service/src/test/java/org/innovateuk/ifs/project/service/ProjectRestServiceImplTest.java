package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectUserResourceList;






import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.*;

public class ProjectRestServiceImplTest extends BaseRestServiceUnitTest<ProjectRestServiceImpl> {
    private static final String projectRestURL = "/project";

    @Override
    protected ProjectRestServiceImpl registerRestServiceUnderTest() {
        ProjectRestServiceImpl projectService = new ProjectRestServiceImpl();
        ReflectionTestUtils.setField(projectService, "projectRestURL", projectRestURL);
        return projectService;
    }

    @Test
    public void testGetProjectById() {
        ProjectResource returnedResponse = new ProjectResource();
        setupGetWithRestResultExpectations(projectRestURL + "/123", ProjectResource.class, returnedResponse);
        ProjectResource result = service.getProjectById(123L).getSuccessObject();
        Assert.assertEquals(returnedResponse, result);
    }

    @Test
    public void testGetStatusByProjectId() {
        ProjectStatusResource returnedResponse = new ProjectStatusResource();
        setupGetWithRestResultExpectations(projectRestURL + "/123/status", ProjectStatusResource.class, returnedResponse);
        ProjectStatusResource result = service.getProjectStatus(123L).getSuccessObject();
        Assert.assertEquals(returnedResponse, result);
    }


    @Test
    public void testUpdateFinanceContact() {
        setupPostWithRestResultExpectations(projectRestURL + "/123/organisation/5/finance-contact?financeContact=6", null, OK);
        RestResult<Void> result = service.updateFinanceContact(new ProjectOrganisationCompositeId(123L, 5L), 6L);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetProjectUsers() {
        List<ProjectUserResource> users = Arrays.asList(1,2,3).stream().map(i -> new ProjectUserResource()).collect(Collectors.toList());
        setupGetWithRestResultExpectations(projectRestURL + "/123/project-users", projectUserResourceList(), users);
        RestResult<List<ProjectUserResource>> result = service.getProjectUsersForProject(123L);
        assertTrue(result.isSuccess());
        Assert.assertEquals(users, result.getSuccessObject());
    }

    @Test
    public void testUpdateProjectAddress() {

        AddressResource addressResource = new AddressResource();

        setupPostWithRestResultExpectations(projectRestURL + "/123/address?addressType=" + OrganisationAddressType.REGISTERED.name() + "&leadOrganisationId=456", addressResource, OK);

        RestResult<Void> result = service.updateProjectAddress(456L, 123L, OrganisationAddressType.REGISTERED, addressResource);

        assertTrue(result.isSuccess());

    }

    @Test
    public void testFindByUserId() {

        List<ProjectResource> projects = Arrays.asList(1,2,3).stream().map(i -> new ProjectResource()).collect(Collectors.toList());

        setupGetWithRestResultExpectations(projectRestURL + "/user/" + 1L, projectResourceListType(), projects);

        RestResult<List<ProjectResource>> result = service.findByUserId(1L);

        assertTrue(result.isSuccess());

        Assert.assertEquals(projects, result.getSuccessObject());

    }

    @Test
    public void testGetByApplicationId() {
        ProjectResource projectResource = new ProjectResource();

        setupGetWithRestResultExpectations(projectRestURL + "/application/" + 123L, ProjectResource.class, projectResource);

        RestResult<ProjectResource> result = service.getByApplicationId(123L);

        assertTrue(result.isSuccess());

        Assert.assertEquals(projectResource, result.getSuccessObject());
    }

    @Test
    public void testSetApplicationDetailsSubmitted() {
        setupPostWithRestResultExpectations(projectRestURL + "/" + 123L + "/setApplicationDetailsSubmitted", null, OK);

        RestResult<Void> result = service.setApplicationDetailsSubmitted(123L);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testIsSubmitAllowed() {
        Boolean isAllowed = true;

        setupGetWithRestResultExpectations(projectRestURL + "/" + 123L + "/isSubmitAllowed", Boolean.class, isAllowed);

        RestResult<Boolean> result = service.isSubmitAllowed(123L);

        assertTrue(result.isSuccess());

        Assert.assertEquals(isAllowed, result.getSuccessObject());
    }

    @Test
    public void testUpdateMonitoringOfficer() {

        Long projectId = 1L;

        MonitoringOfficerResource monitoringOfficerResource = new MonitoringOfficerResource();
        monitoringOfficerResource.setId(null);
        monitoringOfficerResource.setProject(projectId);
        monitoringOfficerResource.setFirstName("abc");
        monitoringOfficerResource.setLastName("xyz");
        monitoringOfficerResource.setEmail("abc.xyz@gmail.com");
        monitoringOfficerResource.setPhoneNumber("078323455");

        setupPutWithRestResultExpectations(projectRestURL + "/" + projectId + "/monitoring-officer", monitoringOfficerResource, OK);

        RestResult<Void> result = service.updateMonitoringOfficer(projectId, "abc", "xyz", "abc.xyz@gmail.com", "078323455");

        assertTrue(result.isSuccess());

    }

    @Test
    public void testGetMonitoringOfficerForProject() {

        MonitoringOfficerResource expectedMonitoringOfficerResource = new MonitoringOfficerResource();
        expectedMonitoringOfficerResource.setProject(1L);
        expectedMonitoringOfficerResource.setFirstName("abc");
        expectedMonitoringOfficerResource.setLastName("xyz");
        expectedMonitoringOfficerResource.setEmail("abc.xyz@gmail.com");
        expectedMonitoringOfficerResource.setPhoneNumber("078323455");

        setupGetWithRestResultExpectations(projectRestURL + "/1/monitoring-officer", MonitoringOfficerResource.class, expectedMonitoringOfficerResource);

        RestResult<MonitoringOfficerResource> result = service.getMonitoringOfficerForProject(1L);

        assertTrue(result.isSuccess());

        Assert.assertEquals(expectedMonitoringOfficerResource, result.getSuccessObject());

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

    @Test
    public void testGetProjectTeamStatus() {
        String expectedUrl = projectRestURL + "/123/team-status";

        setupGetWithRestResultExpectations(expectedUrl, ProjectTeamStatusResource.class, null, OK);

        RestResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(123L, Optional.empty());

        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetProjectTeamStatusWithFilterByUserId() {
        String expectedUrl = projectRestURL + "/123/team-status?filterByUserId=456";

        setupGetWithRestResultExpectations(expectedUrl, ProjectTeamStatusResource.class, null, OK);

        RestResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(123L, Optional.of(456L));

        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetSignedGrantOfferLetterFileContent() {

        String expectedUrl = projectRestURL + "/123/signed-grant-offer";
        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, returnedFileContents, OK);

        // now run the method under test
        ByteArrayResource retrievedFileEntry = service.getSignedGrantOfferLetterFile(123L).getSuccessObject().get();

        assertEquals(returnedFileContents, retrievedFileEntry);
    }

    @Test
    public void testGetSignedGrantOfferLetterFileContentEmptyIfNotFound() {

        String expectedUrl = projectRestURL + "/123/signed-grant-offer";

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, null, NOT_FOUND);

        // now run the method under test
        Optional<ByteArrayResource> retrievedFileEntry = service.getSignedGrantOfferLetterFile(123L).getSuccessObject();

        assertFalse(retrievedFileEntry.isPresent());
    }

    @Test
    public void testGetGeneratedGrantOfferLetterFileContent() {

        String expectedUrl = projectRestURL + "/123/grant-offer";
        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, returnedFileContents, OK);

        // now run the method under test
        ByteArrayResource retrievedFileEntry = service.getGrantOfferFile(123L).getSuccessObject().get();

        assertEquals(returnedFileContents, retrievedFileEntry);
    }

    @Test
    public void testGetGeneratedGrantOfferLetterFileContentEmptyIfNotFound() {

        String expectedUrl = projectRestURL + "/123/grant-offer";

        setupGetWithRestResultExpectations(expectedUrl, ByteArrayResource.class, null, NOT_FOUND);

        // now run the method under test
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
    public void testInviteProjectManager() {
        long projectId = 123L;
        InviteProjectResource invite = new InviteProjectResource();

        String expectedUrl = projectRestURL + "/" + projectId + "/invite-project-manager";
        setupPostWithRestResultExpectations(expectedUrl, invite, OK);

        RestResult<Void> result = service.inviteProjectManager(projectId, invite);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testInviteFinanceContact() {
        long projectId = 123L;
        InviteProjectResource invite = new InviteProjectResource();

        String expectedUrl = projectRestURL + "/" + projectId + "/invite-finance-contact";
        setupPostWithRestResultExpectations(expectedUrl, invite, OK);

        RestResult<Void> result = service.inviteFinanceContact(projectId, invite);

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

    @Test
    public void testGetProjectManager() {
        ProjectUserResource returnedResponse = new ProjectUserResource();
        setupGetWithRestResultExpectations(projectRestURL + "/123/project-manager", ProjectUserResource.class, returnedResponse);
        ProjectUserResource result = service.getProjectManager(123L).getSuccessObject();
        Assert.assertEquals(returnedResponse, result);
    }

    @Test
    public void testGetProjectManagerNotFound() {
        setupGetWithRestResultExpectations(projectRestURL + "/123/project-manager", ProjectUserResource.class, null, NOT_FOUND);
        Optional<ProjectUserResource> result = service.getProjectManager(123L).toOptionalIfNotFound().getSuccessObject();
        Assert.assertFalse(result.isPresent());
    }

}
