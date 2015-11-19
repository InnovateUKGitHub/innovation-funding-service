package com.worth.ifs.finance.service;

import com.worth.ifs.BaseRestServiceMocksTest;
import com.worth.ifs.finance.domain.ApplicationFinance;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static com.worth.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
public class ApplicationFinanceRestServiceMocksTest extends BaseRestServiceMocksTest<ApplicationFinanceRestServiceImpl> {

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
}
