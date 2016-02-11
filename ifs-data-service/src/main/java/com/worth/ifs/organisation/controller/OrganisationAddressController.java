package com.worth.ifs.organisation.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.organisation.transactional.OrganisationAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;

@RestController
@RequestMapping("/organisationaddress")
public class OrganisationAddressController {

    @Autowired
    private OrganisationAddressService service;

    @RequestMapping("/{id}")
    public RestResult<OrganisationAddressResource> findById(@PathVariable("id") final Long id) {
        return newRestHandler().perform(() -> service.findOne(id));
    }
}