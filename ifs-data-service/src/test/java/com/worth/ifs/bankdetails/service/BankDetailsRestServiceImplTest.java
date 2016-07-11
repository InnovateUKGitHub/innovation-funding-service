package com.worth.ifs.bankdetails.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worth.ifs.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static org.junit.Assert.assertEquals;

public class BankDetailsRestServiceImplTest extends BaseRestServiceUnitTest<BankDetailsRestServiceImpl> {
    final String projectRestURL = "/project";
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
        BankDetailsResource response = service.getById(projectId, bankDetailsId).getSuccessObject();
        assertEquals(response, returnedResponse);
    }

    @Test
    public void testGetBankDetailsByProjectAndOrganisation(){
        Long projectId = 123L;
        Long organisationId = 100L;
        BankDetailsResource returnedResponse = newBankDetailsResource().build();
        setupGetWithRestResultExpectations(projectRestURL + "/" + projectId + "/bank-details?organisationId=" + organisationId, BankDetailsResource.class, returnedResponse);
        BankDetailsResource response = service.getBankDetailsByProjectAndOrganisation(projectId, organisationId).getSuccessObject();
        assertEquals(response, returnedResponse);
    }
}
