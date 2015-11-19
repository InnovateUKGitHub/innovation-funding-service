package com.worth.ifs.finance.service;

import com.worth.ifs.BaseRestServiceMocksTest;
import com.worth.ifs.finance.domain.Cost;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.finance.builder.CostBuilder.newCost;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
public class CostRestServiceMocksTest extends BaseRestServiceMocksTest<CostRestServiceImpl> {

    private static final String costRestURL = "/cost";

    @Override
    protected CostRestServiceImpl registerRestServiceUnderTest() {
        CostRestServiceImpl costService = new CostRestServiceImpl();
        costService.costRestURL = costRestURL;
        return costService;
    }

    @Test
    public void test_getCosts_forApplicationFinanceId() {

        String expectedUrl = dataServicesUrl + costRestURL + "/get/123";
        Cost[] returnedResponse = newCost().buildArray(3, Cost.class);
        ResponseEntity<Cost[]> returnedEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Cost[].class)).thenReturn(returnedEntity);

        List<Cost> costs = service.getCosts(123L);
        assertNotNull(costs);
        assertEquals(returnedResponse[0], costs.get(0));
        assertEquals(returnedResponse[1], costs.get(1));
        assertEquals(returnedResponse[2], costs.get(2));
    }

    @Test
    public void test_findById() {

        String expectedUrl = dataServicesUrl + costRestURL + "/findById/123";
        Cost returnedResponse = newCost().build();
        ResponseEntity<Cost> returnedEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Cost.class)).thenReturn(returnedEntity);

        Cost cost = service.findById(123L);
        assertNotNull(cost);
        assertEquals(returnedResponse, cost);
    }

    @Test
    public void test_add_byApplicationFinanceIdAndQuestionId() {

        String expectedUrl = dataServicesUrl + costRestURL + "/add/123/456";
        service.add(123L, 456L);
        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
    }

    @Test
    public void test_delete_byCostId() {
        String expectedUrl = dataServicesUrl + costRestURL + "/delete/123";
        service.delete(123L);
        verify(mockRestTemplate).exchange(expectedUrl, DELETE, httpEntityForRestCall(), Void.class);
    }

    @Test
    public void test_update_byCost() {

        Cost costToUpdate = newCost().with(id(123L)).build();

        String expectedUrl = dataServicesUrl + costRestURL + "/update/123";

        service.update(costToUpdate);
        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(costToUpdate), Void.class);
    }
}
