package com.worth.ifs.finance.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.finance.domain.Cost;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;


public class CostControllerIntegrationTest extends BaseControllerIntegrationTest<CostController> {

    @Override
    @Autowired
    protected void setControllerUnderTest(CostController controller) {
        this.controller = controller;
    }

    @Rollback
    @Test
    public void testUpdateGrantClaimPercentage(){

        Cost grantClaim = controller.findById(48L);

        assertEquals("Grant Claim", grantClaim.getDescription());

        grantClaim.setCost(BigDecimal.valueOf(43));
        controller.update(48L, grantClaim);

        assertEquals(BigDecimal.valueOf(43), controller.findById(48L).getCost());

    }

}
