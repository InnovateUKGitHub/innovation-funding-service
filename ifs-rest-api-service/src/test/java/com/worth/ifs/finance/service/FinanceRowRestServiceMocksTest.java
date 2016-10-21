package com.worth.ifs.finance.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.builder.LabourCostBuilder;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.LabourCost;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.costItemListType;
import static org.junit.Assert.*;

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

    @Test
    public void test_getCosts_forApplicationFinanceId() {

        List<FinanceRowItem> returnedResponse = new ArrayList<>();

        setupGetWithRestResultExpectations(costRestURL + "/get/123", ParameterizedTypeReferences.costItemListType(), returnedResponse);
        List<FinanceRowItem> costs = service.getCosts(123L).getSuccessObject();
        assertNotNull(costs);
        assertEquals(returnedResponse, costs);
    }

    @Test
    public void test_findById() {
        String expectedUrl = BaseRestServiceUnitTest.dataServicesUrl + costRestURL + "/123";
        FinanceRowItem returnedResponse = new LabourCost();

        setupGetWithRestResultExpectations(costRestURL + "/123", FinanceRowItem.class, returnedResponse);

        FinanceRowItem cost = service.findById(123L).getSuccessObject();
        assertNotNull(cost);
        Assert.assertEquals(returnedResponse, cost);
    }

    @Test
    public void test_add_byApplicationFinanceIdAndQuestionId() {
        LabourCost costToUpdate = LabourCostBuilder.newLabourCost().build();
        String expectedUrl = costRestURL + "/add/123/456";
        setupPostWithRestResultExpectations(expectedUrl, ValidationMessages.class, costToUpdate, new ValidationMessages(), HttpStatus.OK);
        service.add(123L, 456L, costToUpdate).getSuccessObject();
    }

    @Test
    public void test_delete_byCostId() {
        setupDeleteWithRestResultExpectations(costRestURL + "/delete/123");
        service.delete(123L);
        setupDeleteWithRestResultVerifications(costRestURL + "/delete/123");
    }

    @Test
    public void test_update_byCost() {
        LabourCost costToUpdate = LabourCostBuilder.newLabourCost().build();
        String expectedUrl = costRestURL + "/update/" + costToUpdate.getId();
        setupPutWithRestResultExpectations(expectedUrl, ValidationMessages.class, costToUpdate, new ValidationMessages());
        service.update(costToUpdate).getSuccessObject();
    }
}
