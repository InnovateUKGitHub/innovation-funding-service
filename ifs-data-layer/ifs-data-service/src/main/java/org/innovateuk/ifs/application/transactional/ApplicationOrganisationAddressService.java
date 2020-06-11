package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ApplicationOrganisationAddressService {

    @PreAuthorize("hasPermission(#organisationId, 'org.innovateuk.ifs.organisation.resource.OrganisationResource', 'READ')")
    ServiceResult<AddressResource> getAddress(long applicationId, long organisationId, OrganisationAddressType type);

    @PreAuthorize("hasPermission(#organisationId, 'org.innovateuk.ifs.organisation.resource.OrganisationResource', 'UPDATE')")
    ServiceResult<AddressResource> updateAddress(long applicationId, long organisationId, OrganisationAddressType type, AddressResource address);

}