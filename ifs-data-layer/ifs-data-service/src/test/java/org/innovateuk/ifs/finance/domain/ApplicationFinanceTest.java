package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ApplicationFinanceTest {
    ApplicationFinance applicationFinance;

    Long id;
    Organisation organisation;
    Application application;

    @Before
    public void setUp() throws Exception {
        organisation = new Organisation( "Worth Internet Systems");
        application = new Application();
        applicationFinance = new ApplicationFinance(application, organisation);
    }

    @Test
    public void constructorsShouldCreateInstancesOnValidInput() throws Exception {
        new ApplicationFinance();
        new ApplicationFinance(application, organisation);
    }

    @Test
    public void applicationFinanceShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(applicationFinance.getId(), id);
        Assert.assertEquals(applicationFinance.getOrganisation(), organisation);
        Assert.assertEquals(applicationFinance.getApplication(), application);
    }

    @Test
    public void applicationFinanceShouldReturnCorrectAttributeValuesAfterSetId() throws Exception {
        Long newId = 2L;
        applicationFinance.setId(newId);
        Assert.assertEquals(applicationFinance.getId(), newId);
    }

}
