package com.worth.ifs.finance.resource;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.Materials;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationSize;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.junit.Assert.assertEquals;

public class ApplicationFinanceResourceTest {
    Long id;
    private Long organisation;
    private Long application;
    private OrganisationSize organisationSize;
    private List<CostItem> costItems;
    private ApplicationFinanceResource applicationFinanceResource;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        organisation = 1L;
        application = 1L;
        organisationSize = OrganisationSize.MEDIUM;
        costItems = new ArrayList<>();
        applicationFinanceResource = new ApplicationFinanceResource(id, organisation, application, organisationSize);
    }

    @Test
    public void applicationFinanceResourceShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(applicationFinanceResource.getId().equals(id));
        assert(applicationFinanceResource.getOrganisation().equals(organisation));
        assert(applicationFinanceResource.getApplication().equals(application));
        assert(applicationFinanceResource.getOrganisationSize().equals(organisationSize));
    }

    @Test
    public void setApplicationFinanceAttributes() throws Exception {
        ApplicationFinanceResource applicationFinanceResourceEmpty = new ApplicationFinanceResource();
        applicationFinanceResourceEmpty.setId(id);
        applicationFinanceResourceEmpty.setApplication(application);
        applicationFinanceResourceEmpty.setOrganisation(organisation);
        applicationFinanceResourceEmpty.setOrganisationSize(organisationSize);

        assert(applicationFinanceResource.getId().equals(id));
        assert(applicationFinanceResource.getOrganisation().equals(organisation));
        assert(applicationFinanceResource.getApplication().equals(application));
        assert(applicationFinanceResource.getOrganisationSize().equals(organisationSize));
    }

    @Test
    public void mapApplicationFinanceToResourceTest() throws Exception {
        Application application = newApplication().build();
        Organisation organisation = newOrganisation().build();
        ApplicationFinance applicationFinance = newApplicationFinance()
                .withApplication(application)
                .withOrganisation(organisation).build();

        ApplicationFinanceResource applicationFinanceResource1 = new ApplicationFinanceResource(applicationFinance);
        assertEquals(applicationFinanceResource1.getApplication(), new Long(1L));
        assertEquals(applicationFinanceResource1.getOrganisation(), new Long(1L));
    }

    @Test
    public void calculatedTotalMustBeZeroWhenQuantityOrCostAreNotSetTest() throws Exception {
        Materials materialWithoutValues = new Materials();
        assertEquals(BigDecimal.ZERO, materialWithoutValues.getTotal());
    }
}
