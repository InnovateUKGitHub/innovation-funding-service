package org.innovateuk.ifs.project.bankdetails.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.bankDetailsReviewResourceListType;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static org.innovateuk.ifs.project.bankdetails.builder.ProjectBankDetailsStatusSummaryBuilder.newProjectBankDetailsStatusSummary;
import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class BankDetailsRestServiceImplTest extends BaseRestServiceUnitTest<BankDetailsRestServiceImpl> {
    private String competitionRestURL = "/competition";
    private String competitionsRestURL = "/competitions";
    private final String projectRestURL = "/project";
    
    @Override
    protected BankDetailsRestServiceImpl registerRestServiceUnderTest() {
        BankDetailsRestServiceImpl bankDetailsRestService = new BankDetailsRestServiceImpl();
        ReflectionTestUtils.setField(bankDetailsRestService, "projectRestURL", projectRestURL);
        return bankDetailsRestService;
    }
    
    @Test
    public void testGetById(){
        Long projectId = 123L;
        Long bankDetailsId = 1L;
        BankDetailsResource returnedResponse = newBankDetailsResource().build();
        setupGetWithRestResultExpectations(projectRestURL + "/" + projectId + "/bank-details?bankDetailsId=" + bankDetailsId, BankDetailsResource.class, returnedResponse);
        BankDetailsResource response = service.getByProjectIdAndBankDetailsId(projectId, bankDetailsId).getSuccess();
        assertEquals(response, returnedResponse);
    }
    
    @Test
    public void testGetBankDetailsByProjectAndOrganisation(){
        Long projectId = 123L;
        Long organisationId = 100L;
        BankDetailsResource returnedResponse = newBankDetailsResource().build();
        setupGetWithRestResultExpectations(projectRestURL + "/" + projectId + "/bank-details?organisationId=" + organisationId, BankDetailsResource.class, returnedResponse);
        BankDetailsResource response = service.getBankDetailsByProjectAndOrganisation(projectId, organisationId).getSuccess();
        assertEquals(response, returnedResponse);
    }
    
    @Test
    public void testGetBankDetailsByProjectAndOrganisationReturnsNotFoundWhenBankDetailsDontExist(){
        Long projectId = 123L;
        Long organisationId = 100L;
        setupGetWithRestResultExpectations(projectRestURL + "/" + projectId + "/bank-details?organisationId=" + organisationId, BankDetailsResource.class, null, NOT_FOUND);
        RestResult<BankDetailsResource> response = service.getBankDetailsByProjectAndOrganisation(projectId, organisationId);
        assertTrue(response.isFailure());
        assertEquals(response.getFailure().getStatusCode(), HttpStatus.NOT_FOUND);
    }
    
    @Test
    public void testUpdateBankDetails(){
        Long projectId = 123L;
        BankDetailsResource bankDetailsResource = newBankDetailsResource().build();
        setupPostWithRestResultExpectations(projectRestURL + "/" + projectId + "/bank-details", bankDetailsResource, OK);
        RestResult result = service.updateBankDetails(projectId, bankDetailsResource);
        assertTrue(result.isSuccess());
    }
    
    @Test
    public void testSubmitBankDetails(){
        Long projectId = 123L;
        BankDetailsResource bankDetailsResource = newBankDetailsResource().build();
        setupPutWithRestResultExpectations(projectRestURL + "/" + projectId + "/bank-details", bankDetailsResource, OK);
        RestResult result = service.submitBankDetails(projectId, bankDetailsResource);
        assertTrue(result.isSuccess());
    }
    
    @Test
    public void testgetBankDetailsByProject(){
        Long projectId = 123L;
        ProjectBankDetailsStatusSummary projectBankDetailsStatusSummary = newProjectBankDetailsStatusSummary().build();
        setupGetWithRestResultExpectations(projectRestURL + "/" + projectId + "/bank-details/status-summary", ProjectBankDetailsStatusSummary.class, projectBankDetailsStatusSummary, OK);
        RestResult<ProjectBankDetailsStatusSummary> response = service.getBankDetailsStatusSummaryByProject(projectId);
        assertTrue(response.isSuccess());
        assertEquals(projectBankDetailsStatusSummary, response.getSuccess());
    }
    
    @Test
    public void testDownloadByCompetition() {
        Long competitionId = 123L;
        ByteArrayResource returnedFileContents = new ByteArrayResource("Retrieved content".getBytes());
        String url = competitionRestURL + "/" + competitionId + "/bank-details/export";
        setupGetWithRestResultExpectations(url, ByteArrayResource.class, returnedFileContents, OK);
        ByteArrayResource retrievedFileEntry = service.downloadByCompetition(123L).getSuccess();
        assertEquals(returnedFileContents, retrievedFileEntry);
    }

    @Test
    public void getPendingBankDetailsApprovals() {

        List<BankDetailsReviewResource> returnedResponse = singletonList(new BankDetailsReviewResource());

        setupGetWithRestResultExpectations(competitionsRestURL + "/pending-bank-details-approvals", bankDetailsReviewResourceListType(), returnedResponse);

        List<BankDetailsReviewResource> response = service.getPendingBankDetailsApprovals().getSuccess();
        assertNotNull(response);
        Assert.assertEquals(returnedResponse, response);
    }

    @Test
    public void countPendingBankDetailsApprovals() {

        Long pendingBankDetailsCount = 8L;

        setupGetWithRestResultExpectations(competitionsRestURL + "/count-pending-bank-details-approvals", Long.class, pendingBankDetailsCount);

        Long response = service.countPendingBankDetailsApprovals().getSuccess();
        assertNotNull(response);
        Assert.assertEquals(pendingBankDetailsCount, response);
    }
}
