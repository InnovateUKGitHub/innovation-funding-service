package com.worth.ifs.user.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.resource.OrganisationTypeResource;
import com.worth.ifs.user.transactional.OrganisationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/organisationtype")
public class OrganisationTypeController {

    @Autowired
    private OrganisationTypeService service;

    @RequestMapping("/{id}")
    public RestResult<OrganisationTypeResource> findById(@PathVariable("id") final Long id) {
        return service.findOne(id).toDefaultRestResultForGet();
    }

    @RequestMapping("/getAll")
    public RestResult<List<OrganisationTypeResource>> findAll() {
        return service.findAll().toDefaultRestResultForGet();
    }
}