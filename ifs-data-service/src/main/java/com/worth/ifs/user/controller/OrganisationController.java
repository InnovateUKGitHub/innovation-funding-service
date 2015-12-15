package com.worth.ifs.user.controller;

import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.repository.AddressRepository;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.domain.AddressType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.resource.OrganisationResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.user.service.OrganisationRestServiceImpl} and other REST-API users
 * to manage {@link Organisation} related data.
 */
@RestController
@RequestMapping("/organisation")
public class OrganisationController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    OrganisationRepository organisationRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    ProcessRoleRepository processRoleRepository;

    @RequestMapping("/findByApplicationId/{applicationId}")
    public Set<Organisation> findByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);
        Set<Organisation> organisations = roles.stream().map(role -> organisationRepository.findByProcessRoles(role)).collect(Collectors.toCollection(LinkedHashSet::new));

        return organisations;
    }

    @RequestMapping("/findById/{organisationId}")
    public Organisation findById(@PathVariable("organisationId") final Long organisationId) {

        return organisationRepository.findOne(organisationId);
    }

    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public OrganisationResource create(@RequestBody Organisation organisation){
        log.info("OrganisationController , create method");
        log.info("OrganisationController , create method "+organisation.getName());
        organisation = organisationRepository.save(organisation);
        return new OrganisationResource(organisation);
    }

    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    @RequestMapping(value = "/addAddress/{organisationId}", method = RequestMethod.POST)
    public OrganisationResource addAddress(@PathVariable("organisationId") final Long organisationId, @RequestParam("addressType") final AddressType addressType, @RequestBody Address address){
        log.info("OrganisationController , add address");
        log.info("OrganisationController , add address2 "+ organisationId);
        log.info("OrganisationController , add addresstype "+ addressType.name());
        log.info("OrganisationController , add getAddressLine1 "+ address.getAddressLine1());
        address = addressRepository.save(address);
        Organisation organisation = organisationRepository.findOne(organisationId);
        log.info("existing addresses: " + organisation.getAddresses().size());
        organisation.addAddress(address, addressType);
        organisation = organisationRepository.save(organisation);
        return new OrganisationResource(organisation);
    }
}
