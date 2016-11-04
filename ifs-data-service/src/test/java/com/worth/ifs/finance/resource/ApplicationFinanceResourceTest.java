package com.worth.ifs.finance.resource;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.finance.resource.cost.Materials;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationSize;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.clearUniqueIds;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.junit.Assert.assertEquals;

public class ApplicationFinanceResourceTest extends BaseUnitTestMocksTest {

    Long id;
    private Long organisation;
    private Long application;
    private Long fileEntry;
    private OrganisationSize organisationSize;
    private ApplicationFinanceResource applicationFinanceResource;

    @Before
    public void setUp() throws Exception {
        clearUniqueIds();
        id = 0L;
        organisation = 1L;
        application = 1L;
        fileEntry = 1L;
        organisationSize = OrganisationSize.MEDIUM;
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

        assertEquals(applicationFinanceResource.getApplication(), new Long(1L));
        assertEquals(applicationFinanceResource.getOrganisation(), new Long(2L));
    }

    @Test
    public void calculatedTotalMustBeZeroWhenQuantityOrCostAreNotSetTest() throws Exception {
        Materials materialWithoutValues = new Materials();
        assertEquals(BigDecimal.ZERO, materialWithoutValues.getTotal());
    }
}
