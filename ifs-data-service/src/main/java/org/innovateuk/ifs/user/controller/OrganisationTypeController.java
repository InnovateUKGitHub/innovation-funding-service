package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.transactional.OrganisationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/organisationtype")
public class OrganisationTypeController {

    @Autowired
    private OrganisationTypeService service;

    @Autowired
    private OrganisationService organisationService;


    @GetMapping("/{id}")
    public RestResult<OrganisationTypeResource> findById(@PathVariable("id") final Long id) {
        return service.findOne(id).toGetResponse();
    }

    @GetMapping("/getAll")
    public RestResult<List<OrganisationTypeResource>> findAll() {
        return service.findAll().toGetResponse();
    }


    @GetMapping("/getTypeForOrganisation/{organisationId}")
    public RestResult<OrganisationTypeResource> findTypeForOrganisation(@PathVariable Long organisationId) {
        return organisationService.findById(organisationId).
                andOnSuccess(organisation -> service.findOne(organisation.getOrganisationType())).
                toGetResponse();
    }
}
