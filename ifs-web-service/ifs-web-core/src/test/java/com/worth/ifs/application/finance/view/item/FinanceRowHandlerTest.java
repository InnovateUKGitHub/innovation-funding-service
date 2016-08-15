package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.view.jes.AcademicFinanceHandler;
import com.worth.ifs.util.NumberUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;

public class FinanceRowHandlerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void getBigDecimalValue() throws Exception {
        AcademicFinanceHandler costHandler = new AcademicFinanceHandler();
        BigDecimal expecting = new BigDecimal(1000500.95).setScale(2, BigDecimal.ROUND_HALF_UP);
        assertTrue(expecting.compareTo(NumberUtils.getBigDecimalValue("1,000,500.95", new Double(0.0))) == 0);

        assertTrue(expecting.compareTo(NumberUtils.getBigDecimalValue("1000500.95", new Double(0.0))) == 0);
        assertTrue(expecting.compareTo(NumberUtils.getBigDecimalValue("£ 1000500.95", new Double(0.0))) == 0);
        assertTrue(expecting.compareTo(NumberUtils.getBigDecimalValue("£1000500.95", new Double(0.0))) == 0);
        assertTrue(expecting.compareTo(NumberUtils.getBigDecimalValue("£100a0500.95", new Double(0.0))) == 0);

        expecting = new BigDecimal(-1000500.95).setScale(2, BigDecimal.ROUND_HALF_UP);
        assertTrue(expecting.compareTo(NumberUtils.getBigDecimalValue("-1,000,500.95", new Double(0.0))) == 0);

        expecting = new BigDecimal(1000500).setScale(2, BigDecimal.ROUND_HALF_UP);
        assertTrue(expecting.compareTo(NumberUtils.getBigDecimalValue("1000500", new Double(0.0))) == 0);

        expecting = new BigDecimal(-1000500).setScale(2, BigDecimal.ROUND_HALF_UP);
        assertTrue(expecting.compareTo(NumberUtils.getBigDecimalValue("-1000500", new Double(0.0))) == 0);
    }

    @Test
    public void getIntegerValue() throws Exception {
        AcademicFinanceHandler costHandler = new AcademicFinanceHandler();
        Integer expecting = new Integer("500000");
        assertTrue(expecting.compareTo(NumberUtils.getIntegerValue("500,000", new Integer("0"))) == 0);
        assertTrue(expecting.compareTo(NumberUtils.getIntegerValue("500,000.000", new Integer("0"))) == 0);
        assertTrue(expecting.compareTo(NumberUtils.getIntegerValue("£500,000.500", new Integer("0"))) == 0);
        assertTrue(expecting.compareTo(NumberUtils.getIntegerValue("£500,0 00.500", new Integer("0"))) == 0);
        assertTrue(expecting.compareTo(NumberUtils.getIntegerValue("£500,0asdf00.50??&0", new Integer("0"))) == 0);
        assertTrue(expecting.compareTo(NumberUtils.getIntegerValue("£ 500,000.500", new Integer("0"))) == 0);

        expecting = new Integer("-500000");
        assertTrue(expecting.compareTo(NumberUtils.getIntegerValue("-500,000", new Integer("0"))) == 0);

    }

}