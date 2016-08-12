package com.worth.ifs.address.controller;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.transactional.AddressLookupService;
import com.worth.ifs.address.transactional.AddressService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
    private AddressService addressService;

    @Autowired
    private AddressLookupService addressLookupService;

    @RequestMapping("/doLookup")
    public RestResult<List<AddressResource>> doLookup(@RequestParam(name="lookup", defaultValue="") final String lookup) {
        return addressLookupService.doLookup(lookup).toGetResponse();
    }

    @RequestMapping("/validatePostcode")
    public RestResult<Boolean> validatePostcode(@RequestParam(name="postcode", defaultValue="") final String postcode) {
        return addressLookupService.validatePostcode(postcode).toGetResponse();
    }

    @RequestMapping("/{id}")
    public RestResult<AddressResource> getById(@PathVariable("id") final Long id){
        return addressService.getById(id).toGetResponse();
    }
}