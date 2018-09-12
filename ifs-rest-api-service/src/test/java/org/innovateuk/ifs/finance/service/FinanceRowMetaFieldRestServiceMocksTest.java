package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.financeRowMetaFieldResourceListType;
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

        List<FinanceRowMetaFieldResource> returnedResponse = Arrays.asList(1,2,3).stream().map(i -> new FinanceRowMetaFieldResource()).collect(Collectors.toList());

        setupGetWithRestResultExpectations(costFieldRestURL + "/findAll/", financeRowMetaFieldResourceListType(), returnedResponse);

        List<FinanceRowMetaFieldResource> costFields = service.getFinanceRowMetaFields().getSuccess();
        assertNotNull(costFields);
        assertEquals(returnedResponse, costFields);
    }
}
