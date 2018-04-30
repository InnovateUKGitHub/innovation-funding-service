package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.springframework.stereotype.Service;

@Service
public class OrganisationAddressRestServiceImpl extends BaseRestService implements OrganisationAddressRestService {

    private String restUrl = "/organisationaddress";

    @Override
    public RestResult<OrganisationAddressResource> findOne(Long id) {
        return getWithRestResult(restUrl + "/" + id, OrganisationAddressResource.class);
    }

    @Override
    public RestResult<OrganisationAddressResource> findByOrganisationIdAndAddressId(Long organisationId, Long addressId) {
        return getWithRestResult(restUrl + "/organisation/" + organisationId + "/address/" + addressId, OrganisationAddressResource.class);
    }
}
