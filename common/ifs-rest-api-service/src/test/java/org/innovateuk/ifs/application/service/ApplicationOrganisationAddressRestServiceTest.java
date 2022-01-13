package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

public class ApplicationOrganisationAddressRestServiceTest extends BaseRestServiceUnitTest<ApplicationOrganisationAddressRestServiceImpl> {
    private static final String BASE_URL = "/application/%d/organisation/%d/address/%s";

    @Override
    protected ApplicationOrganisationAddressRestServiceImpl registerRestServiceUnderTest() {
        return new ApplicationOrganisationAddressRestServiceImpl();
    }

    @Test
    public void getAddress() {
        long applicationId = 1L;
        long organisationId = 2L;
        OrganisationAddressType type = OrganisationAddressType.INTERNATIONAL;

        String expectedUrl = format(BASE_URL, applicationId, organisationId, type.name());
        AddressResource address = new AddressResource();

        setupGetWithRestResultExpectations(expectedUrl, AddressResource.class, address);

        RestResult<AddressResource> result = service.getAddress(applicationId, organisationId, type);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(address, result.getSuccess());
    }

    @Test
    public void updateAddress() {
        long applicationId = 1L;
        long organisationId = 2L;
        OrganisationAddressType type = OrganisationAddressType.INTERNATIONAL;
        AddressResource address = new AddressResource();

        String expectedUrl = format(BASE_URL, applicationId, organisationId, type.name());
        setupPutWithRestResultExpectations(expectedUrl, address, HttpStatus.OK);

        RestResult<Void> result = service.updateAddress(applicationId, organisationId, type, address);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
