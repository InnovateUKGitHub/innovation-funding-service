package com.worth.ifs.user.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.transactional.OrganisationService;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.user.service.OrganisationRestServiceImpl} and other REST-API users
 * to manage {@link Organisation} related data.
 */
@RestController
@RequestMapping("/organisation")
public class OrganisationController {

    private static final Log LOG = LogFactory.getLog(OrganisationController.class);

    @Autowired
    private OrganisationService organisationService;

    @RequestMapping("/findByApplicationId/{applicationId}")
    public RestResult<Set<Organisation>> findByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        return organisationService.findByApplicationId(applicationId).toGetResponse();
    }

    @RequestMapping("/findById/{organisationId}")
    public RestResult<Organisation> findById(@PathVariable("organisationId") final Long organisationId) {
        return organisationService.findById(organisationId).toGetResponse();
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public RestResult<OrganisationResource> create(@RequestBody Organisation organisation) {
        return organisationService.create(organisation).toPostCreateResponse();
    }

    @RequestMapping(value = "/saveResource", method = RequestMethod.POST)
    public RestResult<OrganisationResource> saveResource(@RequestBody OrganisationResource organisationResource) {
        return organisationService.saveResource(organisationResource).toPostCreateResponse();
    }

    // TODO DW - INFUND-1555 - do we want to be returning an OrganisationResource from this call?
    @RequestMapping(value = "/addAddress/{organisationId}", method = RequestMethod.POST)
    public RestResult<OrganisationResource> addAddress(@PathVariable("organisationId") final Long organisationId, @RequestParam("addressType") final AddressType addressType, @RequestBody Address address) {
        return organisationService.addAddress(organisationId, addressType, address).toPutWithBodyResponse();
    }
}
