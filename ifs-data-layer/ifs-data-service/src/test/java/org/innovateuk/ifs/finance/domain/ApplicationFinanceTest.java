package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApplicationFinanceTest {
    private ApplicationFinance applicationFinance;
    private Organisation organisation;
    private Application application;
    private String workPostcode;

    @Before
    public void setUp() {
        organisation = new Organisation("Worth Internet Systems");
        application = new Application();
        workPostcode = new ApplicationFinance().getWorkPostcode();
        applicationFinance = new ApplicationFinance(application, organisation, workPostcode);
    }

    @Test
    public void constructorsShouldCreateInstancesOnValidInput() {
        new ApplicationFinance();
        new ApplicationFinance(application, organisation, workPostcode);
    }

    @Test
    public void applicationFinanceShouldReturnCorrectAttributeValues() {
        assertEquals(applicationFinance.getOrganisation(), organisation);
        assertEquals(applicationFinance.getApplication(), application);
    }

    @Test
    public void applicationFinanceShouldReturnCorrectAttributeValuesAfterSetId() {
        Long newId = 2L;
        applicationFinance.setId(newId);
        assertEquals(applicationFinance.getId(), newId);
    }
}