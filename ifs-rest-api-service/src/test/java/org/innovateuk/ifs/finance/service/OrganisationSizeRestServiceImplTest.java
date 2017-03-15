package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.organisationSizeListType;
import static org.junit.Assert.*;

/**
 * Test for {@link OrganisationSizeRestServiceImpl}
 */
public class OrganisationSizeRestServiceImplTest extends BaseRestServiceUnitTest<OrganisationSizeRestServiceImpl> {

    private static final String organisationSizeUrl = "/organisation-size";

    @Override
    protected OrganisationSizeRestServiceImpl registerRestServiceUnderTest() {
        return new OrganisationSizeRestServiceImpl();
    }

    @Test
    public void test_getOrganisationSizes() {

        List<OrganisationSizeResource> returnedResponse = new ArrayList<>();

        setupGetWithRestResultExpectations(organisationSizeUrl, organisationSizeListType(), returnedResponse);
        List<OrganisationSizeResource> actual = service.getOrganisationSizes().getSuccessObject();
        assertNotNull(actual);
        assertEquals(returnedResponse, actual);
    }

}
