package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.innovateuk.ifs.finance.service.OrganisationDetailsRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


/**
 * Test Class for all functionality in {@link OrganisationSizeServiceImpl}
 */
public class OrganisationSizeServiceImplTest extends BaseServiceUnitTest<OrganisationSizeServiceImpl> {

    @Mock
    private OrganisationDetailsRestService organisationDetailsRestService;

    @Override
    protected OrganisationSizeServiceImpl supplyServiceUnderTest() {
        return new OrganisationSizeServiceImpl();
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testGetOrganisationSizes() {
        List<OrganisationSizeResource> expected = new ArrayList<>();
        when(organisationDetailsRestService.getOrganisationSizes()).thenReturn(RestResult.restSuccess(expected));

        List<OrganisationSizeResource> actual = service.getOrganisationSizes();

        assertThat(actual, equalTo(expected));
    }

}
