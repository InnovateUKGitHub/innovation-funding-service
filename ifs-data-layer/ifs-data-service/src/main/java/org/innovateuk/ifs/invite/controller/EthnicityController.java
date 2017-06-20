package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.transactional.EthnicityService;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes CRUD operations through a REST API to manage {@link org.innovateuk.ifs.user.domain.Ethnicity} related data.
 */
@RestController
@RequestMapping("/ethnicity")
public class EthnicityController {

    @Autowired
    private EthnicityService ethnicityService;

    @GetMapping("/findAllActive")
    public RestResult<List<EthnicityResource>> findAllActive() {
        return ethnicityService.findAllActive().toGetResponse();
    }
}
