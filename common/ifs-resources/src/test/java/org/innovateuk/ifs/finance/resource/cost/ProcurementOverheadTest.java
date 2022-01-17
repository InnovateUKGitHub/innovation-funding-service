package org.innovateuk.ifs.finance.resource.cost;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class ProcurementOverheadTest {
    private Long id;
    private Integer companyCost;
    private BigDecimal projectCost;
    private String item;
    private Long target_id;
    private ProcurementOverhead overhead;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        target_id = 1L;
        item = "procurement";
    }

    @Test
    public void totalForOverhead_NullProjectCost() {
        companyCost = 100;
        projectCost = null;
        overhead = new ProcurementOverhead(id, item, projectCost, companyCost, target_id);

        assertEquals(BigDecimal.ZERO, overhead.getTotal());
    }

    @Test
    public void totalForOverhead_NullCompanyCost() {
        companyCost = null;
        projectCost = BigDecimal.TEN;
        overhead = new ProcurementOverhead(id, item, projectCost, companyCost, target_id);

        assertEquals(BigDecimal.ZERO, overhead.getTotal());
    }

    @Test
    public void totalForOverhead() {
        companyCost = 500;
        projectCost = BigDecimal.TEN;
        overhead = new ProcurementOverhead(id, item, projectCost, companyCost, target_id);

        assertEquals(BigDecimal.valueOf(50), overhead.getTotal());
    }
}
