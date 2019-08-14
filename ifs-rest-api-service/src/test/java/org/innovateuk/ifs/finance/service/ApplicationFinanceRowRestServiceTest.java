package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class ApplicationFinanceRowRestServiceTest extends BaseRestServiceUnitTest<ApplicationFinanceRowRestServiceImpl> {

    private static final String costRestURL = "/application-finance-row";

    @Override
    protected ApplicationFinanceRowRestServiceImpl registerRestServiceUnderTest() {
        ApplicationFinanceRowRestServiceImpl costService = new ApplicationFinanceRowRestServiceImpl();
        return costService;
    }

    @Test
    public void get() {
        LabourCost returnedResponse = new LabourCost(1L);

        setupGetWithRestResultExpectations(costRestURL + "/123", FinanceRowItem.class, returnedResponse);

        FinanceRowItem cost = service.get(123L).getSuccess();
        assertNotNull(cost);
        Assert.assertEquals(returnedResponse, cost);
    }

    @Test
    public void create() {
        LabourCost costToUpdate = new LabourCost(1L);
        setupPostWithRestResultExpectations(costRestURL, FinanceRowItem.class, costToUpdate, costToUpdate, HttpStatus.OK);
        assertEquals(costToUpdate, service.create(costToUpdate).getSuccess());
    }

    @Test
    public void delete() {
        setupDeleteWithRestResultExpectations(costRestURL + "/123");
        service.delete(123L);
        setupDeleteWithRestResultVerifications(costRestURL + "/123");
    }

    @Test
    public void update() {
        LabourCost costToUpdate = new LabourCost(1L);
        String expectedUrl = costRestURL + "/" + costToUpdate.getId();
        setupPutWithRestResultExpectations(expectedUrl, ValidationMessages.class, costToUpdate, new ValidationMessages());
        service.update(costToUpdate).getSuccess();
    }
}
