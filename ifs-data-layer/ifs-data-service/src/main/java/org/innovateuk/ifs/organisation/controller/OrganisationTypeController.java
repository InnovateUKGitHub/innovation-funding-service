package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.organisation.transactional.OrganisationTypeService;
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

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getAll", "/get-all"})
    public RestResult<List<OrganisationTypeResource>> findAll() {
        return service.findAll().toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getTypeForOrganisation/{organisationId}", "/get-type-for-organisation/{organisationId}"})
    public RestResult<OrganisationTypeResource> findTypeForOrganisation(@PathVariable Long organisationId) {
        return organisationService.findById(organisationId).
                andOnSuccess(organisation -> service.findOne(organisation.getOrganisationType())).
                toGetResponse();
    }
}
