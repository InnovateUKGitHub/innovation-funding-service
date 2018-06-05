package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrganisationAddressRestServiceImplTest extends BaseRestServiceUnitTest<OrganisationAddressRestServiceImpl> {

    private static final String restUrl = "/organisationaddress";

    @Test
    public void findOne() {

        long id = 1L;

        OrganisationAddressResource organisationAddressResource = OrganisationAddressResourceBuilder.newOrganisationAddressResource().build();

        setupGetWithRestResultExpectations(restUrl + "/" + id, OrganisationAddressResource.class, organisationAddressResource);

        RestResult<OrganisationAddressResource> result = service.findOne(id);
        assertTrue(result.isSuccess());
        assertEquals(organisationAddressResource, result.getSuccess());

        setupGetWithRestResultVerifications(restUrl + "/" + id, null, OrganisationAddressResource.class);
    }

    @Test
    public void findByOrganisationIdAndAddressId() {

        long organisationId = 1L;
        long addressId = 2L;

        OrganisationAddressResource organisationAddressResource = OrganisationAddressResourceBuilder.newOrganisationAddressResource().build();

        setupGetWithRestResultExpectations(restUrl + "/organisation/" + organisationId + "/address/" + addressId, OrganisationAddressResource.class, organisationAddressResource);

        RestResult<OrganisationAddressResource> result = service.findByOrganisationIdAndAddressId(organisationId, addressId);
        assertTrue(result.isSuccess());
        assertEquals(organisationAddressResource, result.getSuccess());

        setupGetWithRestResultVerifications(restUrl + "/organisation/" + organisationId + "/address/" + addressId, null, OrganisationAddressResource.class);
    }

    @Override
    protected OrganisationAddressRestServiceImpl registerRestServiceUnderTest() {
        OrganisationAddressRestServiceImpl organisationAddressRestService = new OrganisationAddressRestServiceImpl();
        ReflectionTestUtils.setField(organisationAddressRestService, "restUrl", restUrl);
        return organisationAddressRestService;
    }
}
