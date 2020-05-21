package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.service.ServiceResult;

public interface ApplicationOrganisationAddressService {

    ServiceResult<AddressResource> getAddress(long applicationId, long organisationId, OrganisationAddressType type);

    ServiceResult<AddressResource> updateAddress(long applicationId, long organisationId, OrganisationAddressType type, AddressResource address);

}