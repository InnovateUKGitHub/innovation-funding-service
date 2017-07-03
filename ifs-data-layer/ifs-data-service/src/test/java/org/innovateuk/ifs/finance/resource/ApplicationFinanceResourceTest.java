package org.innovateuk.ifs.finance.resource;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.finance.resource.cost.Materials;
import org.innovateuk.ifs.user.domain.Organisation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.clearUniqueIds;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;

public class ApplicationFinanceResourceTest extends BaseUnitTestMocksTest {

    Long id;
    private Long organisation;
    private Long application;
    private Long fileEntry;
    private Long organisationSize;
    private ApplicationFinanceResource applicationFinanceResource;

    @Before
    public void setUp() throws Exception {
        clearUniqueIds();
        id = 0L;
        organisation = 1L;
        application = 1L;
        fileEntry = 1L;
        organisationSize = 1L;
        applicationFinanceResource = new ApplicationFinanceResource(id, organisation, application, organisationSize);
        applicationFinanceResource.setFinanceFileEntry(fileEntry);
    }

    @Test
    public void applicationFinanceResourceShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(applicationFinanceResource.getId().equals(id));
        assert(applicationFinanceResource.getOrganisation().equals(organisation));
        assert(applicationFinanceResource.getApplication().equals(application));
        assert(applicationFinanceResource.getOrganisationSize().equals(organisationSize));
        assert(applicationFinanceResource.getFinanceFileEntry().equals(fileEntry));
    }

    @Test
    public void setApplicationFinanceAttributes() throws Exception {
        ApplicationFinanceResource applicationFinanceResourceEmpty = new ApplicationFinanceResource();
        applicationFinanceResourceEmpty.setId(id);
        applicationFinanceResourceEmpty.setApplication(application);
        applicationFinanceResourceEmpty.setOrganisation(organisation);
        applicationFinanceResourceEmpty.setOrganisationSize(organisationSize);
        applicationFinanceResourceEmpty.setFinanceFileEntry(fileEntry);

        assert(applicationFinanceResource.getId().equals(id));
        assert(applicationFinanceResource.getOrganisation().equals(organisation));
        assert(applicationFinanceResource.getApplication().equals(application));
        assert(applicationFinanceResource.getOrganisationSize().equals(organisationSize));
        assert(applicationFinanceResource.getFinanceFileEntry().equals(fileEntry));
    }

    @Test
    public void mapApplicationFinanceToResourceTest() throws Exception {
        Application application = newApplication().build();
        Organisation organisation = newOrganisation().build();
        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withApplication(application.getId())
                .withOrganisation(organisation.getId()).build();

        Assert.assertEquals(applicationFinanceResource.getApplication(), new Long(1L));
        Assert.assertEquals(applicationFinanceResource.getOrganisation(), new Long(2L));
    }

    @Test
    public void calculatedTotalMustBeZeroWhenQuantityOrCostAreNotSetTest() throws Exception {
        Materials materialWithoutValues = new Materials();
        Assert.assertEquals(BigDecimal.ZERO, materialWithoutValues.getTotal());
    }
}
