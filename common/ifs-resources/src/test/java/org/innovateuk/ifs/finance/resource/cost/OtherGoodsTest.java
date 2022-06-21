package org.innovateuk.ifs.finance.resource.cost;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.OTHER_GOODS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OtherGoodsTest {

    private OtherGoods otherGoods;

    @Before
    public void setUp() {
        Long id = 1L;
        Integer deprecation = 12;
        String description = "";
        String existing = "New";
        BigDecimal npv = new BigDecimal(10000);
        BigDecimal residualValue = new BigDecimal(5000);
        Integer utilisation = 25;

        otherGoods = new OtherGoods(id, deprecation, description, existing, npv, residualValue, utilisation, 1L);
    }

    @Test
    public void getTotal() {
        assertEquals(BigDecimal.valueOf(1250).setScale(2, BigDecimal.ROUND_HALF_EVEN), otherGoods.getTotal());
    }

    @Test
    public void totalMustBeZeroWhenDataIsNotAvailableTest() {
        OtherGoods emptyOtherGoods = new OtherGoods(1L);
        assertEquals(BigDecimal.ZERO, emptyOtherGoods.getTotal());
    }

    @Test
    public void getTotalNullNPV() {
        otherGoods.setNpv(null);
        assertEquals(BigDecimal.ZERO, otherGoods.getTotal());
    }

    @Test
    public void getTotalNullResidualValue() {
        otherGoods.setResidualValue(null);
        assertEquals(BigDecimal.ZERO, otherGoods.getTotal());
    }

    @Test
    public void getTotalNullUtilizationValue() {
        otherGoods.setUtilisation(null);
        assertEquals(BigDecimal.ZERO, otherGoods.getTotal());
    }

    @Test
    public void getTotalZeroNPV() {
        otherGoods.setNpv(BigDecimal.ZERO);
        assertEquals(BigDecimal.valueOf(-1250).setScale(2, BigDecimal.ROUND_HALF_EVEN), otherGoods.getTotal());
    }

    @Test
    public void getTotalZeroResidualVal() {
        otherGoods.setResidualValue(BigDecimal.ZERO);
        assertEquals(BigDecimal.valueOf(2500).setScale(2, BigDecimal.ROUND_HALF_EVEN), otherGoods.getTotal());
    }

    @Test
    public void getTotalZeroUtilisation() {
        otherGoods.setUtilisation(0);
        assertEquals(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_EVEN), otherGoods.getTotal());
    }

    @Test
    public void getName() {
        assertEquals("other_goods", otherGoods.getName());
    }

    @Test
    public void isEmpty() {
        assertFalse(otherGoods.isEmpty());
    }

    @Test
    public void getCostType() {
        assertEquals(OTHER_GOODS, otherGoods.getCostType());
    }
}
