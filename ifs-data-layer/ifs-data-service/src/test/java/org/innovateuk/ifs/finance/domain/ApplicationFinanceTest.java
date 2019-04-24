package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ApplicationFinanceTest {
    private ApplicationFinance applicationFinance;
    private Long id;
    private Organisation organisation;
    private Application application;

    @Before
    public void setUp() {
        id=0L;
        organisation = new Organisation("Worth Internet Systems");
        application = new Application();
    }

    @Test
    public void constructorsShouldCreateInstancesOnValidInput() {
        new ApplicationFinance();
        new ApplicationFinance(application, organisation);
    }

    @Test
    public void applicationFinanceShouldReturnCorrectAttributeValues() {
        Assert.assertEquals(applicationFinance.getId(), id);
        Assert.assertEquals(applicationFinance.getOrganisation(), organisation);
        Assert.assertEquals(applicationFinance.getApplication(), application);
    }

    @Test
    public void applicationFinanceShouldReturnCorrectAttributeValuesAfterSetId() {
        Long newId = 2L;
        applicationFinance.setId(newId);
        Assert.assertEquals(applicationFinance.getId(), newId);
    }
}