package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * REST service for getting and saving application organisation addresses.
 */
@Service
public class ApplicationOrganisationAddressRestServiceImpl extends BaseRestService implements ApplicationOrganisationAddressRestService {

    private static final String BASE_URL = "/application/%d/organisation/%d/address/%s";

    @Override
    public RestResult<AddressResource> getAddress(long applicationId, long organisationId, OrganisationAddressType type) {
        return getWithRestResult(format(BASE_URL, applicationId, organisationId, type.name()), AddressResource.class);
    }

    @Override
    public RestResult<Void> updateAddress(long applicationId, long organisationId, OrganisationAddressType type, AddressResource address) {
        return putWithRestResult(format(BASE_URL, applicationId, organisationId, type.name()), address, Void.class);
    }
}