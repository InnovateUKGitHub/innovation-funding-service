package com.worth.ifs.finance.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.financeRowMetaFieldResourceListType;
import static com.worth.ifs.finance.builder.FinanceRowMetaFieldResourceBuilder.newFinanceRowMetaFieldResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class FinanceRowMetaFieldRestServiceMocksTest extends BaseRestServiceUnitTest<FinanceRowMetaFieldRestServiceImpl> {

    private static final String costFieldRestURL = "/costfield";

    @Override
    protected FinanceRowMetaFieldRestServiceImpl registerRestServiceUnderTest() {
        FinanceRowMetaFieldRestServiceImpl costFieldService = new FinanceRowMetaFieldRestServiceImpl();
        return costFieldService;
    }

    @Test
    public void test_getCostFields() {

        List<FinanceRowMetaFieldResource> returnedResponse = FinanceRowMetaFieldResourceBuilder.newFinanceRowMetaFieldResource().build(3);

        setupGetWithRestResultExpectations(costFieldRestURL + "/findAll/", ParameterizedTypeReferences.financeRowMetaFieldResourceListType(), returnedResponse);

        List<FinanceRowMetaFieldResource> costFields = service.getFinanceRowMetaFields().getSuccessObject();
        assertNotNull(costFields);
        assertEquals(returnedResponse, costFields);
    }
}
