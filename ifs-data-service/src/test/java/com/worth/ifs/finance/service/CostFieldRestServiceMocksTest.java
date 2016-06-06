package com.worth.ifs.finance.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.finance.resource.CostFieldResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.costFieldResourceListType;
import static com.worth.ifs.finance.builder.CostFieldResourceBuilder.newCostFieldResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class CostFieldRestServiceMocksTest extends BaseRestServiceUnitTest<CostFieldRestServiceImpl> {

    private static final String costFieldRestURL = "/costfield";

    @Override
    protected CostFieldRestServiceImpl registerRestServiceUnderTest() {
        CostFieldRestServiceImpl costFieldService = new CostFieldRestServiceImpl();
        return costFieldService;
    }

    @Test
    public void test_getCostFields() {

        List<CostFieldResource> returnedResponse = newCostFieldResource().build(3);

        setupGetWithRestResultExpectations(costFieldRestURL + "/findAll/", costFieldResourceListType(), returnedResponse);

        List<CostFieldResource> costFields = service.getCostFields().getSuccessObject();
        assertNotNull(costFields);
        assertEquals(returnedResponse, costFields);
    }
}
