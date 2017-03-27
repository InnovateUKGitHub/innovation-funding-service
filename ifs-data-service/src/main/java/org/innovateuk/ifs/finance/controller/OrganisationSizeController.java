package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.innovateuk.ifs.finance.transactional.OrganisationSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This RestController exposes CRUD operations to both the
 * {@link OrganisationSizeResource} and other REST-API users
 * to manage {@link org.innovateuk.ifs.finance.domain.OrganisationSize} related data.
 */
@RestController
@RequestMapping("/organisation-size")
public class OrganisationSizeController {

    @Autowired
    private OrganisationSizeService organisationSizeService;

    @GetMapping
    public RestResult<List<OrganisationSizeResource>> getOrganisationSizes() {
        return organisationSizeService.getOrganisationSizes().toGetResponse();
    }
    
}
