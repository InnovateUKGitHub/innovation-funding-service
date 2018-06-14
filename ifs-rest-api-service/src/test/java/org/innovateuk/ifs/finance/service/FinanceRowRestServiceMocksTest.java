package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.costItemListType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class FinanceRowRestServiceMocksTest extends BaseRestServiceUnitTest<DefaultFinanceRowRestServiceImpl> {

    private static final String costRestURL = "/cost";

    @Override
    protected DefaultFinanceRowRestServiceImpl registerRestServiceUnderTest() {
        DefaultFinanceRowRestServiceImpl costService = new DefaultFinanceRowRestServiceImpl();
        return costService;
    }

    @Test
    public void test_getCosts_forApplicationFinanceId() {

        List<FinanceRowItem> returnedResponse = new ArrayList<>();

        setupGetWithRestResultExpectations(costRestURL + "/get/123", costItemListType(), returnedResponse);
        List<FinanceRowItem> costs = service.getCosts(123L).getSuccess();
        assertNotNull(costs);
        assertEquals(returnedResponse, costs);
    }

    @Test
    public void test_findById() {
        String expectedUrl = BaseRestServiceUnitTest.dataServicesUrl + costRestURL + "/123";
        FinanceRowItem returnedResponse = new LabourCost();

        setupGetWithRestResultExpectations(costRestURL + "/123", FinanceRowItem.class, returnedResponse);

        FinanceRowItem cost = service.findById(123L).getSuccess();
        assertNotNull(cost);
        Assert.assertEquals(returnedResponse, cost);
    }

    @Test
    public void test_add_byApplicationFinanceIdAndQuestionId() {
        LabourCost costToUpdate = new LabourCost();
        String expectedUrl = costRestURL + "/add/123/456";
        setupPostWithRestResultExpectations(expectedUrl, ValidationMessages.class, costToUpdate, new ValidationMessages(), HttpStatus.OK);
        service.add(123L, 456L, costToUpdate).getSuccess();
    }

    @Test
    public void test_delete_byCostId() {
        setupDeleteWithRestResultExpectations(costRestURL + "/delete/123");
        service.delete(123L);
        setupDeleteWithRestResultVerifications(costRestURL + "/delete/123");
    }

    @Test
    public void test_update_byCost() {
        LabourCost costToUpdate = new LabourCost();
        String expectedUrl = costRestURL + "/update/" + costToUpdate.getId();
        setupPutWithRestResultExpectations(expectedUrl, ValidationMessages.class, costToUpdate, new ValidationMessages());
        service.update(costToUpdate).getSuccess();
    }

    @Test
    public void testAddApplicationCostWithoutPersisting() {
        LabourCost costToUpdate = new LabourCost();
        String expectedUrl = costRestURL + "/add-without-persisting/" + 123L + "/" + 456L;
        setupPostWithRestResultExpectations(expectedUrl, FinanceRowItem.class, null, costToUpdate, HttpStatus.OK);
        RestResult<FinanceRowItem> financeRowItem = service.addWithoutPersisting(123L, 456L);
        assertTrue(financeRowItem.isSuccess());
        assertEquals(costToUpdate, financeRowItem.getSuccess());
    }
}
