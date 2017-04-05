package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organisationaddress")
public class OrganisationAddressController {

    @Autowired
    private OrganisationAddressService service;

    @GetMapping("/{id}")
    public RestResult<OrganisationAddressResource> findById(@PathVariable("id") final Long id) {
        return service.findOne(id).toGetResponse();
    }
}
