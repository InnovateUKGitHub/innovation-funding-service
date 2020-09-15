package org.innovateuk.ifs.address.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.transactional.AddressLookupService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  Controller for the addresses for retrieving existing addresses but also for finding
 *  ones by postcode or address.
 */
@RestController
@RequestMapping("/address")
public class AddressController {


    @Autowired
    private AddressLookupService addressLookupService;

    @GetMapping("/do-lookup")
    public RestResult<List<AddressResource>> doLookup(@RequestParam(name="lookup", defaultValue="") final String lookup) {
        return addressLookupService.doLookup(lookup).toGetResponse();
    }

    @GetMapping("/validate-postcode")
    public RestResult<Boolean> validatePostcode(@RequestParam(name="postcode", defaultValue="") final String postcode) {
        return addressLookupService.validatePostcode(postcode).toGetResponse();
    }
}
