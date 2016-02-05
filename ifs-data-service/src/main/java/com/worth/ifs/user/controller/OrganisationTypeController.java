package com.worth.ifs.user.controller;

import com.worth.ifs.user.mapper.OrganisationTypeMapper;
import com.worth.ifs.user.resource.OrganisationTypeResource;
import com.worth.ifs.user.transactional.OrganisationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organisationtype")
public class OrganisationTypeController {
    @Autowired
    private OrganisationTypeService service;

    @Autowired
    private OrganisationTypeMapper mapper;

    @RequestMapping("/{id}")
    public OrganisationTypeResource findById(@PathVariable("id") final Long id) {
        return mapper.mapToResource(service.findOne(id));
    }
}