package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ApplicationFinanceTest {
    ApplicationFinance applicationFinance;

    Long id;
    Integer workingDaysPerYear;
    String overheadAcceptRate;
    Integer overheadRate;
    String otherFunding;

    @Before
    public void setUp() throws Exception {
        id=0L;
        workingDaysPerYear=200;
        overheadAcceptRate="Yes";
        overheadRate=10;
        otherFunding="Yes";

        applicationFinance = new ApplicationFinance(id, workingDaysPerYear, overheadAcceptRate, overheadRate, otherFunding);
    }

    @Test
    public void applicationFinanceShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(applicationFinance.getId(), id);
        Assert.assertEquals(applicationFinance.getWorkingDaysPerYear(), workingDaysPerYear);
        Assert.assertEquals(applicationFinance.getOverheadAcceptRate(), overheadAcceptRate);
        Assert.assertEquals(applicationFinance.getOverheadRate(), overheadRate);
        Assert.assertEquals(applicationFinance.getOtherFunding(), otherFunding);
    }
}