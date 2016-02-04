package com.worth.ifs.organisation.controller;

import com.worth.ifs.organisation.mapper.AddressMapper;
import com.worth.ifs.organisation.resource.AddressResource;
import com.worth.ifs.organisation.transactional.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService service;

    @Autowired
    private AddressMapper mapper;

    @RequestMapping("/{id}")
    public AddressResource findById(@PathVariable("id") final Long id) {
        return mapper.mapAddressToResource(service.findOne(id));
    }
}