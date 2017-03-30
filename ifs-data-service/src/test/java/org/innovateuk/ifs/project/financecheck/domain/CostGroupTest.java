package org.innovateuk.ifs.project.financecheck.domain;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class CostGroupTest {

    @Test
    public void testCreateCostGroup() {

        Cost cost1 = new Cost("1.23");
        Cost cost2 = new Cost("4.56");
        CostGroup costGroup = new CostGroup("My cost group", asList(cost1, cost2));
        assertEquals("My cost group", costGroup.getDescription());
        assertEquals(asList(cost1, cost2), costGroup.getCosts());

        // check that the hibernate back-links have been set to link the Costs to their owning CostGroup
        assertEquals(costGroup, cost1.getCostGroup());
        assertEquals(costGroup, cost2.getCostGroup());
    }
}
