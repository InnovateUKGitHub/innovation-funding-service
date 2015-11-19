package com.worth.ifs.finance.service;

import com.worth.ifs.BaseRestServiceMocksTest;
import com.worth.ifs.finance.domain.CostField;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.worth.ifs.finance.builder.CostFieldBuilder.newCostField;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
public class CostFieldRestServiceMocksTest extends BaseRestServiceMocksTest<CostFieldRestServiceImpl> {

    private static final String costFieldRestURL = "/costfield";

    @Override
    protected CostFieldRestServiceImpl registerRestServiceUnderTest() {
        CostFieldRestServiceImpl costFieldService = new CostFieldRestServiceImpl();
        costFieldService.costFieldRestURL = costFieldRestURL;
        return costFieldService;
    }

    @Test
    public void test_getCostFields() {

        String expectedUrl = dataServicesUrl + costFieldRestURL + "/findAll/";
        CostField[] returnedResponse = newCostField().buildArray(3, CostField.class);
        ResponseEntity<CostField[]> returnedEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), CostField[].class)).thenReturn(returnedEntity);

        List<CostField> costFields = service.getCostFields();
        assertNotNull(costFields);
        assertEquals(returnedResponse[0], costFields.get(0));
        assertEquals(returnedResponse[1], costFields.get(1));
        assertEquals(returnedResponse[2], costFields.get(2));
    }
}
