package org.innovateuk.ifs.finance.service;

import static org.junit.Assert.assertEquals;


import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.junit.Test;
import org.springframework.http.HttpStatus;

public class ProjectFinanceRowRestServiceMocksTest extends BaseRestServiceUnitTest<ProjectFinanceRowRestServiceImpl> {
    private static final String costRestURL = "/project-finance-row";


    @Test
    public void create() {
        LabourCost costToUpdate = new LabourCost(1L);
        setupPostWithRestResultExpectations(costRestURL, FinanceRowItem.class, costToUpdate, costToUpdate, HttpStatus.OK);
        assertEquals(costToUpdate, service.create(costToUpdate).getSuccess());
    }

    @Test
    public void delete() {
        setupDeleteWithRestResultExpectations(costRestURL + "/456");
        service.delete(456L);
    }

    @Test
    public void update() {
        LabourCost costToUpdate = new LabourCost(1L);
        String expectedUrl = costRestURL + "/" + costToUpdate.getId();
        setupPutWithRestResultExpectations(expectedUrl, ValidationMessages.class, costToUpdate, new ValidationMessages());
        service.update(costToUpdate).getSuccess();
    }

    @Override
    protected ProjectFinanceRowRestServiceImpl registerRestServiceUnderTest() {
        return new ProjectFinanceRowRestServiceImpl();
    }
}
