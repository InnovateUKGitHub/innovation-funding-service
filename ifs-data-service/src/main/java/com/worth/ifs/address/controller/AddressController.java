package com.worth.ifs.address.controller;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.transactional.AddressLookupService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping("/doLookup/{lookup}")
    public RestResult<List<AddressResource>> doLookup(@PathVariable("lookup") final String lookup) {
        return addressLookupService.doLookup(lookup).toGetResponse();
    }

    @RequestMapping("/validatePostcode/{postcode}")
    public RestResult<Boolean> validatePostcode(@PathVariable("postcode") final String postcode) {
        return addressLookupService.validatePostcode(postcode).toGetResponse();
    }
}