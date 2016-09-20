package com.worth.ifs.invite.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.transactional.EthnicityService;
import com.worth.ifs.user.resource.EthnicityResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes CRUD operations through a REST API to manage {@link com.worth.ifs.user.domain.Ethnicity} related data.
 */
@RestController
@RequestMapping("/ethnicity")
public class EthnicityController {

    @Autowired
    private EthnicityService ethnicityService;

    @RequestMapping(value = "/findAllActive", method = RequestMethod.GET)
    public RestResult<List<EthnicityResource>> findAllActive() {
        return ethnicityService.findAllActive().toGetResponse();
    }
}