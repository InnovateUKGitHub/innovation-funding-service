package org.innovateuk.ifs.finance.resource;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.finance.resource.cost.Materials;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;

public class ApplicationFinanceResourceTest extends BaseUnitTestMocksTest {

    @Test
    public void mapApplicationFinanceToResourceTest() throws Exception {
        Application application = newApplication().build();
        Organisation organisation = newOrganisation().build();
        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withApplication(application.getId())
                .withOrganisation(organisation.getId()).build();

        Assert.assertEquals(applicationFinanceResource.getApplication(), (Long) 1L);
        Assert.assertEquals(applicationFinanceResource.getOrganisation(), (Long) 2L);
    }

    @Test
    public void calculatedTotalMustBeZeroWhenQuantityOrCostAreNotSetTest() throws Exception {
        Materials materialWithoutValues = new Materials();
        Assert.assertEquals(BigDecimal.ZERO, materialWithoutValues.getTotal());
    }
}
