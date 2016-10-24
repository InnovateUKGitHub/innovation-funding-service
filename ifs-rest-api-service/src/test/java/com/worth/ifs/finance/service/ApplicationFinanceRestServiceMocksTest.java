package com.worth.ifs.finance.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.applicationFinanceResourceListType;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpStatus.OK;

public class ApplicationFinanceRestServiceMocksTest extends BaseRestServiceUnitTest<ApplicationFinanceRestServiceImpl> {
    private static final String applicationFinanceRestURL = "/applicationfinance";

    @Override
    protected ApplicationFinanceRestServiceImpl registerRestServiceUnderTest() {
        ApplicationFinanceRestServiceImpl financeService = new ApplicationFinanceRestServiceImpl();
        return financeService;
    }

    @Test
    public void test_getApplicationFinance_forApplicationIdAndOrganisationId() {

        ApplicationFinanceResource returnedResponse = newApplicationFinanceResource().build();

        setupGetWithRestResultExpectations(applicationFinanceRestURL + "/findByApplicationOrganisation/123/456", ApplicationFinanceResource.class, returnedResponse);

        ApplicationFinanceResource finance = service.getApplicationFinance(123L, 456L).getSuccessObject();
        Assert.assertEquals(returnedResponse, finance);
    }

    @Test
    public void test_getApplicationFinance_forApplicationIdAndOrganisationId_nullSafe() {
        assertNull(service.getApplicationFinance(123L, null));
        assertNull(service.getApplicationFinance(null, 456L));
        assertNull(service.getApplicationFinance(null, null));
    }

    @Test
    public void test_getApplicationFinances_forApplicationId() {
        List<ApplicationFinanceResource> returnedResponse = newApplicationFinanceResource().build(3);

        setupGetWithRestResultExpectations(applicationFinanceRestURL + "/findByApplication/123", applicationFinanceResourceListType(), returnedResponse);

        List<ApplicationFinanceResource> finances = service.getApplicationFinances(123L).getSuccessObject();
        assertEquals(returnedResponse, finances);
    }

    @Test
    public void test_getApplicationFinances_forApplicationId_nullSafe() {
        assertNull(service.getApplicationFinances(null));
    }

    @Test
    public void test_addApplicationFinance_forApplicationIdAndOrganisationId() {
        ApplicationFinanceResource returnedResponse = newApplicationFinanceResource().build();

        setupPostWithRestResultExpectations(applicationFinanceRestURL + "/add/123/456", ApplicationFinanceResource.class, null, returnedResponse, OK);

        ApplicationFinanceResource finance = service.addApplicationFinanceForOrganisation(123L, 456L).getSuccessObject();
        Assert.assertEquals(returnedResponse, finance);
    }

    @Test
    public void test_getFileDetails() {

        FileEntryResource returnedResponse = newFileEntryResource().build();

        setupGetWithRestResultExpectations(applicationFinanceRestURL + "/financeDocument/fileentry?applicationFinanceId=123", FileEntryResource.class, returnedResponse);

        FileEntryResource fileDetails = service.getFileDetails(123L).getSuccessObject();
        Assert.assertEquals(returnedResponse, fileDetails);
    }

    @Test
    public void test_addApplicationFinance_nullSafe() {
        assertNull(service.addApplicationFinanceForOrganisation(123L, null));
        assertNull(service.addApplicationFinanceForOrganisation(null, 456L));
        assertNull(service.addApplicationFinanceForOrganisation(null, null));
    }
}
