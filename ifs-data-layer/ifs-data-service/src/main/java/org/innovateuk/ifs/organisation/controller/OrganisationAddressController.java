package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/organisationaddress")
public class OrganisationAddressController {

    @Autowired
    private OrganisationAddressService service;

    @GetMapping("/{id}")
    public RestResult<OrganisationAddressResource> findById(@PathVariable("id") final Long id) {
        return service.findOne(id).toGetResponse();
    }

    @GetMapping("/organisation/{organisationId}/address/{addressId}")
    public RestResult<OrganisationAddressResource> findByOrganisationIdAndAddressId(@PathVariable("organisationId") final long organisationId,
                                                                                    @PathVariable("addressId") final long addressId) {
        return service.findByOrganisationIdAndAddressId(organisationId, addressId).toGetResponse();
    }

    @GetMapping("/find-by-id/{organisationId}")
    public RestResult<List<OrganisationAddressResource>> findByOrganisationIdAndAddressType(@PathVariable("organisationId") final Long organisationId) {
        AddressType addressType = new AddressType();
        addressType.setId(OrganisationAddressType.REGISTERED.getId());
        addressType.setName(OrganisationAddressType.REGISTERED.name());
        return service.findByOrganisationIdAndAddressType(organisationId, addressType).toGetResponse();
    }
}
