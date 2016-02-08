package com.worth.ifs.organisation.controller;

import com.worth.ifs.organisation.mapper.OrganisationAddressMapper;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.organisation.transactional.OrganisationAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organisationaddress")
public class OrganisationAddressController {
    @Autowired
    private OrganisationAddressService service;

    @Autowired
    private OrganisationAddressMapper mapper;

    @RequestMapping("/{id}")
    public OrganisationAddressResource findById(@PathVariable("id") final Long id) {
        return mapper.mapOrganisationAddressToResource(service.findOne(id));
    }
}