package com.worth.ifs.finance.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.finance.domain.FinanceRow;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.costListType;
import static com.worth.ifs.finance.builder.FinanceRowBuilder.newFinanceRow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
public class FinanceRowRestServiceMocksTest extends BaseRestServiceUnitTest<FinanceRowRestServiceImpl> {

    private static final String costRestURL = "/cost";

    @Override
    protected FinanceRowRestServiceImpl registerRestServiceUnderTest() {
        FinanceRowRestServiceImpl costService = new FinanceRowRestServiceImpl();
        return costService;
    }

    //@Test
    public void test_getCosts_forApplicationFinanceId() {

        List<FinanceRow> returnedResponse = newFinanceRow().build(3);

        setupGetWithRestResultExpectations(costRestURL + "/get/123", costListType(), returnedResponse);
        /* List<FinanceRow> costs = service.getCosts(123L);
        assertNotNull(costs);
        assertEquals(returnedResponse[0], costs.get(0));
        assertEquals(returnedResponse[1], costs.get(1));
        assertEquals(returnedResponse[2], costs.get(2)); */
    }

    //@Test
    public void test_findById() {

        String expectedUrl = dataServicesUrl + costRestURL + "/findById/123";
        FinanceRow returnedResponse = newFinanceRow().build();
        ResponseEntity<FinanceRow> returnedEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), FinanceRow.class)).thenReturn(returnedEntity);

        /* FinanceRow cost = service.findById(123L);
        assertNotNull(cost);
        assertEquals(returnedResponse, cost); */
    }

    //@Test
    public void test_add_byApplicationFinanceIdAndQuestionId() {

        String expectedUrl = dataServicesUrl + costRestURL + "/add/123/456";
        service.add(123L, 456L, null);
        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
    }

    @Test
    public void test_delete_byCostId() {
        setupDeleteWithRestResultExpectations(costRestURL + "/delete/123");
        service.delete(123L);
        setupDeleteWithRestResultVerifications(costRestURL + "/delete/123");
    }

    //@Test
    public void test_update_byCost() {

        FinanceRow costToUpdate = newFinanceRow().with(id(123L)).build();

        String expectedUrl = dataServicesUrl + costRestURL + "/update/123";

        //service.update(costToUpdate);
        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(costToUpdate), Void.class);
    }
}
