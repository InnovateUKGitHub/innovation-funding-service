package com.worth.ifs.user.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.RestResultBuilder;
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

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;
import static com.worth.ifs.commons.rest.RestSuccesses.createdRestSuccess;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.user.service.OrganisationRestServiceImpl} and other REST-API users
 * to manage {@link Organisation} related data.
 */
@RestController
@RequestMapping("/organisation")
public class OrganisationController {

    private final Log LOG = LogFactory.getLog(getClass());

    @Autowired
    private OrganisationService organisationService;

    @RequestMapping("/findByApplicationId/{applicationId}")
    public RestResult<Set<Organisation>> findByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        RestResultBuilder<Set<Organisation>, Set<Organisation>> handler = newRestHandler();
        return handler.perform(() -> organisationService.findByApplicationId(applicationId));
    }

    @RequestMapping("/findById/{organisationId}")
    public RestResult<Organisation> findById(@PathVariable("organisationId") final Long organisationId) {
        return newRestHandler(Organisation.class).perform(() -> organisationService.findById(organisationId));
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public RestResult<OrganisationResource> create(@RequestBody Organisation organisation) {
        LOG.debug("OrganisationController , create method");
        LOG.debug("OrganisationController , create method " + organisation.getName());

        return newRestHandler(OrganisationResource.class).
                andOnSuccess(org -> createdRestSuccess(org)).
                perform(() -> organisationService.create(organisation));
    }

    @RequestMapping(value = "/saveResource", method = RequestMethod.POST)
    public RestResult<OrganisationResource> saveResource(@RequestBody OrganisationResource organisationResource) {
        LOG.debug("OrganisationController , create method");

        // TODO DW - INFUND-1555 - assuming that even though the method is POST, it's actually an update as opposed to a create...
        return newRestHandler(OrganisationResource.class).perform(() -> organisationService.saveResource(organisationResource));
    }

    // TODO DW - INFUND-1555 - do we want to be returning an OrganisationResource from this call?
    @RequestMapping(value = "/addAddress/{organisationId}", method = RequestMethod.POST)
    public RestResult<OrganisationResource> addAddress(@PathVariable("organisationId") final Long organisationId, @RequestParam("addressType") final AddressType addressType, @RequestBody Address address) {
        LOG.info("OrganisationController , add address");
        LOG.info("OrganisationController , add address2 " + organisationId);
        LOG.info("OrganisationController , add addresstype " + addressType.name());
        LOG.info("OrganisationController , add getAddressLine1 " + address.getAddressLine1());

        return newRestHandler(OrganisationResource.class).
                andOnSuccess(org -> createdRestSuccess(org)).
                perform(() -> organisationService.addAddress(organisationId, addressType, address));
    }
}
