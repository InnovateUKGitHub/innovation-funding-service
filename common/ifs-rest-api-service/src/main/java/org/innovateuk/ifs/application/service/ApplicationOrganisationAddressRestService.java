package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.rest.RestResult;

public interface ApplicationOrganisationAddressRestService {

    RestResult<AddressResource> getAddress(long applicationId, long organisationId, OrganisationAddressType type);

    RestResult<Void> updateAddress(long applicationId, long organisationId, OrganisationAddressType type, AddressResource address);
}
