package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.applicationFinanceResourceListType;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
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

        ApplicationFinanceResource returnedResponse = new ApplicationFinanceResource();

        setupGetWithRestResultExpectations(applicationFinanceRestURL + "/find-by-application-organisation/123/456", ApplicationFinanceResource.class, returnedResponse);

        ApplicationFinanceResource finance = service.getApplicationFinance(123L, 456L).getSuccess();
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
        List<ApplicationFinanceResource> returnedResponse = Stream.of(1, 2, 3).map(i -> new ApplicationFinanceResource()).collect(Collectors.toList());//.build(3);
        setupGetWithRestResultExpectations(applicationFinanceRestURL + "/find-by-application/123", applicationFinanceResourceListType(), returnedResponse);
        List<ApplicationFinanceResource> finances = service.getApplicationFinances(123L).getSuccess();
        assertEquals(returnedResponse, finances);
    }

    @Test
    public void test_getApplicationFinances_forApplicationId_nullSafe() {
        assertNull(service.getApplicationFinances(null));
    }

    @Test
    public void test_getFileDetails() {

        FileEntryResource returnedResponse = new FileEntryResource();

        setupGetWithRestResultExpectations(applicationFinanceRestURL + "/finance-document/fileentry?applicationFinanceId=123", FileEntryResource.class, returnedResponse);

        FileEntryResource fileDetails = service.getFileDetails(123L).getSuccess();
        Assert.assertEquals(returnedResponse, fileDetails);
    }

    @Test
    public void test_getApplicationFinanceDetails_forApplicationId() {
        List<ApplicationFinanceResource> returnedResponse = newApplicationFinanceResource().withApplication(1L).build(3);
        setupGetWithRestResultExpectations(applicationFinanceRestURL + "/finance-details/123", applicationFinanceResourceListType(), returnedResponse);
        List<ApplicationFinanceResource> finances = service.getFinanceDetails(123L).getSuccess();
        assertEquals(returnedResponse, finances);
    }
}
