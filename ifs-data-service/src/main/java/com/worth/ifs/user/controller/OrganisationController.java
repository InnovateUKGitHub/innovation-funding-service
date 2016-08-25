package com.worth.ifs.user.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.organisation.transactional.OrganisationService;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;
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

    @Autowired
    private OrganisationService organisationService;

    @RequestMapping("/findByApplicationId/{applicationId}")
    public RestResult<Set<OrganisationResource>> findByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        return organisationService.findByApplicationId(applicationId).toGetResponse();
    }

    @RequestMapping("/findById/{organisationId}")
    public RestResult<OrganisationResource> findById(@PathVariable("organisationId") final Long organisationId) {
        return organisationService.findById(organisationId).toGetResponse();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public RestResult<OrganisationResource> create(@RequestBody OrganisationResource organisation) {
        return organisationService.create(organisation).toPostCreateResponse();
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public RestResult<OrganisationResource> saveResource(@RequestBody OrganisationResource organisationResource) {
        return organisationService.update(organisationResource).toPutWithBodyResponse();
    }

    @RequestMapping(value = "/updateNameAndRegistration/{organisationId}", method = RequestMethod.POST)
    public RestResult<OrganisationResource> updateNameAndRegistration(@PathVariable("organisationId") Long organisationId, @RequestParam(value = "name") String name, @RequestParam(value = "registration") String registration) {
        return organisationService.updateOrganisationNameAndRegistration(organisationId, name, registration).toPostCreateResponse();
    }

    // TODO DW - INFUND-1555 - do we want to be returning an OrganisationResource from this call?
    @RequestMapping(value = "/addAddress/{organisationId}", method = RequestMethod.POST)
    public RestResult<OrganisationResource> addAddress(@PathVariable("organisationId") final Long organisationId, @RequestParam("addressType") final OrganisationAddressType addressType, @RequestBody AddressResource address) {
        return organisationService.addAddress(organisationId, addressType, address).toPutWithBodyResponse();
    }
}
