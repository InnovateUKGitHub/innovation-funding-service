package org.innovateuk.ifs.heukar.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.heukar.transactional.HeukarOrganisationTypeService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/heukar-organisation-type")
public class HeukarOrganisationTypeController {

    @Autowired
    private HeukarOrganisationTypeService heukarOrganisationTypeService;

    @GetMapping("/find-by-application-id/{applicationId}")
    public RestResult<Set<OrganisationTypeResource>> findByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        return heukarOrganisationTypeService.findByApplicationId(applicationId).toGetResponse();
    }

}
