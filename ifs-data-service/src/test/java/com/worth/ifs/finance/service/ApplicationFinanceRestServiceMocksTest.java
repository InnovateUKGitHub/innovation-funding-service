package com.worth.ifs.finance.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.finance.domain.ApplicationFinance;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.worth.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
public class ApplicationFinanceRestServiceMocksTest extends BaseRestServiceUnitTest<ApplicationFinanceRestServiceImpl> {

    private static final String applicationFinanceRestURL = "/finance";

    @Override
    protected ApplicationFinanceRestServiceImpl registerRestServiceUnderTest() {
        ApplicationFinanceRestServiceImpl financeService = new ApplicationFinanceRestServiceImpl();
        financeService.applicationFinanceRestURL = applicationFinanceRestURL;
        return financeService;
    }

    @Test
    public void test_getApplicationFinance_forApplicationIdAndOrganisationId() {

        String expectedUrl = dataServicesUrl + applicationFinanceRestURL + "/findByApplicationOrganisation/123/456";
        ApplicationFinance returnedResponse = newApplicationFinance().build();
        ResponseEntity<ApplicationFinance> returnedEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), ApplicationFinance.class)).thenReturn(returnedEntity);

        ApplicationFinance finance = service.getApplicationFinance(123L, 456L);
        assertNotNull(finance);
        assertEquals(returnedResponse, finance);
    }

    @Test
    public void test_getApplicationFinance_forApplicationIdAndOrganisationId_nullSafe() {
        assertNull(service.getApplicationFinance(123L, null));
        assertNull(service.getApplicationFinance(null, 456L));
        assertNull(service.getApplicationFinance(null, null));
    }

    @Test
    public void test_getApplicationFinances_forApplicationId() {

        String expectedUrl = dataServicesUrl + applicationFinanceRestURL + "/findByApplication/123";
        ApplicationFinance[] returnedResponse = newApplicationFinance().buildArray(3, ApplicationFinance.class);
        ResponseEntity<ApplicationFinance[]> returnedEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), ApplicationFinance[].class)).thenReturn(returnedEntity);

        List<ApplicationFinance> finances = service.getApplicationFinances(123L);
        assertNotNull(finances);
        assertEquals(returnedResponse[0], finances.get(0));
        assertEquals(returnedResponse[1], finances.get(1));
        assertEquals(returnedResponse[2], finances.get(2));
    }


    @Test
    public void test_getApplicationFinances_forApplicationId_nullSafe() {
        assertNull(service.getApplicationFinances(null));
    }

    @Test
    public void test_addApplicationFinance_forApplicationIdAndOrganisationId() {

        String expectedUrl = dataServicesUrl + applicationFinanceRestURL + "/add/123/456";
        ApplicationFinance returnedResponse = newApplicationFinance().build();
        ResponseEntity<ApplicationFinance> returnedEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.postForEntity(expectedUrl, httpEntityForRestCall(null), ApplicationFinance.class)).thenReturn(returnedEntity);

        ApplicationFinance finance = service.addApplicationFinanceForOrganisation(123L, 456L);
        assertNotNull(finance);
        assertEquals(returnedResponse, finance);
    }

    @Test
    public void test_addApplicationFinance_nullSafe() {
        assertNull(service.addApplicationFinanceForOrganisation(123L, null));
        assertNull(service.addApplicationFinanceForOrganisation(null, 456L));
        assertNull(service.addApplicationFinanceForOrganisation(null, null));
    }

}
