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
        LOG.debug("OrganisationController , create method");
        LOG.debug("OrganisationController , create method " + organisation.getName());

        return organisationService.create(organisation).toPostCreateResponse();
    }

    @RequestMapping(value = "/saveResource", method = RequestMethod.POST)
    public RestResult<OrganisationResource> saveResource(@RequestBody OrganisationResource organisationResource) {
        LOG.debug("OrganisationController , create method");

        return organisationService.saveResource(organisationResource).toPostCreateResponse();
    }

    // TODO DW - INFUND-1555 - do we want to be returning an OrganisationResource from this call?
    @RequestMapping(value = "/addAddress/{organisationId}", method = RequestMethod.POST)
    public RestResult<OrganisationResource> addAddress(@PathVariable("organisationId") final Long organisationId, @RequestParam("addressType") final AddressType addressType, @RequestBody Address address) {
        LOG.info("OrganisationController , add address");
        LOG.info("OrganisationController , add address2 " + organisationId);
        LOG.info("OrganisationController , add addresstype " + addressType.name());
        LOG.info("OrganisationController , add getAddressLine1 " + address.getAddressLine1());

        return organisationService.addAddress(organisationId, addressType, address).toPutWithBodyResponse();
    }
}
