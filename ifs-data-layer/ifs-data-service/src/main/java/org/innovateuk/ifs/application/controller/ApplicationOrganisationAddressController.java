package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.transactional.ApplicationOrganisationAddressService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application/{applicationId}/organisation/{organisationId}/address/{addressType}")
public class ApplicationOrganisationAddressController {

    @Autowired
    private ApplicationOrganisationAddressService applicationOrganisationAddressService;

    @GetMapping
    public RestResult<AddressResource> get(@PathVariable long applicationId,
                                           @PathVariable long organisationId,
                                           @PathVariable OrganisationAddressType addressType) {
        return applicationOrganisationAddressService.getAddress(applicationId, organisationId, addressType).toGetResponse();
    }

    @PutMapping
    public RestResult<Void> update(@PathVariable long applicationId,
                                              @PathVariable long organisationId,
                                              @PathVariable OrganisationAddressType addressType,
                                              @RequestBody AddressResource address) {
        return applicationOrganisationAddressService.updateAddress(applicationId, organisationId, addressType, address).toPutResponse();
    }

}
